package pl.lodz.pas.librarianwebapp.repository.user;

import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianwebapp.services.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {

    Optional<User> findUserByLogin(String login);

    void addUser(User user) throws ObjectAlreadyExistsException;

    void updateUser(User user) throws ObjectNotFoundException;

    List<User> findAllUsers();

    List<User> findUserByLoginContains(String query);

}
