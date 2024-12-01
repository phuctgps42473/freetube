package api;

import com.google.gson.Gson;
import daos.AccountDAO;
import entities.Account;
import entities.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.XJsonErrorBody;
import utils.XPassword;

import java.io.IOException;

@WebServlet("/api/sign-up")
public class SignUpHandler extends HttpServlet {
    private static final AccountDAO accountDAO = new AccountDAO();
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        SignUpForm form = gson.fromJson(req.getReader(), SignUpForm.class);
        if (accountDAO.emailAndUsernameExists(form.getEmail(), form.getUsername())) {
            res.setStatus(HttpServletResponse.SC_CONFLICT);
            res.getWriter().write(XJsonErrorBody.message("Account with this email already exists"));
        } else {
            Account account = new Account(form.getEmail(), XPassword.hashPassword(form.getPassword()), form.getUsername(), Role.createUserRole());
            accountDAO.createAccount(account);
            res.setStatus(HttpServletResponse.SC_CREATED);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        res.getWriter().write(XJsonErrorBody.notAllowed());
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        res.getWriter().write(XJsonErrorBody.notAllowed());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        res.getWriter().write(XJsonErrorBody.notAllowed());
    }



    class SignUpForm {
        private final String email, username, password;

        public SignUpForm(String email, String username, String password) {
            this.email = email;
            this.username = username;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}

