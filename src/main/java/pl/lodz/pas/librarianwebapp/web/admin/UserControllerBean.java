package pl.lodz.pas.librarianwebapp.web.admin;

import pl.lodz.pas.librarianwebapp.services.UsersService;
import pl.lodz.pas.librarianwebapp.services.dto.UserDto;
import pl.lodz.pas.librarianwebapp.web.localization.UserTypeI18n;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

@Named("userController")
@RequestScoped
public class UserControllerBean {

    @Inject
    UsersService service;

    private String loginToEdit;

    private UserDto user = new UserDto();

    public void initializeUser() {

        if (loginToEdit == null || loginToEdit.isBlank()) {
            loginToEdit = null;
            return;
        }

        var user = service.getUserByLogin(loginToEdit);

        if (user.isEmpty()) {
            loginToEdit = null;
            return;
        }

        this.user = user.get();
    }

    public String getLoginToEdit() {
        return loginToEdit;
    }

    public void setLoginToEdit(String loginToEdit) {
        this.loginToEdit = loginToEdit;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<UserTypeI18n> getTypes() {
        return Arrays.asList(UserTypeI18n.values());
    }

    public UserTypeI18n getUserType() {

        if (user == null || user.getType() == null) {
            return null;
        }

        return UserTypeI18n.from(user.getType());
    }

    public void setUserType(UserTypeI18n type) {
        user.setType(type.toUserType());
    }

    public String saveUser() {

        boolean result;

        if (loginToEdit == null) {
            result = service.addUser(user);
        } else {
            result = service.updateUserByLogin(user);
        }

        if (result) {
            return "usersList.xhtml?faces-redirect=true";
        } else {
            return "/error.xhtml?faces-redirect=true&errorId=operationFailed";
        }
    }
}
