package pl.lodz.pas.librarianwebapp.services.exceptions;

public class ServiceException extends Exception {

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
