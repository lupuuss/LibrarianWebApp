package pl.lodz.pas.librarianwebapp.services;

import pl.lodz.pas.librarianwebapp.DateProvider;
import pl.lodz.pas.librarianwebapp.repository.books.BooksRepository;
import pl.lodz.pas.librarianwebapp.repository.books.MagazinesRepository;
import pl.lodz.pas.librarianwebapp.repository.books.data.*;
import pl.lodz.pas.librarianwebapp.repository.events.EventsRepository;
import pl.lodz.pas.librarianwebapp.repository.events.data.ElementLock;
import pl.lodz.pas.librarianwebapp.repository.events.data.LendingEvent;
import pl.lodz.pas.librarianwebapp.repository.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;
import pl.lodz.pas.librarianwebapp.services.dto.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
public class ElementsService {

    @SuppressWarnings("FieldCanBeLocal")
    private final long reservationTimeInMinutes = 1;

    @Inject
    private BooksRepository booksRepository;

    @Inject
    private MagazinesRepository magazinesRepository;

    @Inject
    private EventsRepository eventsRepository;

    @Inject
    private DateProvider dateProvider;

    private ElementCopyDto.State mapState(ElementCopy.State state) {
        return ElementCopyDto.State.valueOf(state.name());
    }

    private ElementCopy.State mapState(ElementCopyDto.State state) {

        switch (state) {

            case NEW:
                return ElementCopy.State.NEW;
            case GOOD:
                return ElementCopy.State.GOOD;
            case USED:
                return ElementCopy.State.USED;
            case NEED_REPLACEMENT:
                return ElementCopy.State.NEED_REPLACEMENT;
            case DAMAGED:
                return ElementCopy.State.DAMAGED;
        }

        throw new IllegalArgumentException("Mapping for passed state not found!");
    }

    public List<ElementCopyDto> getAllCopies() {

        var books = booksRepository.findAllBooks();
        var magazines = magazinesRepository.findAllMagazines();

        var copies = new ArrayList<ElementCopyDto>();

        for (var book : books) {

            var bookDto = new BookDto(
                    book.getTitle(),
                    book.getPublisher(),
                    book.getIsbn(),
                    book.getAuthor()
            );

            var copiesForBook = booksRepository.findBookCopiesByIsbn(book.getIsbn())
                    .stream()
                    .map(copy -> new ElementCopyDto(copy.getNumber(), bookDto, mapState(copy.getState())))
                    .collect(Collectors.toList());

            copies.addAll(copiesForBook);
        }

        for (var magazine : magazines) {
            var magazineDto = new MagazineDto(
                    magazine.getTitle(),
                    magazine.getPublisher(),
                    magazine.getIssn(),
                    magazine.getIssue()
            );

            var copiesForMagazine =
                    magazinesRepository.findMagazineCopiesByIssnAndIssue(magazine.getIssn(), magazine.getIssue())
                            .stream()
                    .map(copy -> new ElementCopyDto(copy.getNumber(), magazineDto, mapState(copy.getState())))
                    .collect(Collectors.toList());

            copies.addAll(copiesForMagazine);
        }

        return copies;
    }

