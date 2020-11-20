package pl.lodz.pas.librarianwebapp.model.repositories.user;

import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.DtoAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.DtoNotFoundException;
import pl.lodz.pas.librarianwebapp.producer.annotations.UsersRepositoryInitializer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class LocalUsersRepository implements UsersRepository {

    @Inject
    @UsersRepositoryInitializer
    private Consumer<List<UserDto>> usersInitializer;

    private final List<UserDto> users;

    public LocalUsersRepository() {
        this.users = Collections.synchronizedList(new ArrayList<>());
    }

    @PostConstruct
    private void initializeUsers() {

        if (usersInitializer != null) {
            usersInitializer.accept(users);
        }
    }

    @Override
    public Optional<UserDto> findUserByLogin(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst()
                .map(UserDto::copy);
    }

    @Override
    public void addUser(UserDto user) throws DtoAlreadyExistsException {

        var inBaseUser = findUserByLogin(user.getLogin());

        if (inBaseUser.isPresent() &&
                inBaseUser.get().getLogin().equals(user.getLogin())) {
            throw new DtoAlreadyExistsException(UserDto.class.getSimpleName(), user.getLogin());
        }

        users.add(user.copy());
    }

    @Override
    public void updateUser(UserDto updatedUser) throws DtoNotFoundException {
        var inBaseUser = findUserByLogin(updatedUser.getLogin());

        if (inBaseUser.isEmpty()) {
            throw new DtoNotFoundException(UserDto.class.getSimpleName(), updatedUser.getLogin());
        }

        inBaseUser.ifPresent(user -> {
            users.remove(user);
            users.add(updatedUser.copy());
        });
    }

    @Override
    public List<UserDto> findAllUsers() {
        return users.stream()
                .map(UserDto::copy)
                .collect(Collectors.toList());
    }
}
