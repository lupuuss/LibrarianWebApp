package pl.lodz.pas.librarianwebapp.web.admin;

import pl.lodz.pas.librarianwebapp.services.UsersService;
import pl.lodz.pas.librarianwebapp.services.dto.UserDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named("newPersonController")
@RequestScoped
public class NewUserControllerBean {

    @Inject
    UsersService service;

    private UserDto user = new UserDto();

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String createUser() {

        var result = service.addUser(user);

        if (result) {
            return "users.xhtml?faces-redirect=true";
        } else {
            return "userCreationFailed.xhtml?faces-redirect=true";
        }
    }
}
