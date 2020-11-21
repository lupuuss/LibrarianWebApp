package pl.lodz.pas.librarianwebapp.model.repositories.exceptions;

public class InconsistencyFoundException extends RepositoryException {
    public InconsistencyFoundException(String message) {
        super(message, null);
    }
}
