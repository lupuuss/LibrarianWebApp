package pl.lodz.pas.librarianwebapp.web.admin;

import pl.lodz.pas.librarianwebapp.services.UsersService;
import pl.lodz.pas.librarianwebapp.services.dto.UserDto;
import pl.lodz.pas.librarianwebapp.web.MarksController;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named("usersListController")
@RequestScoped
public class UsersListControllerBean extends MarksController<UserDto> {

    @Inject
    private UsersService service;

    private String query = "";

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<UserDto> getAllUsers() {

        return service.getUsersByLoginContains(query);
    }

    public String deactivateMarkedUsers() {

        service.updateUsersActive(getMarkedItems(), false);

        return "usersList.xhtml?faces-redirect=true&query=" + query;
    }

    public String activateMarkedUsers() {
        service.updateUsersActive(getMarkedItems(), true);

        return "usersList.xhtml?faces-redirect=true&query=" + query;
    }

    public String filter() {
        return "usersList.xhtml?faces-redirect=true&query=" + query;
    }
}
