package pl.lodz.pas.librarianwebapp.web;

import pl.lodz.pas.librarianwebapp.services.UsersService;
import pl.lodz.pas.librarianwebapp.services.dto.UserDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named("usersController")
@RequestScoped
public class UsersControllerBean {

    @Inject
    private UsersService service;

    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }
}
