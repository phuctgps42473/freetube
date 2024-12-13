package api;

import daos.AccountDAO;
import daos.CommentDAO;
import daos.VideoMetaDAO;
import entities.Account;
import entities.Comment;
import entities.VideoMeta;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.XGson;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/comments/*")
public class CommentHandler extends HttpServlet {
    private static VideoMetaDAO videoMetaDAO;
    private static AccountDAO accountDAO;
    private static CommentDAO commentDAO;

    @Override
    public void init() {
        accountDAO = new AccountDAO();
        commentDAO = new CommentDAO();
        videoMetaDAO = new VideoMetaDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        if (req.getAttribute("accountId") == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        long accountId = (long) req.getAttribute("accountId");

        try {
            CommentForm cmt = XGson.createGson(false).fromJson(req.getReader(), CommentForm.class);

            if (accountId != cmt.accountId) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            VideoMeta video = videoMetaDAO.findById(cmt.videoId);
            Account account = accountDAO.findAccountById(cmt.accountId);

            if (video == null || account == null) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Comment comment = new Comment(cmt.content, account, video);
            commentDAO.saveComment(comment);

            res.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String id = req.getParameter("video-id");
        if (id == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        long videoId = Long.parseLong(id);
        VideoMeta video = videoMetaDAO.findById(videoId);
        if (video == null) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<CommentResponse> body = video.getComment().stream().map(CommentResponse::fromComment).collect(Collectors.toList());

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(XGson.createGson(false).toJson(body));
    }

    static class CommentResponse {
        long accountId;
        String username, profileImage, content;
        public static CommentResponse fromComment(Comment comment) {
            CommentResponse response = new CommentResponse();
            response.accountId = comment.getAccount().getId();
            response.username = comment.getAccount().getUsername();
            response.profileImage = comment.getAccount().getProfileImage();
            response.content = comment.getContent();
            return response;
        }
    }

    static class CommentForm {
        String content;
        long accountId, videoId;

        public CommentForm(String content, long accountId, long videoId) {
            this.content = content;
            this.accountId = accountId;
            this.videoId = videoId;
        }
    }
}
