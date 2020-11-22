package pl.lodz.pas.librarianwebapp.web.lendings;

import pl.lodz.pas.librarianwebapp.services.dto.BookCopyDto;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Named("lendingsCartController")
@SessionScoped
public class LendingsCartControllerBean implements Serializable {

    private Set<BookCopyDto> books = new HashSet<>();


    void addToCart(BookCopyDto bookCopy) {

        books.add(bookCopy);
    }
}
