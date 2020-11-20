package pl.lodz.pas.librarianwebapp.model.repositories.user;

import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.DtoAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.DtoNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {

    Optional<UserDto> findUserByLogin(String login);

    void addUser(UserDto user) throws DtoAlreadyExistsException;

    void updateUser(UserDto user) throws DtoNotFoundException;

    List<UserDto> findAllUsers();
}
