package pl.lodz.pas.librarianwebapp.web;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@RequestScoped
public class AuthController implements Serializable {

    private static final Logger logger = Logger.getLogger(AuthController.class.getSimpleName());

    private String username;
    private String password;

    @Inject
    private HttpServletRequest request;

    public String login() {

        try {

            request.login(username, password);

            logger.log(Level.INFO, "User '" + username + "' logged in successfully!");

            return "/index.xhtml?faces-redirect=true";

        } catch (ServletException e) {
            logger.log(Level.WARNING, "User '" + username + "' failed to login!");
            return "/auth/error.xhtml?faces-redirect=true";
        }
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
