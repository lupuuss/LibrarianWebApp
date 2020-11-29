package pl.lodz.pas.librarianwebapp.web.lendings;

import pl.lodz.pas.librarianwebapp.services.BooksService;
import pl.lodz.pas.librarianwebapp.services.dto.BookDto;
import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;
import pl.lodz.pas.librarianwebapp.services.dto.ElementDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.Set;

@Named("lendingsController")
@RequestScoped
public class LendingsControllerBean {

    @Inject
    private BooksService service;

    public Set<Map.Entry<ElementDto, Long>> getAvailableBooksCopiesCount() {
        return service.getAvailableCopiesCount()
                .entrySet();
    }
}
