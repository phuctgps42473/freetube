package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;
import utils.XGson;
import utils.XRedis;
import utils.XToken;
import utils.XJsonErrorBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebFilter("/*")
public class AuthFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        if (req.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(req, res);
            return;
        }

        String uri = req.getRequestURI();
        if (uri.contains("/login") || uri.contains("/logout") || uri.contains("/register")) {
            filterChain.doFilter(req, res);
            return;
        }

        String authorization = req.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            if (XToken.verify(token) != null) {
                String refreshToken = null, sessionId = null;
                Cookie[] cookies = req.getCookies();
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("refresh_token")) {
                        refreshToken = cookie.getValue();
                    }

                    if (cookie.getName().equals("ssid")) {
                        sessionId = cookie.getValue();
                    }
                }

                if (sessionId != null) {
                    try (Jedis jedis = XRedis.getPoolResource()) {
                        if (refreshToken != null && XToken.verify(refreshToken) != null) {
                            long ttl = jedis.ttl(sessionId);
                            if (ttl <= 60 * 30) {
                                jedis.expire(sessionId, 60 * 60 * 3);
                            }
                        }

                        long accountId = Long.parseLong(jedis.hget(sessionId, "account_id"));
                        String accountRole = jedis.hget(sessionId, "role");
                        req.setAttribute("accountId", accountId);
                        req.setAttribute("accountRole", accountRole);
                        filterChain.doFilter(req, res);
                        return;
                    }
                } else {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
        }


        String refreshToken = null, sessionId = null;
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh_token")) {
                refreshToken = cookie.getValue();
            }

            if (cookie.getName().equals("ssid")) {
                sessionId = cookie.getValue();
            }
        }

        if (sessionId == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write(XJsonErrorBody.message("Require login"));
            return;
        }


        try (Jedis jedis = XRedis.getPoolResource()) {
            Map<String, String> accountData = jedis.hgetAll(sessionId);
            if (accountData == null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().write(XJsonErrorBody.message("Require login"));
                return;
            }

            if (refreshToken != null && XToken.verify(refreshToken) != null) {
                long ttl = jedis.ttl(sessionId);
                if (ttl <= 60 * 30) {
                    jedis.expire(sessionId, 60 * 60 * 3);
                }
                long accountId = Long.parseLong(jedis.hget(sessionId, "account_id"));
                Map<String, String> tokenMap = new HashMap<>();
                tokenMap.put("account_id", String.valueOf(accountId));
                String newAccessToken = XToken.create(XGson.createGson(false).toJson(tokenMap), 15);
                res.setHeader("Authorization", "Bearer " + newAccessToken);
            }

            long accountId = Long.parseLong(jedis.hget(sessionId, "account_id"));
            String accountRole = jedis.hget(sessionId, "role");

            req.setAttribute("accountId", accountId);
            req.setAttribute("accountRole", accountRole);
            filterChain.doFilter(req, res);

        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
