package api;

import com.google.gson.Gson;
import daos.AccountDAO;
import entities.Account;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;
import utils.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/login")
public class LoginHandler extends HttpServlet {
    private static final AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LoginForm form = new Gson().fromJson(req.getReader(), LoginForm.class);
        Account account = accountDAO.findAccountByEmailOrUsername(form.username);

        if (account == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write(XJsonErrorBody.message("Wrong username/email or password"));
            return;
        }

        if (XPassword.verifyPassword(form.password, account.getHashPassword())) {
            Gson gson = XGson.createGson(true);

            Map<String, String> accessMap = new HashMap<>();
            accessMap.put("user_id", String.valueOf(account.getId()));
            accessMap.put("role", account.getRole().getRole());
            String accessToken = XToken.create(gson.toJson(accessMap), 15);

            Map<String, String> refreshMap = new HashMap<>();
            accessMap.put("user_id", String.valueOf(account.getId()));
            String refreshToken = XToken.create(gson.toJson(refreshMap), 60 * 24 * 7);

            Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(60 * 60 * 24 * 7);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);

            try (Jedis jedis = XRedis.getPoolResource()) {
                String sessionId = UUID.randomUUID().toString();
                Map<String, Object> user = new HashMap<>();
                user.put("id", account.getId());
                user.put("username", account.getUsername());
                user.put("email", account.getEmail());
                user.put("role", account.getRole().getRole());
                jedis.set(sessionId, gson.toJson(user));
                jedis.expire(sessionId, 60 * 60 * 2);

                Cookie sessionCookie = new Cookie("ssid", sessionId);
                sessionCookie.setPath("/");
                sessionCookie.setMaxAge(60 * 60 * 2);
                sessionCookie.setHttpOnly(true);
                sessionCookie.setSecure(true);

                res.setHeader("Authorization", "Bearer " + accessToken);
                res.addCookie(refreshCookie);
                res.addCookie(sessionCookie);
            } catch (Exception e) {
                e.printStackTrace();
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.getWriter().write(XJsonErrorBody.message("We encountered some errors"));
            }

            String body = XGson.createGson(true).toJson(account);

            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write(body);
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write(XJsonErrorBody.message("Wrong username/email or password"));
        }


    }

    static class LoginForm {
        private final String username;
        private final String password;

        public LoginForm(String usernameOrEmail, String password) {
            this.username = usernameOrEmail;
            this.password = password;
        }
    }
}
