package pl.lodz.pas.librarianwebapp.services.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ElementLockDto {


    private ElementCopyDto copy;
    private String userLogin;
    private LocalDateTime until;

    public ElementLockDto(ElementCopyDto copy, String userLogin, LocalDateTime until) {
        this.copy = copy;
        this.userLogin = userLogin;
        this.until = until;
    }

    public ElementCopyDto getCopy() {
        return copy;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public LocalDateTime getUntil() {
        return until;
    }

    public void setCopy(ElementCopyDto copy) {
        this.copy = copy;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public void setUntil(LocalDateTime until) {
        this.until = until;
    }
}
