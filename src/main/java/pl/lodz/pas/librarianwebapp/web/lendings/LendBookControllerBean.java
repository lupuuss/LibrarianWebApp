package pl.lodz.pas.librarianwebapp.web.lendings;

import pl.lodz.pas.librarianwebapp.services.BooksService;
import pl.lodz.pas.librarianwebapp.services.dto.BookDto;
import pl.lodz.pas.librarianwebapp.web.BookCopyStateI18n;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named("lendBookController")
@ViewScoped
public class LendBookControllerBean implements Serializable {

    @Inject
    private BooksService service;

    @Inject
    private LendingsCartControllerBean lendingsCartControllerBean;

    private BookDto book;

    private List<BookCopyStateI18n> states;

    private String isbn;

    private BookCopyStateI18n state;

    public void initializeBook() {
        var book = service.getBook(isbn);

        if (book.isEmpty()) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("error.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        this.book = book.get();
        this.states = service.getAvailableStatesForBook(isbn)
                .stream()
                .map(BookCopyStateI18n::from)
                .collect(Collectors.toList());
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BookDto getBook() {
        return book;
    }

    public void setBook(BookDto book) {
        this.book = book;
    }

    public BookCopyStateI18n getState() {
        return state;
    }

    public void setState(BookCopyStateI18n state) {

        this.state = state;
    }

    public List<BookCopyStateI18n> getAvailableStates() {
        return states;
    }

    public String addCurrentBookToCart() {

        return lendingsCartControllerBean.addToCart(isbn, state.toBookCopyState());
    }
}
