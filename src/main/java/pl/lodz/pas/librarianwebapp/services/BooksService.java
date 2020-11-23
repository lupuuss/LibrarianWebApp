package pl.lodz.pas.librarianwebapp.services;

import pl.lodz.pas.librarianwebapp.DateProvider;
import pl.lodz.pas.librarianwebapp.repository.books.BooksRepository;
import pl.lodz.pas.librarianwebapp.repository.books.data.Book;
import pl.lodz.pas.librarianwebapp.repository.books.data.BookCopy;
import pl.lodz.pas.librarianwebapp.repository.events.EventsRepository;
import pl.lodz.pas.librarianwebapp.repository.events.data.BookLock;
import pl.lodz.pas.librarianwebapp.repository.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;
import pl.lodz.pas.librarianwebapp.services.dto.BookCopyDto;
import pl.lodz.pas.librarianwebapp.services.dto.BookDto;
import pl.lodz.pas.librarianwebapp.services.dto.BookLockDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
public class BooksService {

    @SuppressWarnings("FieldCanBeLocal")
    private final long reservationTimeInMinutes = 1;

    @Inject
    private BooksRepository booksRepository;

    @Inject
    private EventsRepository eventsRepository;

    @Inject
    private DateProvider dateProvider;

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

        var books = booksRepository.findAllBooks();

        var copies = new ArrayList<BookCopyDto>();

        for (var book : books) {

            var bookDto = new BookDto(
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher()
            );

            var copiesForBook = booksRepository.findBookCopiesByIsbn(book.getIsbn())
                    .stream()
                    .map(copy -> new BookCopyDto(copy.getNumber(), bookDto, mapState(copy.getState())))
                    .collect(Collectors.toList());

            copies.addAll(copiesForBook);
        }

        return copies;
    }

    public boolean addBook(BookDto book) {

        try {

            booksRepository.addBook(new Book(
                    UUID.randomUUID(),
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

    public boolean addCopy(String isbn, BookCopyDto.State state) {
        try {

            var number = booksRepository.getNextCopyNumberByIsbn(isbn);

            var book = booksRepository.findBookByIsbn(isbn);

            if (book.isEmpty()) {
                return false;
            }

            booksRepository.addBookCopy(new BookCopy(
                    UUID.randomUUID(),
                    book.get().getUuid(),
                    number,
                    mapState(state)
            ));

            return true;
        } catch (RepositoryException e) {

            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAllIsbns() {
        return booksRepository.findAllBooks()
                .stream()
                .map(Book::getIsbn)
                .collect(Collectors.toList());
    }

    public void degradeCopies(List<BookCopyDto> copies) {

        for (var copy : copies) {

            var toUpdate = booksRepository.findBookCopyByIsbnAndNumber(
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
                booksRepository.updateBookCopy(bookCopy);
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteCopies(List<BookCopyDto> copies) {

        for (var bookCopy : copies) {

            var toRemove = booksRepository.findBookCopyByIsbnAndNumber(
                    bookCopy.getBook().getIsbn(),
                    bookCopy.getNumber()
            );

            if (toRemove.isEmpty()) {
                continue;
            }

            try {
                booksRepository.deleteBookCopy(toRemove.get());
            } catch (ObjectNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<BookDto, Long> getAvailableCopiesCount() {

        var books = booksRepository.findAllBooks();
        var booksMap = new HashMap<BookDto, Long>();

        for (var book : books) {
            var copies = booksRepository.findBookCopiesByIsbnAndNotDamaged(book.getIsbn());

            var amount = copies.stream()
                    .filter(copy -> eventsRepository.isBookAvailable(copy.getUuid()))
                    .count();

            booksMap.put(
                    new BookDto(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher()),
                    amount
            );
        }

        return booksMap;

    }

    public Optional<BookLockDto> lockBook(String isbn, String userLogin, BookCopyDto.State state) {

        var copies = booksRepository.findBookCopiesByIsbnAndState(isbn, mapState(state));

        var optReservedCopy = copies.stream()
                .filter(copy -> eventsRepository.isBookAvailable(copy.getUuid()))
                .findAny();

        if (optReservedCopy.isEmpty()) {
            return Optional.empty();
        }

        BookLock lock;

        try {
            lock = new BookLock(
                    optReservedCopy.get().getUuid(),
                    userLogin,
                    dateProvider.now().plusMinutes(reservationTimeInMinutes)
            );

            eventsRepository.saveBookLock(lock);

        } catch (InconsistencyFoundException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        var reservedCopy = optReservedCopy.get();

        var book = booksRepository.findBookByUuid(reservedCopy.getElementUuid()).orElseThrow();
        var bookDto = new BookDto(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher()
        );

        var result = new BookCopyDto(
                reservedCopy.getNumber(),
                bookDto,
                mapState(reservedCopy.getState())
        );

        return Optional.of(new BookLockDto(result, lock.getUserLogin(), lock.getUntil()));
    }

    public void unlockBook(String user, BookCopyDto bookCopyDto) {

        var bookCopy = booksRepository.findBookCopyByIsbnAndNumber(
                bookCopyDto.getBook().getIsbn(),
                bookCopyDto.getNumber()
        ).orElseThrow();

        eventsRepository.deleteBookLock(bookCopy.getUuid(), user);
    }

    public Optional<BookDto> getBook(String isbn) {
        return booksRepository.findBookByIsbn(isbn).map(book -> new BookDto(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher()
        ));
    }

    public List<BookCopyDto.State> getAvailableStatesForBook(String isbn) {

        var states = Arrays.stream(BookCopy.State.values())
                .filter(state -> state.getLevel() > BookCopy.State.NEED_REPLACEMENT.getLevel())
                .collect(Collectors.toList());

        var availableStates = new ArrayList<BookCopyDto.State>();

        for (var state : states) {

            var copies = booksRepository.findBookCopiesByIsbnAndState(isbn, state);

            if (copies.stream().anyMatch(copy -> eventsRepository.isBookAvailable(copy.getUuid()))) {
                availableStates.add(mapState(state));
            }
        }

        return availableStates;
    }
}
