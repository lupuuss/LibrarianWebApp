package pl.lodz.pas.librarianwebapp.repository.events;

import pl.lodz.pas.librarianwebapp.DateProvider;
import pl.lodz.pas.librarianwebapp.repository.events.data.*;
import pl.lodz.pas.librarianwebapp.repository.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class LocalEventsRepository implements EventsRepository {

    private final Set<Event> events = new HashSet<>();
    private final Set<ElementLock> locks = new HashSet<>();

    @Inject
    private DateProvider dateProvider;

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
        return events.stream()
                .filter(e -> e instanceof LendingEvent)
                .map(e -> (LendingEvent) e)
                .filter(e -> e.getElementUuid().equals(event.getElementUuid()) && e.getReturnUuid().isEmpty())
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


}
