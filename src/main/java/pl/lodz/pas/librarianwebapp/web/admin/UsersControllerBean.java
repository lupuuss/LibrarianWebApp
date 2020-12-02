package pl.lodz.pas.librarianwebapp.web.admin;

import pl.lodz.pas.librarianwebapp.services.UsersService;
import pl.lodz.pas.librarianwebapp.services.dto.UserDto;
import pl.lodz.pas.librarianwebapp.web.MarksController;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("usersController")
@RequestScoped
public class UsersControllerBean extends MarksController<UserDto> {

    @Inject
    private UsersService service;

    private String query;

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

        return "users.xhtml?faces-redirect=true";
    }

    public String activateMarkedUsers() {
        service.updateUsersActive(getMarkedItems(), true);

        return "users.xhtml?faces-redirect=true";
    }

    public String filter() {
        return "users.xhtml?faces-redirect=true&query=" + query;
    }
}
