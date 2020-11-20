package pl.lodz.pas.librarianwebapp.model.repositories.user;

import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.DtoAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.DtoNotFoundException;
import pl.lodz.pas.librarianwebapp.producer.annotations.UsersRepositoryInitializer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@ApplicationScoped
public class LocalUsersRepository implements UsersRepository {

    @Inject
    @UsersRepositoryInitializer
    private Consumer<List<User>> usersInitializer;

    private final List<User> users;

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
    public Optional<User> findUserByLogin(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst()
                .map(User::copy);
    }

    @Override
    public void addUser(User user) throws DtoAlreadyExistsException {

        var inBaseUser = findUserByLogin(user.getLogin());

        if (inBaseUser.isPresent() &&
                inBaseUser.get().getLogin().equals(user.getLogin())) {
            throw new DtoAlreadyExistsException(User.class.getSimpleName(), user.getLogin());
        }

        users.add(user.copy());

        System.out.println(users);
    }

    @Override
    public void updateUser(User updatedUser) throws DtoNotFoundException {
        var inBaseUser = findUserByLogin(updatedUser.getLogin());

        if (inBaseUser.isEmpty()) {
            throw new DtoNotFoundException(User.class.getSimpleName(), updatedUser.getLogin());
        }

        inBaseUser.ifPresent(user -> {
            users.removeIf(u -> u.getLogin().equals(user.getLogin()));
            users.add(updatedUser.copy());
        });
    }

    @Override
    public List<User> findAllUsers() {
        return users.stream()
                .map(User::copy)
                .collect(Collectors.toList());
    }
}
