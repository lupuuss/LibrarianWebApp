package pl.lodz.pas.librarianwebapp.web.lendings;

import pl.lodz.pas.librarianwebapp.DateProvider;
import pl.lodz.pas.librarianwebapp.services.BooksService;
import pl.lodz.pas.librarianwebapp.services.dto.BookCopyDto;
import pl.lodz.pas.librarianwebapp.services.dto.BookLockDto;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Named("lendingsCartController")
@SessionScoped
public class LendingsCartControllerBean implements Serializable {

    @Inject
    private DateProvider dateProvider;

    private final Set<BookLockDto> bookLocks = new HashSet<>();

    private Map<BookLockDto, Boolean> markedPositions = new HashMap<>();

    @Inject
    private BooksService service;

    public String addToCart(String isbn, BookCopyDto.State state) {

        var userLogin = getLogin();

        var bookCopy = service.lockBook(isbn, userLogin, state);

        if (bookCopy.isEmpty()) {
            return "error.xhtml?faces-redirect=true";
        }

        bookLocks.add(bookCopy.get());

        System.out.println(bookLocks);

        return "books.xhtml?faces-redirect=true";
    }

    private String getLogin() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

    public String removeMarkedPositions() {

        var marked = markedPositions.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (var bookLock : marked) {
            var userLogin = getLogin();

            bookLocks.remove(bookLock);

            service.unlockBook(userLogin, bookLock.getCopy());
        }

        return "cart.xhtml?faces-redirect=true";
    }

    public List<BookLockDto> getCartPositions() {

        cleanOutdatedCartPositions();
        return new ArrayList<>(bookLocks);
    }

    private void cleanOutdatedCartPositions() {
        bookLocks.removeIf(lock -> lock.getUntil().isBefore(dateProvider.now()));
        markedPositions.entrySet().removeIf(entry -> !bookLocks.contains(entry.getKey()));
    }


    public Map<BookLockDto, Boolean> getMarkedPositions() {

        cleanOutdatedCartPositions();
        return markedPositions;
    }

    public void setMarkedPositions(Map<BookLockDto, Boolean> markedPositions) {
        this.markedPositions = markedPositions;
    }
}
