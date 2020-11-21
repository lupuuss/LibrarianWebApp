package pl.lodz.pas.librarianwebapp.web.converter;

import pl.lodz.pas.librarianwebapp.services.dto.BookCopyDto;
import pl.lodz.pas.librarianwebapp.web.BookCopyStateI18n;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("pl.lodz.pas.librarianwebapp.web.converter.BookCopyDtoStateConverter")
public class BookCopyDtoStateConverter implements Converter<BookCopyDto.State> {
    @Override
    public BookCopyDto.State getAsObject(FacesContext context, UIComponent component, String value) {
        return BookCopyStateI18n.fromTranslatedString(value).toBookCopyState();
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, BookCopyDto.State value) {
        return BookCopyStateI18n.from(value).getTranslated();
    }
}
