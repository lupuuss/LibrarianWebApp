package pl.lodz.pas.librarianwebapp.producer;

import pl.lodz.pas.librarianwebapp.model.repositories.books.BooksRepository;
import pl.lodz.pas.librarianwebapp.model.repositories.books.data.Book;
import pl.lodz.pas.librarianwebapp.model.repositories.books.data.BookCopy;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.RepositoryException;
import pl.lodz.pas.librarianwebapp.model.repositories.user.User;
import pl.lodz.pas.librarianwebapp.producer.annotations.BooksRepositoryInitializer;
import pl.lodz.pas.librarianwebapp.producer.annotations.UsersRepositoryInitializer;

import javax.enterprise.inject.Produces;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Producer {

    @Produces
    @UsersRepositoryInitializer
    public Consumer<List<User>> produceUsersInitializer() {
        return (users) -> {

            users.add(new User(UUID.randomUUID(), "admin", "Jan", "Kowalski","jan.kowalski@wp.pl", true));
            users.add(new User(UUID.randomUUID(), "joe", "Joe", "Doe","joe.doe@gmail.com", true));
            users.add(new User(UUID.randomUUID(), "kid", "Some", "Kid","some.kid@yeet.com", true));
        };
    }

    @Produces
    @BooksRepositoryInitializer
    public BiConsumer<Set<Book>, Set<BookCopy>> produceBooksInitializer() {

        return (books, copies) -> {
            String[] isbn = new String[] { "zaq12wsx", "qwerty12", "xsw21qaz" };

            books.add(new Book(isbn[0], "Diuna1", "Sapkowski", "ZSRR"));
            books.add(new Book(isbn[1], "Diuna2", "Morawiecki", "Fabryka Slow"));
            books.add(new Book(isbn[2], "Diuna3", "Kaczynski", "Polska Ksiazka"));

            copies.add(new BookCopy(UUID.randomUUID(), isbn[0], BookCopy.State.GOOD));
            copies.add(new BookCopy(UUID.randomUUID(), isbn[1], BookCopy.State.USED));
            copies.add(new BookCopy(UUID.randomUUID(), isbn[2], BookCopy.State.NEW));
            copies.add(new BookCopy(UUID.randomUUID(), isbn[0], BookCopy.State.NEED_REPLACEMENT));
            copies.add(new BookCopy(UUID.randomUUID(), isbn[1], BookCopy.State.GOOD));
            copies.add(new BookCopy(UUID.randomUUID(), isbn[1], BookCopy.State.USED));
            copies.add(new BookCopy(UUID.randomUUID(), isbn[2], BookCopy.State.GOOD));
        };
    }
}
