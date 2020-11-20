package pl.lodz.pas.librarianwebapp.model.repositories.user;

import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.DtoAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.DtoNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {

    Optional<User> findUserByLogin(String login);

    void addUser(User user) throws DtoAlreadyExistsException;

    void updateUser(User user) throws DtoNotFoundException;

    List<User> findAllUsers();
}
