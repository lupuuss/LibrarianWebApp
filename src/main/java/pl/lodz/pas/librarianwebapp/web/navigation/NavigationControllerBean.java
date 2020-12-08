package pl.lodz.pas.librarianwebapp.web.navigation;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@SessionScoped
@Named("navigationController")
public class NavigationControllerBean implements Serializable {

    @Inject
    private PagesTree pagesTree;

    public List<PageNode> getBreadcrumbs() {
        var currentLocation = FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getRequestServletPath();

        var params = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();

        return pagesTree.getBreadcrumbs(currentLocation, params);
    }


    public String getUserName() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

    public void logout() {

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        try {
            ec.redirect(ec.getRequestContextPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
