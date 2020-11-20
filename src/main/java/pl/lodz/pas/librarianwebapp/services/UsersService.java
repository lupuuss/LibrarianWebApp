package pl.lodz.pas.librarianwebapp.services;

import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.DtoAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.model.repositories.user.User;
import pl.lodz.pas.librarianwebapp.model.repositories.user.UsersRepository;
import pl.lodz.pas.librarianwebapp.services.dto.UserDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestScoped
public class UsersService {

    @Inject
    private UsersRepository repository;

    public List<UserDto> getAllUsers() {
        return repository.findAllUsers()
                .stream()
                .map(userDto -> new UserDto(
                        userDto.getLogin(),
                        userDto.getFirstName(),
                        userDto.getLastName(),
                        userDto.getEmail(),
                        userDto.isActive()
                )).collect(Collectors.toList());
    }

    public boolean addUser(UserDto user) {

        var newUser = new User(
                UUID.randomUUID(),
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.isActive()
        );

        try {
            repository.addUser(newUser);
            return true;
        } catch (DtoAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        }
    }
}
