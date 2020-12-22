package pl.lodz.pas.librarianwebapp.repository.books;

import pl.lodz.pas.librarianwebapp.producer.annotations.BooksRepositoryInitializer;
import pl.lodz.pas.librarianwebapp.repository.books.data.Book;
import pl.lodz.pas.librarianwebapp.repository.books.data.BookCopy;
import pl.lodz.pas.librarianwebapp.repository.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class LocalBooksRepository implements BooksRepository {

    @Inject
    @BooksRepositoryInitializer
    private BiConsumer<Set<Book>,Set<BookCopy>> booksInitializer;


    private final Set<Book> books = new TreeSet<>(Comparator.comparing(Book::getIsbn));
    private final Set<BookCopy> bookCopies = new TreeSet<>(Comparator.comparing(BookCopy::getUuid));

    @PostConstruct
    private void initializeBooks() {
        if (booksInitializer != null) {
            booksInitializer.accept(books, bookCopies);
        }
    }

    @Override
    public synchronized Integer getNextCopyNumberByIsbn(String isbn) {

        return findBookCopiesByIsbn(isbn)
                .stream()
                .mapToInt(copy -> copy.getNumber() + 1)
                .max()
                .orElse(0);
    }

    @Override
    public synchronized List<Book> findAllBooks() {
        return books.stream()
                .map(Book::copy)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized Optional<Book> findBookByIsbn(String isbn) {

        return books.stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst()
                .map(Book::copy);
    }

    @Override
    public synchronized Optional<Book> findBookByUuid(UUID uuid) {
        return books.stream()
                .filter(book -> book.getUuid().equals(uuid))
                .findFirst()
                .map(Book::copy);
    }

    @Override
    public synchronized Optional<BookCopy> findBookCopyByUuid(UUID uuid) {
        return bookCopies.stream()
                .filter(bookCopy -> bookCopy.getUuid().equals(uuid))
                .findAny()
                .map(BookCopy::copy);
    }

    @Override
    public synchronized List<BookCopy> findBookCopiesByIsbn(String isbn) {

        var book = findBookByIsbn(isbn);

        if (book.isEmpty()) {
            return Collections.emptyList();
        }

        return bookCopies.stream()
                .filter(copy -> copy.getElementUuid().equals(book.get().getUuid()))
                .map(BookCopy::copy)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<BookCopy> findBookCopiesByIsbnAndNotDamaged(String isbn) {
        return findBookCopiesByIsbn(isbn)
                .stream()
                .filter(copy -> copy.getState() != BookCopy.State.DAMAGED &&
                                copy.getState() != BookCopy.State.NEED_REPLACEMENT)
                .collect(Collectors.toList());
    }
    @Override
    public synchronized void addBook(Book book) throws ObjectAlreadyExistsException {

        if (books.contains(book)) {
            throw new ObjectAlreadyExistsException(Book.class.getSimpleName(), book.getIsbn());
        }

        books.add(book.copy());
    }

    private void checkBookCopyConsistency(BookCopy copy) throws InconsistencyFoundException {
        var book = books.stream()
                .filter(b -> b.getUuid().equals(copy.getElementUuid()))
                .findFirst();

        if (book.isEmpty()) {
            throw new InconsistencyFoundException(
                    "Passed BookCopy doesn't match any Book! UUID '" + copy.getElementUuid() + "' not found!"
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

        bookCopies.add(bookCopy.copy());
    }

    @Override
    public synchronized Optional<BookCopy> findBookCopyByIsbnAndNumber(String isbn, int number) {

        var book = findBookByIsbn(isbn);

        if (book.isEmpty()) {
            return Optional.empty();
        }

        return bookCopies.stream()
                .filter(copy -> copy.getElementUuid().equals(book.get().getUuid()) &&
                                copy.getNumber() == number)
                .findFirst();
    }

    @Override
    public synchronized List<BookCopy> findBookCopiesByIsbnAndState(String isbn, BookCopy.State state) {

        var book = findBookByIsbn(isbn);

        if (book.isEmpty()) {
            return Collections.emptyList();
        }

        return bookCopies.stream()
                .filter(copy -> copy.getState() == state && copy.getElementUuid().equals(book.get().getUuid()))
                .map(BookCopy::copy)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void updateBook(Book book) throws ObjectNotFoundException {

        if (!books.contains(book)) {
            throw new ObjectNotFoundException(Book.class.getSimpleName(), book.getIsbn());
        }

        books.remove(book);
        books.add(book.copy());
    }

    @Override
    public synchronized void updateBookCopy(BookCopy bookCopy) throws RepositoryException {

        if (!bookCopies.contains(bookCopy)) {
            throw new ObjectNotFoundException(BookCopy.class.getSimpleName(), bookCopy.getUuid().toString());
        }

        checkBookCopyConsistency(bookCopy);

        bookCopies.remove(bookCopy);
        bookCopies.add(bookCopy.copy());
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

    @Override
    public synchronized void deleteBookCopy(BookCopy bookCopy) throws ObjectNotFoundException {

        if (!bookCopies.contains(bookCopy)) {
            throw new ObjectNotFoundException(BookCopy.class.getSimpleName(), bookCopy.getUuid().toString());
        }

        bookCopies.remove(bookCopy);
    }

    private Stream<BookCopy> streamBookCopiesByIsbnContains(String query) {
        return bookCopies.stream()
                .filter(book -> findBookByUuid(book.getElementUuid())
                        .orElseThrow()
                        .getIsbn()
                        .contains(query))
                .map(BookCopy::copy);
    }

    @Override
    public synchronized List<BookCopy> findBookCopiesByIsbnContains(String query) {

        return streamBookCopiesByIsbnContains(query)
                .collect(Collectors.toList());

    }

    @Override
    public synchronized List<BookCopy> findBookCopiesByIsbnContains(String query, int limit, int offset) {
         return streamBookCopiesByIsbnContains(query)
                 .skip(offset)
                 .limit(limit)
                 .collect(Collectors.toList());
    }

    @Override
    public int countBookCopiesByIsbnContains(String query) {
        return (int) streamBookCopiesByIsbnContains(query).count();
    }

    @Override
    public synchronized void deleteBook(Book book) throws ObjectNotFoundException {
        if (!books.contains(book)) {
            throw new ObjectNotFoundException(Book.class.getSimpleName(), book.getIsbn());
        }

        books.remove(book);
    }
}
