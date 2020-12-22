package pl.lodz.pas.librarianwebapp.web;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@RequestScoped
@Named("indexController")
public class IndexControllerBean {

    public void redirectByRole() {
        var context = FacesContext.getCurrentInstance().getExternalContext();

        try {

            if (context.isUserInRole("ADMIN")) {

                context.redirect("admin/usersList.xhtml");

            } else if (context.isUserInRole("EMPLOYEE")) {

                context.redirect("employee/elementsList.xhtml");

            } else if (context.isUserInRole("USER")) {

                context.redirect("lendings/elements.xhtml");
            } else {

                context.redirect("auth/login.xhtml");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
