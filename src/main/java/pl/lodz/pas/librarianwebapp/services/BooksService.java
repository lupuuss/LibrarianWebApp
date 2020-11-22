package pl.lodz.pas.librarianwebapp.services;

import pl.lodz.pas.librarianwebapp.repository.books.BooksRepository;
import pl.lodz.pas.librarianwebapp.repository.books.data.Book;
import pl.lodz.pas.librarianwebapp.repository.books.data.BookCopy;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;
import pl.lodz.pas.librarianwebapp.services.dto.BookCopyDto;
import pl.lodz.pas.librarianwebapp.services.dto.BookDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestScoped
public class BooksService {

    @Inject
    private BooksRepository repository;

    private BookCopyDto.State mapState(BookCopy.State state) {

        switch (state) {

            case NEW:
                return BookCopyDto.State.NEW;
            case GOOD:
                return BookCopyDto.State.GOOD;
            case USED:
                return BookCopyDto.State.USED;
            case NEED_REPLACEMENT:
                return BookCopyDto.State.NEED_REPLACEMENT;
            case DAMAGED:
                return BookCopyDto.State.DAMAGED;
        }

        throw new IllegalArgumentException("Mapping for passed state not found!");
    }

    private BookCopy.State mapState(BookCopyDto.State state) {

        switch (state) {

            case NEW:
                return BookCopy.State.NEW;
            case GOOD:
                return BookCopy.State.GOOD;
            case USED:
                return BookCopy.State.USED;
            case NEED_REPLACEMENT:
                return BookCopy.State.NEED_REPLACEMENT;
            case DAMAGED:
                return BookCopy.State.DAMAGED;
        }

        throw new IllegalArgumentException("Mapping for passed state not found!");
    }

    public List<BookCopyDto> getAllCopies() {

        var books = repository.findAllBooks();

        var copies = new ArrayList<BookCopyDto>();

        for (var book : books) {

            var copiesForBook = repository.findBookCopiesByIsbn(book.getIsbn())
                    .stream()
                    .map(copy -> new BookCopyDto(copy.getNumber(), book, mapState(copy.getState())))
                    .collect(Collectors.toList());

            copies.addAll(copiesForBook);
        }

        return copies;
    }

    public boolean addBook(BookDto book) {

        try {

            repository.addBook(new Book(
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher()
            ));
            return true;
        } catch (ObjectAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addBookCopy(String isbn, BookCopyDto.State state) {
        try {

            var number = repository.getNextCopyNumberByIsbn(isbn);

            repository.addBookCopy(new BookCopy(
                    UUID.randomUUID(),
                    number,
                    isbn,
                    mapState(state)
            ));

            return true;
        } catch (RepositoryException e) {

            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAllIsbns() {
        return repository.findAllBooks()
                .stream()
                .map(Book::getIsbn)
                .collect(Collectors.toList());
    }

    public void degradeCopies(List<BookCopyDto> copies) {

        for (var copy : copies) {

            var toUpdate = repository.findBookCopyByIsbnAndNumber(
                    copy.getBook().getIsbn(),
                    copy.getNumber()
            );

            if (toUpdate.isEmpty()) {
                continue;
            }

            var bookCopy = toUpdate.get();

            var currentState = bookCopy.getState();
            bookCopy.setState(BookCopy.State.degrade(currentState));

            try {
                repository.updateBookCopy(bookCopy);
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteBookCopies(List<BookCopyDto> copies) {

        for (var bookCopy : copies) {

            var toRemove = repository.findBookCopyByIsbnAndNumber(
                    bookCopy.getBook().getIsbn(),
                    bookCopy.getNumber()
            );

            if (toRemove.isEmpty()) {
                continue;
            }

            try {
                repository.deleteBookCopy(toRemove.get());
            } catch (ObjectNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<BookDto, Long> getAvailableBooksCopiesCount() {
        return repository.countAllBooksWithNotDamagedCopies()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> {
                            var book = entry.getKey();
                            return new BookDto(
                                    book.getIsbn(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    book.getPublisher()
                            );
                        },
                        Map.Entry::getValue
                ));
    }
}
