package pl.lodz.pas.librarianwebapp.web.lendings;

import pl.lodz.pas.librarianwebapp.services.ElementsService;
import pl.lodz.pas.librarianwebapp.services.LendEventDto;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named("lendingsHistoryController")
@RequestScoped
public class LendingsHistoryControllerBean {

    @Inject
    private ElementsService service;

    private String getLogin() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

    public List<LendEventDto> getLendingsHistory() {
        return service.getLendingForUser(getLogin());
    }
}
