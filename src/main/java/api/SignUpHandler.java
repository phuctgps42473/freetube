package api;

import com.google.gson.Gson;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.XJPA;

import java.io.IOException;

@WebServlet("/api/sign-up")
public class SignUpHandler extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        EntityManager en = XJPA.createEntityManager();
        res.getWriter().write(gson.toJson(new SignUpForm("hello@gmail.com", "frank231", "Cacacaca")));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        SignUpForm form = gson.fromJson(req.getReader(), SignUpForm.class);
        System.out.println(form.getEmail());
        System.out.println(form.getPassword());
        System.out.println(form.getUsername());
        res.getWriter().write(gson.toJson(form));
    }
}

class SignUpForm {
    private String email, username, password;

    public SignUpForm(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