    public boolean addBookCopy(String isbn, ElementCopyDto.State state) {
        try {

            var number = booksRepository.getNextCopyNumberByIsbn(isbn);

            var book = booksRepository.findBookByIsbn(isbn);

            if (book.isEmpty()) {
                return false;
            }

            booksRepository.addBookCopy(new BookCopy(
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

    public boolean addMagazineCopy(String issn, int issue, ElementCopyDto.State state) {
        try {

            var number = magazinesRepository.getNextCopyNumberByIssnAndIssue(issn,issue);

            var magazine = magazinesRepository.findMagazineByIssnAndIssue(issn,issue);

            if (magazine.isEmpty()) {
                return false;
            }

            magazinesRepository.addMagazineCopy(new MagazineCopy(
                    magazine.get().getUuid(),
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

    public List<MagazineDto> getAllMagazines() {
        return magazinesRepository.findAllMagazines()
                .stream()
                .map(magazine -> new MagazineDto(
                        magazine.getTitle(),
                        magazine.getPublisher(),
                        magazine.getIssn(),
                        magazine.getIssue()
                        ))
                .collect(Collectors.toList());
    }

    private void degradeBookCopy(BookDto bookDto, ElementCopyDto copy) {

        var toUpdate = booksRepository.findBookCopyByIsbnAndNumber(
                bookDto.getIsbn(),
                copy.getNumber()
        );

        if (toUpdate.isEmpty()) {
            return;
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

    private void degradeMagazineCopy(MagazineDto magazineDto, ElementCopyDto copy) {
        var toUpdate = magazinesRepository.findMagazineCopyByIssnAndIssueAndNumber(
                magazineDto.getIssn(),
                magazineDto.getIssue(),
                copy.getNumber()
        );

        if (toUpdate.isEmpty()) {
            return;
        }

        var magazineCopy = toUpdate.get();

        var currentState = magazineCopy.getState();
        magazineCopy.setState(MagazineCopy.State.degrade(currentState));

        try {
            magazinesRepository.updateMagazineCopy(magazineCopy);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    public void degradeCopies(List<ElementCopyDto> copies) {

        for (var copy : copies) {

            if (copy.getElement() instanceof BookDto) {

                var book = (BookDto) copy.getElement();

                degradeBookCopy(book, copy);

            } else if(copy.getElement() instanceof MagazineDto) {

                var magazine = (MagazineDto) copy.getElement();

                degradeMagazineCopy(magazine, copy);

            } else {
                throw new IllegalStateException("Unsupported element type!");
            }
        }

    }

    private void deleteBookCopy(ElementCopyDto copy) {
        var book = (BookDto) copy.getElement();
        var toRemove = booksRepository.findBookCopyByIsbnAndNumber(
                book.getIsbn(),
                copy.getNumber()
        );

        if (toRemove.isEmpty()) {
            return;
        }

        try {
            booksRepository.deleteBookCopy(toRemove.get());
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    private void deleteMagazineCopy(ElementCopyDto copy) {
        var magazine = (MagazineDto) copy.getElement();
        var toRemove = magazinesRepository.findMagazineCopyByIssnAndIssueAndNumber(
                magazine.getIssn(),
                magazine.getIssue(),
                copy.getNumber()
        );

        if (toRemove.isEmpty()) {
            return;
        }

        try {
            magazinesRepository.deleteMagazineCopy(toRemove.get());
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    public void deleteCopies(List<ElementCopyDto> copies) {

        for (var copy : copies) {

            if (copy.getElement() instanceof BookDto) {

                deleteBookCopy(copy);

            } else if(copy.getElement() instanceof MagazineDto) {

                deleteMagazineCopy(copy);

            } else {
                throw new IllegalStateException("Unsupported element type!");
            }
        }


    }

    public Map<ElementDto, Long> getAvailableCopiesCount() {

        var books = booksRepository.findAllBooks();
        var booksMap = new HashMap<ElementDto, Long>();

        for (var book : books) {
            var copies = booksRepository.findBookCopiesByIsbnAndNotDamaged(book.getIsbn());

            var amount = copies.stream()
                    .filter(copy -> eventsRepository.isElementAvailable(copy.getUuid()))
                    .count();

            booksMap.put(
                    new BookDto(book.getTitle(), book.getPublisher(), book.getIsbn(), book.getAuthor()),
                    amount
            );
        }

        var magazines = magazinesRepository.findAllMagazines();

        for (var magazine : magazines) {
            var copies =
                    magazinesRepository.findMagazineCopiesByIssnAndIssueAndNotDamaged(magazine.getIssn(), magazine.getIssue());

            var amount = copies.stream()
                    .filter(copy -> eventsRepository.isElementAvailable(copy.getUuid()))
                    .count();

            booksMap.put(
                    new MagazineDto(magazine.getTitle(), magazine.getPublisher(), magazine.getIssn(), magazine.getIssue()),
                    amount
            );
        }

        return booksMap;

    }

    public Optional<ElementLockDto> lockBook(String isbn, String userLogin, ElementCopyDto.State state) {

        List<BookCopy> copies = booksRepository.findBookCopiesByIsbnAndState(isbn, mapState(state));

        var optReservedCopy = copies.stream()
                .filter(copy -> eventsRepository.isElementAvailable(copy.getUuid()))
                .findAny();

        if (optReservedCopy.isEmpty()) {
            return Optional.empty();
        }

        ElementLock lock;

        try {
            lock = new ElementLock(
                    optReservedCopy.get().getUuid(),
                    userLogin,
                    dateProvider.now().plusMinutes(reservationTimeInMinutes)
            );

            eventsRepository.saveElementLock(lock);

        } catch (InconsistencyFoundException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        var reservedCopy = optReservedCopy.get();

        var book = booksRepository.findBookByUuid(reservedCopy.getElementUuid()).orElseThrow();
        var bookDto = new BookDto(
                book.getTitle(),
                book.getPublisher(),
                book.getIsbn(),
                book.getAuthor()
        );

        var result = new ElementCopyDto(
                reservedCopy.getNumber(),
                bookDto,
                mapState(reservedCopy.getState())
        );

        return Optional.of(new ElementLockDto(result, lock.getUserLogin(), lock.getUntil()));
    }

    public Optional<ElementLockDto> lockMagazine(String issn, int issue, String userLogin, ElementCopyDto.State state ){

       List<MagazineCopy> copies = magazinesRepository.findMagazineCopiesByIssnAndIssueAndState(issn,issue,mapState(state));

       var optReservedCopy = copies.stream()
               .filter(copy -> eventsRepository.isElementAvailable(copy.getUuid()))
               .findAny();

       if (optReservedCopy.isEmpty()) {
           return Optional.empty();
       }

       ElementLock lock;

       try {
           lock = new ElementLock(
                   optReservedCopy.get().getUuid(),
                   userLogin,
                   dateProvider.now().plusMinutes(reservationTimeInMinutes)
           );

           eventsRepository.saveElementLock(lock);

       } catch (InconsistencyFoundException e) {
           e.printStackTrace();
           return Optional.empty();
       }

       var reservedCopy = optReservedCopy.get();

       var magazine = magazinesRepository.findMagazineByUuid(reservedCopy.getElementUuid()).orElseThrow();
       var magazineDto = new MagazineDto(
               magazine.getTitle(),
               magazine.getPublisher(),
               magazine.getIssn(),
               magazine.getIssue()
       );

       var result = new ElementCopyDto(
               reservedCopy.getNumber(),
               magazineDto,
               mapState(reservedCopy.getState())
       );

       return Optional.of(new ElementLockDto(result, lock.getUserLogin(), lock.getUntil()));
    }

    public void unlockElement(String user, ElementCopyDto elementCopyDto) {

        ElementCopy<?> element;

        if (elementCopyDto.getElement() instanceof BookDto) {

            var book = (BookDto) elementCopyDto.getElement();

            element = booksRepository.findBookCopyByIsbnAndNumber(
                    book.getIsbn(),
                    elementCopyDto.getNumber()
            ).orElseThrow();

        } else if (elementCopyDto.getElement() instanceof MagazineDto) {

            var magazine = (MagazineDto) elementCopyDto.getElement();

            element = magazinesRepository.findMagazineCopyByIssnAndIssueAndNumber(
                    magazine.getIssn(),
                    magazine.getIssue(),
                    elementCopyDto.getNumber()
            ).orElseThrow();

        } else {

            throw new IllegalStateException("Unsupported element type!");

        }

        eventsRepository.deleteElementLock(element.getUuid(), user);
    }

    public Optional<BookDto> getBook(String isbn) {
        return booksRepository.findBookByIsbn(isbn).map(book -> new BookDto(
                book.getTitle(),
                book.getPublisher(),
                book.getIsbn(),
                book.getAuthor()
        ));
    }

    public List<ElementCopyDto.State> getAvailableStatesForBook(String isbn) {

        var states = Arrays.stream(BookCopy.State.values())
                .filter(state -> state.getLevel() > BookCopy.State.NEED_REPLACEMENT.getLevel())
                .collect(Collectors.toList());

        var availableStates = new ArrayList<ElementCopyDto.State>();

        for (var state : states) {

            var copies = booksRepository.findBookCopiesByIsbnAndState(isbn, state);

            if (copies.stream().anyMatch(copy -> eventsRepository.isElementAvailable(copy.getUuid()))) {
                availableStates.add(mapState(state));
            }
        }

        return availableStates;
    }

    public List<ElementCopyDto.State> getAvailableStatesForMagazine(String issn, Integer issue){
        var states = Arrays.stream(MagazineCopy.State.values())
                .filter(state -> state.getLevel()>MagazineCopy.State.NEED_REPLACEMENT.getLevel())
                .collect(Collectors.toList());

        var availableStates = new ArrayList<ElementCopyDto.State>();

        for (var state : states){
            var copies = magazinesRepository.findMagazineCopiesByIssnAndIssueAndState(issn,issue,state);

            if (copies.stream().anyMatch(copy -> eventsRepository.isElementAvailable(copy.getUuid()))){
                availableStates.add(mapState(state));
            }
        }
        return availableStates;
    }

    public Optional<MagazineDto> getMagazine(String ref, Integer issue) {
        return magazinesRepository.findMagazineByIssnAndIssue(ref, issue).map(magazine -> new MagazineDto(
                magazine.getTitle(),
                magazine.getPublisher(),
                magazine.getIssn(),
                magazine.getIssue()
        ));
    }

    public boolean addBook(BookDto book) {

        try{
            booksRepository.addBook(new Book(
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

    public boolean addMagazine(MagazineDto magazine){
        try {
            magazinesRepository.addMagazine(new Magazine(
                    magazine.getPublisher(),
                    magazine.getTitle(),
                    magazine.getIssn(),
                    magazine.getIssue()
            ));
            return true;
        } catch (ObjectAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addElement(ElementDto elementDto) {

        if (elementDto instanceof BookDto) {

            var book = (BookDto) elementDto;
            return addBook(book);

        } else if (elementDto instanceof MagazineDto) {

            var magazine = (MagazineDto) elementDto;
            return addMagazine(magazine);

        }
        return false;

    }

    public boolean rentLockedItems(Set<ElementLockDto> elementLocks, String userLogin) {

        if (!elementLocks.stream().allMatch(lock -> lock.getUntil().isAfter(dateProvider.now()))) {
            return false;
        }

        for (var lock : elementLocks) {

            var elementCopy = lock.getCopy();

            UUID uuid;

            if (elementCopy.getElement() instanceof BookDto) {
                BookDto book = (BookDto) elementCopy.getElement();

                uuid = booksRepository.findBookCopyByIsbnAndNumber(book.getIsbn(), elementCopy.getNumber())
                        .orElseThrow()
                        .getUuid();
            } else if (elementCopy.getElement() instanceof MagazineDto) {
                MagazineDto magazine = (MagazineDto) elementCopy.getElement();

                uuid = magazinesRepository.findMagazineCopyByIssnAndIssueAndNumber(magazine.getIssn(), magazine.getIssue(), elementCopy.getNumber())
                        .orElseThrow()
                        .getUuid();
            } else {
                throw new IllegalStateException("Unsupported element type!");
            }

            try {
                eventsRepository.addEvent(new LendingEvent(dateProvider.now(), userLogin, uuid));
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public List<LendEventDto> getLendingForUser(String user) {

        var events = eventsRepository.findLendingEventsByUserLogin(user);

        List<LendEventDto> eventDtos = new ArrayList<>();

        for (var event : events) {

            var copy = getElementCopyDtoByUuid(event.getElementUuid());

            var lendDate = event.getDate();

            LocalDateTime returnDate = null;

            if (event.getReturnUuid().isPresent()) {
                var returnEvent =
                        eventsRepository.findReturnEventByUuid(event.getReturnUuid().get());

                returnDate = returnEvent.orElseThrow().getDate();
            }

            eventDtos.add(new LendEventDto(copy, lendDate, returnDate, user));
        }

        return eventDtos;
    }

    private ElementCopyDto getElementCopyDtoByUuid(UUID uuid) {

        var bookCopy = booksRepository.findBookCopyByUuid(uuid);

        if (bookCopy.isPresent()) {

            var book =
                    booksRepository.findBookByUuid(bookCopy.get().getElementUuid())
                            .orElseThrow();

            return new ElementCopyDto(
                    bookCopy.get().getNumber(),
                    new BookDto(book.getTitle(), book.getPublisher(), book.getIsbn(), book.getAuthor()),
                    mapState(bookCopy.get().getState())
            );
        }

        var magazineCopy = magazinesRepository.findMagazineCopyByUuid(uuid);

        if (magazineCopy.isPresent()) {
            var magazine = magazinesRepository.findMagazineByUuid(magazineCopy.get().getElementUuid())
                    .orElseThrow();

            return new ElementCopyDto(
                    magazineCopy.get().getNumber(),
                    new MagazineDto(magazine.getTitle(),magazine.getPublisher(), magazine.getIssn(),magazine.getIssue()),
                    mapState(magazineCopy.get().getState())
            );

        }

        return null;
    }
}
