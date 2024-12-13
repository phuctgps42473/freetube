package api;

import daos.AccountDAO;
import entities.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.XGson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/healthcheck")
public class HealthCheckHandler extends HttpServlet {
    private static AccountDAO accountDAO;

    @Override
    public void init() {
        accountDAO = new AccountDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Object id = req.getAttribute("accountId");
        if (id == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        long accountId = (long) id;
        Account ac = accountDAO.findAccountById(accountId);
        if (ac == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Map<String, Object> a = new HashMap<>();
        a.put("id", ac.getId());
        a.put("username", ac.getUsername());
        a.put("email", ac.getEmail());
        a.put("role", ac.getRole().getRole());
        a.put("bio", ac.getBio());
        a.put("address", ac.getAddress());

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(XGson.createGson(false).toJson(a));
    }
}
