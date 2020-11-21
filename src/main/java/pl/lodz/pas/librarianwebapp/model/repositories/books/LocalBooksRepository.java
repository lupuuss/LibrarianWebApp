package pl.lodz.pas.librarianwebapp.model.repositories.books;

import pl.lodz.pas.librarianwebapp.model.repositories.books.data.Book;
import pl.lodz.pas.librarianwebapp.model.repositories.books.data.BookCopy;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.RepositoryException;
import pl.lodz.pas.librarianwebapp.producer.annotations.BooksRepositoryInitializer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.function.BiConsumer;

@ApplicationScoped
public class LocalBooksRepository implements BooksRepository {

    @Inject
    @BooksRepositoryInitializer
    private BiConsumer<Set<Book>,Set<BookCopy>> booksInitializer;


    private final Set<Book> books = new HashSet<>();
    private final Set<BookCopy> bookCopies = new HashSet<>();


    @PostConstruct
    private void initializeBooks() {
        if (booksInitializer != null) {
            booksInitializer.accept(books, bookCopies);
        }
    }


    @Override
    public synchronized List<Book> findAllBooks() {
        return new ArrayList<>(books);
    }

    @Override
    public synchronized Optional<Book> findBookByIsbn(String isbn) {

        return books.stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst();
    }

    @Override
    public synchronized Map<Book, Long> countAllBooksWithNotDamagedCopies() {
        var map = new HashMap<Book, Long>();

        for (var book : books) {

            var count = bookCopies.stream()
                    .filter(copy -> copy.getBookIsbn().equals(book.getIsbn()))
                    .filter(copy -> copy.getState() != BookCopy.State.DAMAGED &&
                            copy.getState() != BookCopy.State.NEED_REPLACEMENT)
                    .count();

            map.put(book, count);
        }

        return map;
    }

    @Override
    public synchronized Long countCopiesByIsbnAndState(String isbn, BookCopy.State state) {
        return bookCopies
                .stream()
                .filter(copy -> copy.getBookIsbn().equals(isbn) && copy.getState() == state)
                .count();
    }

    @Override
    public synchronized void addBook(Book book) throws ObjectAlreadyExistsException {

        if (books.contains(book)) {
            throw new ObjectAlreadyExistsException(Book.class.getSimpleName(), book.getIsbn());
        }

        books.add(book);
    }

    private void checkBookCopyConsistency(BookCopy copy) throws InconsistencyFoundException {
        var book = findBookByIsbn(copy.getBookIsbn());

        if (book.isEmpty()) {
            throw new InconsistencyFoundException(
                    "Passed BookCopy doesn't match any Book! Isbn '" + copy.getBookIsbn() + "' not found!"
            );
        }
    }


    @Override
    public synchronized void addBookCopy(BookCopy bookCopy) throws RepositoryException {

        var optCopy = bookCopies
                .stream()
                .filter(copy -> copy.equals(bookCopy))
                .findAny();

        if (optCopy.isPresent()) {

            var copy = optCopy.get();
            throw new ObjectAlreadyExistsException(BookCopy.class.getSimpleName(), copy.getUuid().toString());
        }

        checkBookCopyConsistency(bookCopy);

        bookCopies.add(bookCopy);
    }

    @Override
    public synchronized void updateBook(Book book) throws ObjectNotFoundException {

        if (!books.contains(book)) {
            throw new ObjectNotFoundException(Book.class.getSimpleName(), book.getIsbn());
        }

        books.remove(book);
        books.add(book);
    }

    @Override
    public synchronized void updateBookCopy(BookCopy copy) throws RepositoryException {

        if (!bookCopies.contains(copy)) {
            throw new ObjectNotFoundException(BookCopy.class.getSimpleName(), copy.getUuid().toString());
        }

        checkBookCopyConsistency(copy);

        bookCopies.remove(copy);
        bookCopies.add(copy);
    }

    public synchronized void removeBookByIsbn(String isbn) throws ObjectNotFoundException {
        var book = findBookByIsbn(isbn);
        if (book.isEmpty()) {
            throw new ObjectNotFoundException(Book.class.getSimpleName(), isbn);
        }

        books.remove(book.get());
    }

    public synchronized void removeBookCopyByUuid(UUID uuid) throws ObjectNotFoundException {

        var copy = bookCopies.stream()
                .filter(c -> c.getUuid().equals(uuid))
                .findFirst();

        if (copy.isEmpty()) {
            throw new ObjectNotFoundException(BookCopy.class.getSimpleName(), uuid.toString());
        }

        bookCopies.remove(copy.get());
    }
}
