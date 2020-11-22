package pl.lodz.pas.librarianwebapp.repository.exceptions;

public class InconsistencyFoundException extends RepositoryException {
    public InconsistencyFoundException(String message) {
        super(message, null);
    }
}
