package pl.lodz.pas.librarianwebapp.web.lendings;

import pl.lodz.pas.librarianwebapp.services.BooksService;
import pl.lodz.pas.librarianwebapp.services.dto.BookCopyDto;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Named("lendingsCartController")
@SessionScoped
public class LendingsCartControllerBean implements Serializable {

    private final Set<BookCopyDto> books = new HashSet<>();

    @Inject
    private BooksService service;

    public String addToCart(String isbn, BookCopyDto.State state) {

        var userLogin = getLogin();

        var bookCopy = service.lockBook(isbn, userLogin, state);

        if (bookCopy.isEmpty()) {
            return "error.xhtml?faces-redirect=true";
        }

        books.add(bookCopy.get());

        System.out.println(books);

        return "list.xhtml?faces-redirect=true";
    }

    private String getLogin() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

    public String removeFromCart(BookCopyDto bookCopy) {

        var userLogin = getLogin();

        books.remove(bookCopy);

        service.unlockBook(userLogin, bookCopy);

        return "cart.xhtml?faces-redirect=true";
    }

    List<BookCopyDto> getCartPositions() {
        return new ArrayList<>(books);
    }
}
