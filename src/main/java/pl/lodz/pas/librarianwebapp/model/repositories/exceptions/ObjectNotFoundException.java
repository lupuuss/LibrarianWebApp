package pl.lodz.pas.librarianwebapp.model.repositories.exceptions;

public class ObjectNotFoundException extends RepositoryException {
    public ObjectNotFoundException(String type, String identifier) {
        super("Object of type '" + type + "' with identifier '" + identifier + "' doesn't exist!", null);
    }
}
