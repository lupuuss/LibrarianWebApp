package pl.lodz.pas.librarianwebapp.services.data;

public class User {

    private final String login;

    private final String firstName;

    private final String lastName;

    private final String email;

    private final boolean active;

    public User(String login, String firstName, String lastName, String email, boolean active) {
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
