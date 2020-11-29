package pl.lodz.pas.librarianwebapp.repository.events;

import pl.lodz.pas.librarianwebapp.repository.events.data.ElementLock;
import pl.lodz.pas.librarianwebapp.repository.events.data.Event;
import pl.lodz.pas.librarianwebapp.repository.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;

import java.util.UUID;

public interface EventsRepository {

    void addEvent(Event event) throws ObjectAlreadyExistsException, RepositoryException;

    Boolean isElementAvailable(UUID uuid);

    void saveElementLock(ElementLock lock) throws InconsistencyFoundException;

    void deleteElementLock(UUID uuid, String user);
}