package pl.lodz.pas.librarianwebapp.repository.books.data;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class BookCopy {

    public enum State {
        NEW(3), GOOD(2), USED(1), NEED_REPLACEMENT(0), DAMAGED(-1);

        private final int level;

        State(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        public static State degrade(State state) {

            if (state.getLevel() == -1) {
                return DAMAGED;
            }

            if (state.getLevel() == 0) {
                return NEED_REPLACEMENT;
            }

            var level = state.getLevel() - 1;

            return Arrays.stream(values())
                    .filter(s -> s.getLevel() == level)
                    .findFirst()
                    .orElseThrow();

        }
    }

    private final UUID uuid;

    private final int number;

    private String bookIsbn;
    private State state;

    public BookCopy(UUID uuid, int number, String bookIsbn, State state) {
        this.uuid = uuid;
        this.number = number;
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

    public int getNumber() {
        return number;
    }

    public BookCopy copy() {
        return new BookCopy(
                uuid,
                number,
                bookIsbn,
                state
        );
    }
}
