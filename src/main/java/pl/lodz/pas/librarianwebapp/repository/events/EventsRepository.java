package pl.lodz.pas.librarianwebapp.repository.events;

import pl.lodz.pas.librarianwebapp.repository.events.data.ElementLock;
import pl.lodz.pas.librarianwebapp.repository.events.data.Event;
import pl.lodz.pas.librarianwebapp.repository.events.data.LendingEvent;
import pl.lodz.pas.librarianwebapp.repository.events.data.ReturnEvent;
import pl.lodz.pas.librarianwebapp.repository.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventsRepository {

    void addEvent(Event event) throws RepositoryException;

    Boolean isElementAvailable(UUID uuid);

    List<LendingEvent> findLendingEventsByUserLogin(String userLogin);

    Optional<ReturnEvent> findReturnEventByUuid(UUID uuid);

    void saveElementLock(ElementLock lock) throws InconsistencyFoundException;

    void deleteElementLock(UUID uuid, String user);

    List<LendingEvent> findAllLendingEvents();

    List<LendingEvent> findLendingEventsByUserLoginContains(String loginQuery);

    void deleteLendingEventByElementCopyUuidDate(UUID uuid, LocalDateTime date);

    Optional<LendingEvent> findLendingEventByElementCopyUuidDate(UUID uuid, LocalDateTime date);

    void addReturnEvent(UUID lendEventUuid, LocalDateTime now, String customerLogin, UUID elementUuid) throws InconsistencyFoundException;

    void clearDanglingReferencesFor(UUID uuid);
}