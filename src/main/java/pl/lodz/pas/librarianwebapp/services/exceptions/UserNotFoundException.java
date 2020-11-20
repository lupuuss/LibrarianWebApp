package pl.lodz.pas.librarianwebapp.services.exceptions;

public class UserNotFoundException extends ServiceException {

    public UserNotFoundException(String login) {
        super("User with login '" + login + "' not found!", null);
    }
}
