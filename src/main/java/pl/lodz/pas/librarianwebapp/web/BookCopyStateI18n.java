package pl.lodz.pas.librarianwebapp.web;

import pl.lodz.pas.librarianwebapp.services.dto.BookCopyDto;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public enum BookCopyStateI18n {
    NEW, GOOD, USED, NEED_REPLACEMENT, DAMAGED;

    public String getTranslated() {

        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        switch (this) {
            case NEW:
                return bundle.getString("page.books.state.new");
            case GOOD:
                return bundle.getString("page.books.state.good");
            case USED:
                return bundle.getString("page.books.state.used");
            case NEED_REPLACEMENT:
                return bundle.getString("page.books.state.needreplacement");
            case DAMAGED:
                return bundle.getString("page.books.state.damaged");
        }

        throw new IllegalArgumentException("Mapping not found!");
    }

    @Override
    public String toString() {
        return getTranslated();
    }

    public BookCopyDto.State toBookCopyState() {
        return BookCopyDto.State.valueOf(this.name());
    }

    public static BookCopyStateI18n from(BookCopyDto.State state) {

        return BookCopyStateI18n.valueOf(state.name());
    }

    public static BookCopyStateI18n fromTranslatedString(String translated) {

        var map = Arrays.stream(BookCopyStateI18n.values())
                .collect(Collectors.toMap(BookCopyStateI18n::getTranslated, state -> state));
        return map.get(translated);
    }
}
