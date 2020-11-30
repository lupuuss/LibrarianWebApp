package pl.lodz.pas.librarianwebapp.services.dto;

public class UserDto {

    public enum Type {
        ADMIN, EMPLOYEE, USER
    }

    private String login;

    private String firstName;

    private String lastName;

    private String email;

    private Type type;

    private boolean active;

    public UserDto(String login, String firstName, String lastName, String email, Type type, boolean active) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.type = type;
        this.active = active;
    }

    public UserDto() {
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

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
