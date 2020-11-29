package pl.lodz.pas.librarianwebapp.repository.events.data;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ElementLock {

    private UUID elementUuid;
    private String userLogin;
    private LocalDateTime until;

    public ElementLock(UUID elementUuid, String userLogin, LocalDateTime until) {
        this.elementUuid = elementUuid;
        this.userLogin = userLogin;
        this.until = until;
    }

    public UUID getElementUuid() {
        return elementUuid;
    }

    public void setElementUuid(UUID elementUuid) {
        this.elementUuid = elementUuid;
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
        ElementLock bookLock = (ElementLock) o;
        return elementUuid.equals(bookLock.elementUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementUuid);
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
}
