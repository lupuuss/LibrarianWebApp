package pl.lodz.pas.librarianwebapp.repository.events.data;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class BookLock {

    private UUID bookUuid;
    private String userLogin;
    private LocalDateTime until;

    public BookLock(UUID bookUuid, String userLogin, LocalDateTime until) {
        this.bookUuid = bookUuid;
        this.userLogin = userLogin;
        this.until = until;
    }

    public UUID getBookUuid() {
        return bookUuid;
    }

    public void setBookUuid(UUID bookUuid) {
        this.bookUuid = bookUuid;
    }

    public LocalDateTime getUntil() {
        return until;
    }

    public void setUntil(LocalDateTime until) {
        this.until = until;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookLock bookLock = (BookLock) o;
        return bookUuid.equals(bookLock.bookUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookUuid);
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
}
