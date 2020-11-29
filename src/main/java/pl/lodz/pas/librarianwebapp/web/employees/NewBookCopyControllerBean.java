package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.BooksService;
import pl.lodz.pas.librarianwebapp.web.ElementCopyStateI18n;

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
    private ElementCopyStateI18n state;


    public List<String> getIsbns() {

        return booksService.getAllIsbns();
    }

    public ElementCopyStateI18n getState() {
        return state;
    }

    public void setState(ElementCopyStateI18n state) {
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
            return "elements.xhtml?faces-redirect=true";
        } else {
            return "creationFailed.xhtml?faces-redirect=true";
        }
    }

    public List<ElementCopyStateI18n> getStates() {
        return Arrays.asList(ElementCopyStateI18n.values());
    }
}
