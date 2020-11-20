package pl.lodz.pas.librarianwebapp.model.repositories.exceptions;

public class DtoAlreadyExistsException extends RepositoryException {
    public DtoAlreadyExistsException(String type, String identifier) {
        super("Object of type: '" + type + "' with identifier '"+ identifier+ "' already exits!", null);
    }
}
