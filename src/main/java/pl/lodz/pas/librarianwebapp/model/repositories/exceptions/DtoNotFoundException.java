package pl.lodz.pas.librarianwebapp.model.repositories.exceptions;

public class DtoNotFoundException extends RepositoryException {
    public DtoNotFoundException(String type, String identifier) {
        super("Object of type '" + type + "' with identifier '" + identifier + "' doesn't exist!", null);
    }
}
