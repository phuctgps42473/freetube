package api;

import daos.AccountDAO;
import daos.LikeDAO;
import daos.VideoMetaDAO;
import entities.Account;
import entities.Like;
import entities.VideoMeta;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.XGson;
import utils.XJsonErrorBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/likes")
public class LikeHandler extends HttpServlet {
    private AccountDAO accountDAO;
    private VideoMetaDAO videoMetaDAO;
    private LikeDAO likeDAO;

    @Override
    public void init() throws ServletException {
        accountDAO = new AccountDAO();
        videoMetaDAO = new VideoMetaDAO();
        likeDAO = new LikeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        long accountId = (long) req.getAttribute("accountId");
        long videoId = Long.parseLong(req.getParameter("videoId"));

        Like like = likeDAO.findLikeByAccountIdAndVideoId(accountId, videoId);

        Map<String, String> map = new HashMap<>();
        if (like != null) {
            map.put("isLiked", "true");
        } else {
            map.put("isLiked", "false");
        }
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(XGson.createGson(false).toJson(map));
        ;

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        long accountId = (long) req.getAttribute("accountId");
        LikeForm form = XGson.createGson(false).fromJson(req.getReader(), LikeForm.class);

        if (accountId != form.accountId) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write(XJsonErrorBody.message("Account does not match"));
            return;
        }

        Like like = likeDAO.findLikeByAccountIdAndVideoId(accountId, form.videoId);

        if (like != null) {
            likeDAO.deleteLike(like.getId());
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write("{\"isLiked\": \"false\"}");
        } else {
            Account ac = accountDAO.findAccountById(accountId);
            VideoMeta videoMeta = videoMetaDAO.findById(form.videoId);

            like = new Like(ac, videoMeta);
            likeDAO.createLike(like);
            res.setStatus(HttpServletResponse.SC_CREATED);
            res.getWriter().write("{\"isLiked\": \"true\"}");
        }
    }

    class LikeForm {
        long accountId, videoId;
    }
}
