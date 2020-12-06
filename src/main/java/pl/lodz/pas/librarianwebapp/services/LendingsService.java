package pl.lodz.pas.librarianwebapp.services;

import pl.lodz.pas.librarianwebapp.DateProvider;
import pl.lodz.pas.librarianwebapp.repository.books.BooksRepository;
import pl.lodz.pas.librarianwebapp.repository.books.MagazinesRepository;
import pl.lodz.pas.librarianwebapp.repository.books.data.BookCopy;
import pl.lodz.pas.librarianwebapp.repository.books.data.ElementCopy;
import pl.lodz.pas.librarianwebapp.repository.books.data.MagazineCopy;
import pl.lodz.pas.librarianwebapp.repository.events.EventsRepository;
import pl.lodz.pas.librarianwebapp.repository.events.data.ElementLock;
import pl.lodz.pas.librarianwebapp.repository.events.data.LendingEvent;
import pl.lodz.pas.librarianwebapp.repository.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;
import pl.lodz.pas.librarianwebapp.repository.user.UsersRepository;
import pl.lodz.pas.librarianwebapp.services.dto.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestScoped
public class LendingsService {

    @Inject
    private DateProvider dateProvider;

    @Inject
    private BooksRepository booksRepository;

    @Inject
    private MagazinesRepository magazinesRepository;

    @Inject
    private EventsRepository eventsRepository;

    @Inject
    private UsersRepository usersRepository;

    private final long reservationTimeInMinutes = 30;

    public boolean lendLockedElements(Set<ElementLockDto> elementLocks, String userLogin) {

        var user = usersRepository.findUserByLogin(userLogin);

        if (user.isEmpty() || !user.get().isActive()) {
            return false;
        }

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

            eventsRepository.deleteElementLock(uuid, userLogin);

            try {
                eventsRepository.addEvent(new LendingEvent(dateProvider.now(), userLogin, uuid));
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public List<LendEventDto> getLendingsForUser(String user) {

        var events = eventsRepository.findLendingEventsByUserLogin(user);

        return completeEventsDto(events);
    }

    public List<LendEventDto> getAllLendings() {

        var events = eventsRepository.findAllLendingEvents();

        return completeEventsDto(events);
    }

    public List<LendEventDto> getFilteredLendings(String loginQuery, String elementRefQuery) {

        List<LendingEvent> events;

        if ((loginQuery != null && !loginQuery.isBlank())) {
            events = eventsRepository.findLendingEventsByUserLoginContains(loginQuery);
        } else {
            events = eventsRepository.findAllLendingEvents();
        }

        if ((elementRefQuery == null || elementRefQuery.isBlank())) {
            return completeEventsDto(events);
        }

        var bookCopies = booksRepository.findBookCopiesByIsbnContains(elementRefQuery)
                .stream()
                .map(ElementCopy::getUuid);

        var magazinesCopies = magazinesRepository.findMagazineCopiesByIssnContains(elementRefQuery)
                .stream().map(ElementCopy::getUuid);

        var allUuids = Stream.concat(bookCopies, magazinesCopies)
                .collect(Collectors.toList());

        events = events.stream()
                .filter(event -> allUuids.contains(event.getElementUuid()))
                .collect(Collectors.toList());

        return completeEventsDto(events);
    }

    private List<LendEventDto> completeEventsDto(List<LendingEvent> events) {

        List<LendEventDto> eventDtos = new ArrayList<>();

        for (var event : events) {

            var copy = getElementCopyDtoByUuid(event.getElementUuid());

            var lendDate = Timestamp.valueOf(event.getDate());

            Date returnDate = null;

            if (event.getReturnUuid().isPresent()) {
                var returnEvent =
                        eventsRepository.findReturnEventByUuid(event.getReturnUuid().get());

                returnDate = Timestamp.valueOf(returnEvent.orElseThrow().getDate());
            }

            eventDtos.add(new LendEventDto(copy, lendDate, returnDate, event.getCustomerLogin()));
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
                    StateUtils.mapState(bookCopy.get().getState())
            );
        }

        var magazineCopy = magazinesRepository.findMagazineCopyByUuid(uuid);

        if (magazineCopy.isPresent()) {
            var magazine = magazinesRepository.findMagazineByUuid(magazineCopy.get().getElementUuid())
                    .orElseThrow();

            return new ElementCopyDto(
                    magazineCopy.get().getNumber(),
                    new MagazineDto(magazine.getTitle(),magazine.getPublisher(), magazine.getIssn(),magazine.getIssue()),
                    StateUtils.mapState(magazineCopy.get().getState())
            );

        }

        return null;
    }

    public Optional<ElementLockDto> lockBook(String isbn, String userLogin, ElementCopyDto.State state) {

        List<BookCopy> copies = booksRepository.findBookCopiesByIsbnAndState(isbn, StateUtils.mapState(state));

        var user = usersRepository.findUserByLogin(userLogin);

        if (user.isEmpty() || !user.get().isActive()) {
            return Optional.empty();
        }

        var optReservedBook = copies.stream()
                .filter(copy -> eventsRepository.isElementAvailable(copy.getUuid()))
                .findAny();

        if (optReservedBook.isEmpty()) {
            return Optional.empty();
        }

        var reservedCopy = optReservedBook.get();

        ElementLock lock = getLockForElementCopy(userLogin, reservedCopy);

        if (lock == null) return Optional.empty();

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
                StateUtils.mapState(reservedCopy.getState())
        );

        return Optional.of(new ElementLockDto(result, lock.getUserLogin(), lock.getUntil()));
    }

