package pl.lodz.pas.librarianwebapp.web.admin;

import pl.lodz.pas.librarianwebapp.services.UsersService;
import pl.lodz.pas.librarianwebapp.services.dto.UserDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("usersController")
@RequestScoped
public class UsersControllerBean {

    @Inject
    private UsersService service;

    private String query;

    private final Map<UserDto, Boolean> marks = new HashMap<>();

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<UserDto> getAllUsers() {

        return service.getUsersByLoginContains(query);
    }

    public Map<UserDto, Boolean> getMarks() {
        return marks;
    }

    public String deactivateMarkedUsers() {

        service.updateUsersActive(getMarkedUsers(), false);

        return "users.xhtml?faces-redirect=true";
    }

    public String activateMarkedUsers() {
        service.updateUsersActive(getMarkedUsers(), true);

        return "users.xhtml?faces-redirect=true";
    }

    public String filter() {
        return "users.xhtml?faces-redirect=true&query=" + query;
    }

    private List<UserDto> getMarkedUsers() {
        return marks
                .entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
