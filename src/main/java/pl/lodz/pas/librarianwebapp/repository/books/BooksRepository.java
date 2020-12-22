package pl.lodz.pas.librarianwebapp.repository.books;

import pl.lodz.pas.librarianwebapp.repository.books.data.Book;
import pl.lodz.pas.librarianwebapp.repository.books.data.BookCopy;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BooksRepository {

    Integer getNextCopyNumberByIsbn(String isbn);

    List<Book> findAllBooks();

    Optional<Book> findBookByIsbn(String isbn);

    Optional<Book> findBookByUuid(UUID uuid);

    Optional<BookCopy> findBookCopyByUuid(UUID uuid);

    List<BookCopy> findBookCopiesByIsbn(String isbn);

    Optional<BookCopy> findBookCopyByIsbnAndNumber(String isbn, int number);

    List<BookCopy> findBookCopiesByIsbnAndNotDamaged(String isbn);

    List<BookCopy> findBookCopiesByIsbnAndState(String isbn, BookCopy.State state);

    void addBook(Book book) throws ObjectAlreadyExistsException;

    void addBookCopy(BookCopy bookCopy) throws RepositoryException;

    void updateBook(Book book) throws ObjectNotFoundException;

    void updateBookCopy(BookCopy copy) throws RepositoryException;

    void deleteBook(Book book) throws ObjectNotFoundException;

    void deleteBookCopy(BookCopy bookCopy) throws ObjectNotFoundException;

    List<BookCopy> findBookCopiesByIsbnContains(String query);

    List<BookCopy> findBookCopiesByIsbnContains(String query, int limit, int offset);

    int countBookCopiesByIsbnContains(String query);
}