    public Optional<ElementLockDto> lockMagazine(String issn, int issue, String userLogin, ElementCopyDto.State state ) {

        List<MagazineCopy> copies = magazinesRepository.findMagazineCopiesByIssnAndIssueAndState(issn, issue, StateUtils.mapState(state));

        var user = usersRepository.findUserByLogin(userLogin);

        if (user.isEmpty() || !user.get().isActive()) {
            return Optional.empty();
        }

        var optReservedMagazine = copies.stream()
                .filter(copy -> eventsRepository.isElementAvailable(copy.getUuid()))
                .findAny();

        if (optReservedMagazine.isEmpty()) {
            return Optional.empty();
        }

        var reservedCopy = optReservedMagazine.get();

        ElementLock lock = getLockForElementCopy(userLogin, reservedCopy);

        if (lock == null) {
            return Optional.empty();
        }

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
                StateUtils.mapState(reservedCopy.getState())
        );

        return Optional.of(new ElementLockDto(result, lock.getUserLogin(), lock.getUntil()));
    }

    private ElementLock getLockForElementCopy(String userLogin, ElementCopy<?> copy) {
        ElementLock lock;

        try {
            lock = new ElementLock(
                    copy.getUuid(),
                    userLogin,
                    dateProvider.now().plusMinutes(reservationTimeInMinutes)
            );

            eventsRepository.saveElementLock(lock);

            return lock;

        } catch (InconsistencyFoundException e) {
            e.printStackTrace();
            return null;
        }
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

    public void removeNotReturnedLendings(List<LendEventDto> lendingEvents) {

        var events = lendingEvents.stream()
                .filter(lend -> lend.getReturnDate() == null)
                .collect(Collectors.toList());

        for (var event : events){

            var copy = getElementCopyByDto(event.getCopy());

            if (copy.isEmpty()) {
                continue;
            }

            eventsRepository.deleteLendingEventByElementCopyUuidDate(
                    copy.get().getUuid(),
                    event.getLendDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
            );
        }

    }

    private Optional<ElementCopy<?>> getElementCopyByDto(ElementCopyDto copy) {

        if (copy.getElement() instanceof BookDto) {
            return booksRepository.findBookCopyByIsbnAndNumber(
                    ((BookDto) copy.getElement()).getIsbn(),
                    copy.getNumber()
            ).map(c -> c);
        } else if (copy.getElement() instanceof MagazineDto) {

            return magazinesRepository.findMagazineCopyByIssnAndIssueAndNumber(
                    ((MagazineDto) copy.getElement()).getIssn(),
                    ((MagazineDto) copy.getElement()).getIssue(),
                    copy.getNumber()
            ).map(c -> c);

        } else {
            throw new IllegalStateException("Unsupported element type!");
        }
    }

    public void returnLendings(List<LendEventDto> eventsDtos) {

        for (var eventDto : eventsDtos) {
            var copy = getElementCopyByDto(eventDto.getCopy());

            if (copy.isEmpty()) {
                continue;
            }

            var lendingEventOpt = eventsRepository.findLendingEventByElementCopyUuidDate(
                    copy.get().getUuid(),
                    eventDto.getLendDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
            );

            if (lendingEventOpt.isPresent()) {

                var lendingEvent = lendingEventOpt.get();

                try {
                    eventsRepository.addReturnEvent(
                            lendingEvent.getUuid(),
                            dateProvider.now(),
                            lendingEvent.getCustomerLogin(),
                            lendingEvent.getElementUuid()
                    );

                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
