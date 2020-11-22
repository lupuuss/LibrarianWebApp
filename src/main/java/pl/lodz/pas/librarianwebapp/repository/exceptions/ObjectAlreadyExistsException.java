package pl.lodz.pas.librarianwebapp.repository.exceptions;

public class ObjectAlreadyExistsException extends RepositoryException {
    public ObjectAlreadyExistsException(String type, String identifier) {
        super("Object of type: '" + type + "' with identifier '"+ identifier+ "' already exits!", null);
    }
}
