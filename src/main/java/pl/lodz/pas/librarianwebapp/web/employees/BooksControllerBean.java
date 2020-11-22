package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.BooksService;
import pl.lodz.pas.librarianwebapp.services.dto.BookCopyDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("booksController")
@RequestScoped
public class BooksControllerBean {

    @Inject
    private BooksService booksService;
    private Map<BookCopyDto, Boolean> marks = new HashMap<>();

    public List<BookCopyDto> getAllCopies() {
        return booksService.getAllCopies();
    }

    public Map<BookCopyDto, Boolean> getMarks() {
        return marks;
    }

    public void setMarks(Map<BookCopyDto, Boolean> marks) {
        this.marks = marks;
    }

    public String degradeMarkedCopies() {

        booksService.degradeCopies(getMarkedCopies());

        return "books.xhtml?faces-redirect=true";
    }

    public String removeMarkedCopies() {

        booksService.deleteBookCopies(getMarkedCopies());

        return "books.xhtml?faces-redirect=true";
    }

    private List<BookCopyDto> getMarkedCopies() {
        return marks.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
