package pl.lodz.pas.librarianwebapp.model.repositories.books;

import pl.lodz.pas.librarianwebapp.model.repositories.books.data.Book;
import pl.lodz.pas.librarianwebapp.model.repositories.books.data.BookCopy;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianwebapp.model.repositories.exceptions.RepositoryException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BooksRepository {

    List<Book> findAllBooks();

    Optional<Book> findBookByIsbn(String isbn);

    List<BookCopy> findBookCopiesByIsbn(String isbn);

    Map<Book, Long> countAllBooksWithNotDamagedCopies();

    Long countCopiesByIsbnAndState(String isbn, BookCopy.State state);

    void addBook(Book book) throws ObjectAlreadyExistsException;

    void addBookCopy(BookCopy bookCopy) throws RepositoryException;

    void updateBook(Book book) throws ObjectNotFoundException;

    void updateBookCopy(BookCopy copy) throws ObjectNotFoundException, RepositoryException;

    Integer getNextCopyNumberByIsbn(String isbn);
}
