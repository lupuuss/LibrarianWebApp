package pl.lodz.pas.librarianwebapp.services;

import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;

import java.time.LocalDateTime;

public class LendEventDto {

    private ElementCopyDto copy;
    private LocalDateTime lendDate;
    private LocalDateTime returnDate;
    private String login;

    public LendEventDto(ElementCopyDto copy, LocalDateTime lendDate, LocalDateTime returnDate, String login) {
        this.copy = copy;
        this.lendDate = lendDate;
        this.returnDate = returnDate;
        this.login = login;
    }

    public ElementCopyDto getCopy() {
        return copy;
    }

    public void setCopy(ElementCopyDto copy) {
        this.copy = copy;
    }

    public LocalDateTime getLendDate() {
        return lendDate;
    }

    public void setLendDate(LocalDateTime lendDate) {
        this.lendDate = lendDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
