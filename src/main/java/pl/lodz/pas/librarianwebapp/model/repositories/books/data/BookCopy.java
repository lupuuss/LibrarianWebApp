package pl.lodz.pas.librarianwebapp.model.repositories.books.data;

import java.util.Objects;
import java.util.UUID;

public class BookCopy {

    public enum State {
        NEW, GOOD, USED, NEED_REPLACEMENT, DAMAGED
    }

    private final UUID uuid;

    private String bookIsbn;
    private State state;

    public BookCopy(UUID uuid, String bookIsbn, State state) {
        this.uuid = uuid;
        this.bookIsbn = bookIsbn;
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookCopy bookCopy = (BookCopy) o;
        return uuid.equals(bookCopy.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
