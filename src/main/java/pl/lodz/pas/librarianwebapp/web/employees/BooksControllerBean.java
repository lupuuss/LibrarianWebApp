package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.BooksService;
import pl.lodz.pas.librarianwebapp.services.dto.BookCopyDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named("booksController")
@RequestScoped
public class BooksControllerBean {

    @Inject
    private BooksService booksService;

    public List<BookCopyDto> getAllCopies() {
        return booksService.getAllCopies();
    }
}
