package pl.lodz.pas.librarianwebapp.services.dto;

import java.util.Date;

public class LendEventDto {

    private ElementCopyDto copy;
    private Date lendDate;
    private Date returnDate;
    private String login;

    public LendEventDto(ElementCopyDto copy, Date lendDate, Date returnDate, String login) {
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

    public Date getLendDate() {
        return lendDate;
    }

    public void setLendDate(Date lendDate) {
        this.lendDate = lendDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
