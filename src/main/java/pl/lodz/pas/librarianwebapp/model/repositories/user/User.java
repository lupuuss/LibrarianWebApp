package pl.lodz.pas.librarianwebapp.model.repositories.user;

import java.util.UUID;

public class User {

    private UUID uuid;

    private String login;

    private String firstName;

    private String lastName;

    private String email;

    private boolean active;

    public User(UUID uuid, String login, String firstName, String lastName, String email, boolean active) {
        this.uuid = uuid;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public User copy() {
        return new User(
                uuid,
                login,
                firstName,
                lastName,
                email,
                active
        );
    }
}
