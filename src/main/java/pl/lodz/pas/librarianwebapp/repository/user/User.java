package pl.lodz.pas.librarianwebapp.repository.user;

import java.util.Objects;
import java.util.UUID;

public class User {

    public enum Type {
        ADMIN, EMPLOYEE, USER
    }

    private UUID uuid;

    private String login;

    private String firstName;

    private String lastName;

    private String email;

    private Type type;

    private boolean active;

    public User(UUID uuid, String login, String firstName, String lastName, String email, Type type, boolean active) {
        this.uuid = uuid;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.type = type;
        this.active = active;
    }

    public User(String login, String firstName, String lastName, String email, Type type, boolean active) {
        this.uuid = UUID.randomUUID();
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.type = type;
        this.active = active;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
                type, active
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return uuid.equals(user.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid=" + uuid +
                ", login='" + login + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}
