package api;

import daos.AccountDAO;
import entities.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.XGson;


@WebServlet("/api/accounts")
public class AccountController extends HttpServlet {
    private static AccountDAO accountDAO;

    @Override
    public void init() throws ServletException {
        accountDAO = new AccountDAO();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) {
        if (req.getAttribute("accountId") == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            UpdateForm form = XGson.createGson(false).fromJson(req.getReader(), UpdateForm.class);

            long accountId = (long) req.getAttribute("accountId");
            Account account = accountDAO.findAccountById(accountId);

            if (account == null) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            account.setBio(form.bio);
            account.setAddress(form.address);

            accountDAO.update(account);

            res.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    static class UpdateForm {
        String bio, address;

        public UpdateForm(String bio, String address) {
            this.bio = bio;
            this.address = address;
        }

    }

    //TODO
    // UPDATE ACCOUNT
    // FETCH VIDEO
    // UPDATE VIDEO

}
