package pl.lodz.pas.librarianwebapp.web.lendings;

import pl.lodz.pas.librarianwebapp.DateProvider;
import pl.lodz.pas.librarianwebapp.services.LendingsService;
import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;
import pl.lodz.pas.librarianwebapp.services.dto.ElementLockDto;
import pl.lodz.pas.librarianwebapp.web.MarksController;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Named("lendingsCartController")
@SessionScoped
public class LendingsCartControllerBean extends MarksController<ElementLockDto> implements Serializable {

    @Inject
    private DateProvider dateProvider;

    private final Set<ElementLockDto> elementLocks = new HashSet<>();
    @Inject
    private LendingsService service;

    public String addToCart(String isbn,ElementCopyDto.State state) {

        var userLogin = getLogin();

        var bookCopy = service.lockBook(isbn, userLogin, state);

        if (bookCopy.isEmpty()) {
            return "/error.xhtml?faces-redirect=true&errorId=addToCartError";
        }

        elementLocks.add(bookCopy.get());

        return "elements.xhtml?faces-redirect=true";
    }

    public String addToCart(String issn, Integer issue, ElementCopyDto.State state){
        var userLogin = getLogin();

        var magazineCopy = service.lockMagazine(issn,issue, userLogin, state);

        if (magazineCopy.isEmpty()) {
            return "/error.xhtml?faces-redirect=true&errorId=addToCartError";
        }

        elementLocks.add(magazineCopy.get());

        return "elements.xhtml?faces-redirect=true";
    }

    private String getLogin() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

    public String removeMarked() {

        var marked = getMarks().entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (var bookLock : marked) {
            var userLogin = getLogin();

            elementLocks.remove(bookLock);

            service.unlockElement(userLogin, bookLock.getCopy());
        }

        return "cart.xhtml?faces-redirect=true";
    }

    public List<ElementLockDto> getCartPositions() {

        cleanOutdatedCartPositions();
        return new ArrayList<>(elementLocks);
    }

    private void cleanOutdatedCartPositions() {
        elementLocks.removeIf(lock -> lock.getUntil().isBefore(dateProvider.now()));
        getMarkedItems().removeIf(item -> !elementLocks.contains(item));
    }

    public String lendElements() {

        if (elementLocks.isEmpty()) return "";

        var result = service.lendLockedElements(elementLocks, getLogin());

        if (result) {
            elementLocks.clear();
            return "elementsList.xhtml?faces-redirect=true";
        } else {
            return "/error.xhtml?faces-redirect=true&errorId=lendError";
        }
    }
}
