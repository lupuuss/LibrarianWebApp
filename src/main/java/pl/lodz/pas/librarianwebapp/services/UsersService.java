package pl.lodz.pas.librarianwebapp.services;

import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianwebapp.repository.user.User;
import pl.lodz.pas.librarianwebapp.repository.user.UsersRepository;
import pl.lodz.pas.librarianwebapp.services.dto.UserDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestScoped
public class UsersService {

    @Inject
    private UsersRepository repository;

    private User.Type mapType(UserDto.Type type) {
        return User.Type.valueOf(type.name());
    }

    private UserDto.Type mapType(User.Type type) {
        return UserDto.Type.valueOf(type.name());
    }

    public List<UserDto> getAllUsers() {
        return repository.findAllUsers()
                .stream()
                .map(user -> new UserDto(
                        user.getLogin(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        mapType(user.getType()),
                        user.isActive()
                )).collect(Collectors.toList());
    }

    public boolean addUser(UserDto user) {

        var newUser = new User(
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                mapType(user.getType()), user.isActive()
        );

        try {
            repository.addUser(newUser);
            return true;
        } catch (ObjectAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateUsersActive(List<UserDto> toUpdate, boolean active) {

        for (var user : toUpdate) {
            var foundUser = repository.findUserByLogin(user.getLogin());

            foundUser.ifPresent(userToUpdate -> {
                userToUpdate.setActive(active);

                try {
                    repository.updateUser(userToUpdate);
                } catch (ObjectNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public List<UserDto> getUsersByLoginContains(String query) {

        if (query == null || query.equals("")) {
            return getAllUsers();
        }

        return repository.findUserByLoginContains(query)
                .stream()
                .map(user -> new UserDto(
                        user.getLogin(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        mapType(user.getType()),
                        user.isActive()
                ))
                .collect(Collectors.toList());
    }

    public Optional<UserDto> getUserByLogin(String loginToEdit) {
        return repository.findUserByLogin(loginToEdit)
                .map(user -> new UserDto(
                        user.getLogin(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        mapType(user.getType()),
                        user.isActive()
                ));
    }

    public boolean updateUserByLogin(UserDto userDto) {

        var optUser = repository.findUserByLogin(userDto.getLogin());

        if (optUser.isEmpty()) {
            return false;
        }

        var user = optUser.get();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setActive(userDto.isActive());
        user.setEmail(userDto.getEmail());
        user.setType(mapType(userDto.getType()));

        try {
            repository.updateUser(user);
            return true;
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
