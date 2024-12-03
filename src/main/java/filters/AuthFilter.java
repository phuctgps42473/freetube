package filters;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.XToken;
import utils.XJsonErrorBody;

import java.io.IOException;
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

        if (1 == 1) {
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

            DecodedJWT decodedJWT = XToken.verify(token);

            if (decodedJWT != null) {
                Map<String, Claim> claims = decodedJWT.getClaims();

                long userId = claims.get("user_id").asLong();
                req.setAttribute("userId", userId);
                filterChain.doFilter(req, res);
            }

        } else {
            String refreshToken = null;
            Cookie[] cookies = req.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh_token")) {
                    refreshToken = cookie.getValue();

                    // ADD LOGIC FOR RENEW ACCESS TOKEN

                }
            }

            if (refreshToken == null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().write(XJsonErrorBody.message("Require login"));
            }
        }
        filterChain.doFilter(req, res);

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
