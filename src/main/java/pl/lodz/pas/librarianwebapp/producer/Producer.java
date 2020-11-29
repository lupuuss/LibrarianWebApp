package pl.lodz.pas.librarianwebapp.producer;

import pl.lodz.pas.librarianwebapp.producer.annotations.MagazinesRepositoryInitializer;
import pl.lodz.pas.librarianwebapp.repository.books.data.*;
import pl.lodz.pas.librarianwebapp.repository.user.User;
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
            UUID[] uuids = new UUID[] { UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID() };
            String[] isbn = new String[] { "zaq12wsx", "qwerty12", "xsw21qaz" };

            books.add(new Book(uuids[0], isbn[0], "Diuna1", "Sapkowski", "ZSRR"));
            books.add(new Book(uuids[1], isbn[1], "Diuna2", "Morawiecki", "Fabryka Slow"));
            books.add(new Book(uuids[2], isbn[2], "Diuna3", "Kaczynski", "Polska Ksiazka"));

            copies.add(new BookCopy(UUID.randomUUID(), uuids[0],0, BookCopy.State.GOOD));
            copies.add(new BookCopy(UUID.randomUUID(), uuids[1],0, BookCopy.State.USED));
            copies.add(new BookCopy(UUID.randomUUID(), uuids[2],0, BookCopy.State.NEW));
            copies.add(new BookCopy(UUID.randomUUID(), uuids[0],1, BookCopy.State.NEED_REPLACEMENT));
            copies.add(new BookCopy(UUID.randomUUID(), uuids[1],1, BookCopy.State.GOOD));
            copies.add(new BookCopy(UUID.randomUUID(), uuids[1],2, BookCopy.State.USED));
            copies.add(new BookCopy(UUID.randomUUID(), uuids[2],1, BookCopy.State.GOOD));
        };
    }


    @Produces
    @MagazinesRepositoryInitializer
    public BiConsumer<Set<Magazine>, Set<MagazineCopy>> produceMagazinesInitializer(){
        return ((magazines, magazineCopies) -> {
            UUID[] elementUuids = new UUID[] { UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID() , UUID.randomUUID(), UUID.randomUUID() };
            UUID[] uuids = new UUID[] {
                    UUID.randomUUID(), UUID.randomUUID(),
                    UUID.randomUUID() , UUID.randomUUID(),
                    UUID.randomUUID(), UUID.randomUUID()
            };
            String[] issn = new String[] { "mju76yhn", "nhy67ujm", "asdfghjk","kjhgfdsa" };
            Integer[] issue = new Integer[] {1,2,3,4,5,6};

            magazines.add(new Magazine(elementUuids[0],"Fnatic","Fraszki",issn[0],3));
            magazines.add(new Magazine(elementUuids[1],"Gamers2","Sport",issn[1],2));
            magazines.add(new Magazine(elementUuids[2],"Fnatic","Fraszki",issn[0],2));
            magazines.add(new Magazine(elementUuids[3],"Republika Konga","Polska Niepodległa",issn[2],1));
            magazines.add(new Magazine(elementUuids[4],"Nickelodeon","Fakt",issn[3],2));

            magazineCopies.add(new MagazineCopy(uuids[0],elementUuids[0], 0, ElementCopy.State.NEW));
            magazineCopies.add(new MagazineCopy(uuids[1],elementUuids[1], 2, ElementCopy.State.GOOD));
            magazineCopies.add(new MagazineCopy(uuids[2],elementUuids[4], 3, ElementCopy.State.GOOD));
            magazineCopies.add(new MagazineCopy(uuids[3],elementUuids[3], 1, ElementCopy.State.GOOD));
            magazineCopies.add(new MagazineCopy(uuids[4],elementUuids[2], 1, ElementCopy.State.NEED_REPLACEMENT));
            magazineCopies.add(new MagazineCopy(uuids[5],elementUuids[1], 1, ElementCopy.State.NEW));
        });

    }
}
