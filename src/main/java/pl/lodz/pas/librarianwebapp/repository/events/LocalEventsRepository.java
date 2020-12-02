package pl.lodz.pas.librarianwebapp.repository.events;

import pl.lodz.pas.librarianwebapp.DateProvider;
import pl.lodz.pas.librarianwebapp.repository.events.data.*;
import pl.lodz.pas.librarianwebapp.repository.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class LocalEventsRepository implements EventsRepository {

    private final Set<Event> events = new HashSet<>();
    private final Set<ElementLock> locks = new HashSet<>();

    @Inject
    private DateProvider dateProvider;


    private Stream<LendingEvent> findLendingEvents() {
        return events.stream()
                .filter(e -> e instanceof LendingEvent)
                .map(e -> (LendingEvent) e);
    }

    @Override
    public void addEvent(Event event) throws RepositoryException {

        if (events.contains(event)) {
            throw new ObjectAlreadyExistsException(event.getClass().getSimpleName(), event.getUuid().toString());
        }

        if (event instanceof LendingEvent) {
            checkLendingEvent((LendingEvent) event);
        }

        if (event instanceof ReturnEvent) {
            var returnEvent = (ReturnEvent) event;
            var lendEvent = findCorrespondingLendEvent(returnEvent);

            if (lendEvent.isEmpty()) {
                throw new InconsistencyFoundException("No corresponding lending event!");
            }

            lendEvent.get().setReturnUuid(returnEvent.getUuid());
        }

        events.add(event);
    }

    private Optional<LendingEvent> findCorrespondingLendEvent(ReturnEvent event) {
        return findLendingEvents()
                .filter(e -> e.getElementUuid().equals(event.getElementUuid()) &&
                             e.getReturnUuid().isEmpty())
                .findFirst();
    }

    private void checkLendingEvent(LendingEvent event) throws InconsistencyFoundException {
        var result = events.stream()
                .filter(e -> e instanceof LendingEvent)
                .map(e -> (LendingEvent) e)
                .filter(e -> e.getReturnUuid().isEmpty() && e.getElementUuid().equals(event.getElementUuid()))
                .findAny();

        if (result.isPresent()) {
            throw new InconsistencyFoundException("Element is already lent!");
        }
    }

    @Override
    public Boolean isElementAvailable(UUID uuid) {
        var now = dateProvider.now();

        var isLocked = locks.stream()
                .anyMatch(lock -> lock.getElementUuid().equals(uuid) &&
                                  lock.getUntil().isAfter(now));

        if (isLocked) {
            return false;
        }

        return events.stream()
                .filter(e -> e instanceof LendingEvent)
                .map(e -> (LendingEvent) e)
                .filter(e -> e.getElementUuid().equals(uuid) && e.getReturnUuid().isEmpty())
                .findAny()
                .isEmpty();
    }

    @Override
    public List<LendingEvent> findLendingEventsByUserLogin(String userLogin) {
        return findLendingEvents()
                .filter(u -> u.getCustomerLogin().equals(userLogin))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReturnEvent> findReturnEventByUuid(UUID uuid) {
        return events.stream()
                .filter(e -> e instanceof ReturnEvent)
                .map(e -> (ReturnEvent) e)
                .filter(returnEvent -> returnEvent.getUuid().equals(uuid))
                .findAny();

    }


    @Override
    public void saveElementLock(ElementLock lock) throws InconsistencyFoundException {

        var isLockedByOtherUser = locks.stream()
                .anyMatch(l -> l.getElementUuid().equals(lock.getElementUuid()) &&
                               l.getUntil().isAfter(dateProvider.now()) &&
                               !l.getUserLogin().equals(lock.getUserLogin()));

        if (isLockedByOtherUser) {
            throw new InconsistencyFoundException("Element already locked!");
        }

        if (!locks.add(lock)) {
            locks.remove(lock);
            locks.add(lock);
        }
    }

    @Override
    public void deleteElementLock(UUID uuid, String user) {
        locks.removeIf(lock -> lock.getUserLogin().equals(user) && lock.getElementUuid().equals(uuid));
    }

    @Override
    public List<LendingEvent> findAllLendingEvents() {
        return findLendingEvents()
                .collect(Collectors.toList());
    }

    @Override
    public List<LendingEvent> findLendingEventsByUserLoginContains(String loginQuery) {

        return findLendingEvents()
                .filter(event -> event.getCustomerLogin().contains(loginQuery))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLendingEventByElementCopyUuidDate(UUID uuid, LocalDateTime date) {

        var event = findLendingEvents()
                .filter(e -> e.getElementUuid().equals(uuid) &&
                             e.getDate().equals(date))
                .findFirst();

        if (event.isEmpty()) {
            return;
        }

        events.remove(event.get());

        var returnUuid = event.get().getReturnUuid();

        returnUuid.ifPresent(value -> events.removeIf(e -> e.getUuid().equals(value)));
    }

    @Override
    public Optional<LendingEvent> findLendingEventByElementCopyUuidDate(UUID uuid, LocalDateTime date) {
        return findLendingEvents()
                .filter(e -> e.getElementUuid().equals(uuid) &&
                             e.getDate().equals(date))
                .findFirst();
    }

    @Override
    public void addReturnEvent(UUID lendEventUuid, LocalDateTime date, String customerLogin, UUID elementUuid) throws InconsistencyFoundException {

        var lendingEventOpt = findLendingEvents()
                .filter(event -> event.getUuid().equals(lendEventUuid))
                .findFirst();

        if (lendingEventOpt.isEmpty()) {
            throw new InconsistencyFoundException(
                    "No matching lending event with passed uuid: " + lendEventUuid.toString() + "!"
            );
        }

        var returnEvent = new ReturnEvent(date, customerLogin, elementUuid);

        lendingEventOpt.get().setReturnUuid(returnEvent.getUuid());

        events.add(returnEvent);
    }
}
