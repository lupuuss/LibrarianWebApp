package pl.lodz.pas.librarianwebapp.repository.events;

import pl.lodz.pas.librarianwebapp.repository.events.data.BookLock;
import pl.lodz.pas.librarianwebapp.repository.events.data.Event;
import pl.lodz.pas.librarianwebapp.repository.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;

import java.util.UUID;

public interface EventsRepository {

    void addEvent(Event event) throws ObjectAlreadyExistsException, RepositoryException;

    Boolean isBookAvailable(UUID uuid);

    void saveBookLock(BookLock lock) throws InconsistencyFoundException;

    void deleteBookLock(UUID uuid, String user);
}