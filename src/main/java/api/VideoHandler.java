package api;

import daos.AccountDAO;
import daos.VideoMetaDAO;
import entities.Account;
import entities.VideoMeta;
import io.minio.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;
import utils.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/videos")
public class VideoHandler extends HttpServlet {
    private static AccountDAO accountDAO;
    private static VideoMetaDAO videoMetaDAO;
    private static MinioClient minioClient;

    @Override
    public void init() {
        accountDAO = new AccountDAO();
        videoMetaDAO = new VideoMetaDAO();
        minioClient = MinioClient
                .builder()
                .endpoint(XSecret.getSecret("MINIO_ENDPOINT"))
                .credentials(XSecret.getSecret("MINIO_ACCESS_KEY"), XSecret.getSecret("MINIO_SECRET_KEY"))
                .build();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            String bucket = "hello";
            String videoName = "RICA.mp4";

            String range = req.getHeader("Range");

            StatObjectArgs statBuilder = StatObjectArgs.builder().bucket(bucket).object(videoName).build();
            StatObjectResponse stats = minioClient.statObject(statBuilder);

            long fileSize = stats.size() - 1;
            long start = 0;
            long end = fileSize - 1;

            String[] ranges = range.replace("bytes=", "").split("-");
            if (!ranges[0].isEmpty()) {
                start = Long.parseLong(ranges[0]);
            }
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                end = Long.parseLong(ranges[1]);
            }

            if (start > end || end >= fileSize) {
                res.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                res.setHeader("Content-Range", "bytes */" + fileSize);
                return;
            }

            long contentLength = end - start + 1;

            res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            res.setHeader("Content-Type", "video/mp4");
            res.setHeader("Accept-Ranges", "bytes");
            res.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
            res.setContentLengthLong(contentLength);

            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(videoName)
                            .offset(start)
                            .length(contentLength)
                            .build());
                 OutputStream outputStream = res.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            res.getWriter().write(XJsonErrorBody.message("LOI ROI"));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) {
        res.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String contentType = req.getHeader("Content-Type");
        if (contentType.equals("application/json")) {
            handleSaveVideoMeta(req, res);
        } else if (contentType.equals("application/octet-stream")) {
            handleUploadVideo(req, res);
        } else {
            System.out.println("NOT SUPPORTED");
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }


    private void handleSaveVideoMeta(HttpServletRequest req, HttpServletResponse res) throws IOException {
//        long userId = Long.parseLong(req.getParameter("userId"));
        long userId = 3;

        Form form = XGson.createGson(false).fromJson(req.getReader(), Form.class);

        String objectName = UUID.randomUUID().toString();

        String sessionToken = XToken.create("{\"objectName\":\"" + objectName + "\"}", 60 * 3);
        try (Jedis jedis = XRedis.getPoolResource()) {
            Map<String, String> sessionData = new HashMap<>();
            sessionData.put("title", form.title);
            sessionData.put("thumbnail", form.thumbnail);
            sessionData.put("description", form.description);
            sessionData.put("file_size", form.fileSize);
            sessionData.put("offset", "0");
            sessionData.put("object_name", objectName);
            sessionData.put("uploader_id", String.valueOf(userId));
            jedis.hset(sessionToken, sessionData);
            jedis.expire(sessionToken, 60 * 60 * 3);
        }

        res.setHeader("X-Session-Token", sessionToken);
        res.setStatus(HttpServletResponse.SC_OK);
    }


    private void handleUploadVideo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String sessionToken = req.getHeader("X-Session-Token");

        if (sessionToken == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Map<String, String> sessionData;

        try (Jedis jedis = XRedis.getPoolResource()) {
            sessionData = jedis.hgetAll(sessionToken);
            if (sessionData == null) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        String newOffsetString = req.getHeader("X-Offset");
        if (newOffsetString == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String objectName = sessionData.get("object_name");

        File file = new File(req.getServletContext().getRealPath("") + File.separator + "uploads", objectName + ".mp4");
        try (
                InputStream stream = req.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(file, true)
        ) {
            byte[] buffer = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        }

        long offset = Long.parseLong(newOffsetString);
        long fileSize = Long.parseLong(sessionData.get("file_size"));
        if (offset == fileSize) {
            try (FileInputStream videoStream = new FileInputStream(file)) {
                PutObjectArgs putObjectArgs = PutObjectArgs
                        .builder()
                        .bucket("youtube")
                        .object(objectName + ".mp4")
                        .stream(videoStream, -1, 10 * 1024 * 1024)
                        .contentType("video/mp4")
                        .build();
                minioClient.putObject(putObjectArgs);

                Account uploader = accountDAO.findAccountById(Long.parseLong(sessionData.get("uploader_id")));
                VideoMeta videoMeta = new VideoMeta(sessionData.get("title"), sessionData.get("description"), uploader, sessionData.get("thumbnail"), objectName);
                long videoId = videoMetaDAO.createVideoMeta(videoMeta);

                try (Jedis jedis = XRedis.getPoolResource()) {
                    jedis.del(sessionToken);
                }

                res.setStatus(HttpServletResponse.SC_CREATED);
                String videoLink = "http://localhost:5173/videos/" + videoId;
                res.getWriter().write("{\"videoLink\":\"" + videoLink + "\"}");

            } catch (Exception e) {
                System.out.println(e.getMessage());
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            boolean success = file.delete();
            System.out.println(success);
        }
    }

    private static class Form {
        String title, description, thumbnail, fileSize;

        public Form(String title, String description, String thumbnail, String fileSize) {
            this.title = title;
            this.description = description;
            this.thumbnail = thumbnail;
            this.fileSize = fileSize;
        }

    }
}
