package pl.lodz.pas.librarianwebapp.model.repositories.user;

import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {

    Optional<User> findUserByLogin(String login);

    void addUser(User user) throws ObjectAlreadyExistsException;

    void updateUser(User user) throws ObjectNotFoundException;

    List<User> findAllUsers();
}
