package pl.lodz.pas.librarianwebapp.web.lendings;

import pl.lodz.pas.librarianwebapp.services.BooksService;
import pl.lodz.pas.librarianwebapp.services.dto.BookCopyDto;
import pl.lodz.pas.librarianwebapp.services.dto.BookDto;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Named("lendingsController")
@RequestScoped
public class LendingsControllerBean {

    @Inject
    private BooksService service;

    public Set<Map.Entry<BookDto, Long>> getAvailableBooksCopiesCount() {
        return service.getAvailableCopiesCount()
                .entrySet();
    }
}
