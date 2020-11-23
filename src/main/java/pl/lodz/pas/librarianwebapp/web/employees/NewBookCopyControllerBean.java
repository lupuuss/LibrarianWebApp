package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.BooksService;
import pl.lodz.pas.librarianwebapp.web.BookCopyStateI18n;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

@Named("newBookCopyController")
@RequestScoped
public class NewBookCopyControllerBean {

    @Inject
    private BooksService booksService;

    private String isbn;
    private BookCopyStateI18n state;


    public List<String> getIsbns() {

        return booksService.getAllIsbns();
    }

    public BookCopyStateI18n getState() {
        return state;
    }

    public void setState(BookCopyStateI18n state) {
        this.state = state;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String createBookCopy() {

        var result = booksService.addCopy(isbn, state.toBookCopyState());

        if (result) {
            return "books.xhtml?faces-redirect=true";
        } else {
            return "creationFailed.xhtml?faces-redirect=true";
        }
    }

    public List<BookCopyStateI18n> getStates() {
        return Arrays.asList(BookCopyStateI18n.values());
    }
}
