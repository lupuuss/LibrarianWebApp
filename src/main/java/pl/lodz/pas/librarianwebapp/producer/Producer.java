package pl.lodz.pas.librarianwebapp.producer;

import pl.lodz.pas.librarianwebapp.model.repositories.user.UserDto;
import pl.lodz.pas.librarianwebapp.producer.annotations.UsersRepositoryInitializer;

import javax.enterprise.inject.Produces;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Producer {

    @Produces
    @UsersRepositoryInitializer
    public Consumer<List<UserDto>> produceUsersInitializer() {
        return (users) -> {

            users.add(new UserDto(UUID.randomUUID(), "admin", "Jan", "Kowalski","jan.kowalski@wp.pl", true));
            users.add(new UserDto(UUID.randomUUID(), "joe", "Joe", "Doe","joe.doe@gmail.com", true));
            users.add(new UserDto(UUID.randomUUID(), "kid", "Some", "Kid","some.kid@yeet.com", true));
        };
    }
}
