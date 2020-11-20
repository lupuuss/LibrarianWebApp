package pl.lodz.pas.librarianwebapp.services.dto;

public class UserDto {

    private final String login;

    private final String firstName;

    private final String lastName;

    private final String email;

    private final boolean active;

    public UserDto(String login, String firstName, String lastName, String email, boolean active) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }
}
