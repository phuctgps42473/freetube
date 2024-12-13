package api.admin;

import daos.AccountDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.XGson;

import java.io.IOException;

@WebServlet("/api/admin/accounts")
public class AccountController extends HttpServlet {
    private static AccountDAO accountDAO;

    @Override
    public void init() throws ServletException {
        accountDAO = new AccountDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(XGson.createGson(true).toJson(accountDAO.findAll()));
    }

}
