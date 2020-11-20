package pl.lodz.pas.librarianwebapp.services;

import pl.lodz.pas.librarianwebapp.model.repositories.user.UsersRepository;
import pl.lodz.pas.librarianwebapp.services.data.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

@Named("usersService")
public class UsersService {

    @Inject
    private UsersRepository repository;

    public List<User> getAllUsers() {
        return repository.findAllUsers()
                .stream()
                .map(userDto -> new User(
                        userDto.getLogin(),
                        userDto.getFirstName(),
                        userDto.getLastName(),
                        userDto.getEmail(),
                        userDto.isActive()
                )).collect(Collectors.toList());
    }
}
