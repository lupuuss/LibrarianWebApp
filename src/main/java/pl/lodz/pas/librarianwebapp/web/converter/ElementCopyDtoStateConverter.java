package pl.lodz.pas.librarianwebapp.web.converter;

import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;
import pl.lodz.pas.librarianwebapp.web.ElementCopyStateI18n;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("pl.lodz.pas.librarianwebapp.web.converter.ElementCopyDtoStateConverter")
public class ElementCopyDtoStateConverter implements Converter<ElementCopyDto.State> {
    @Override
    public ElementCopyDto.State getAsObject(FacesContext context, UIComponent component, String value) {
        return ElementCopyStateI18n.fromTranslatedString(value).toBookCopyState();
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, ElementCopyDto.State value) {
        return ElementCopyStateI18n.from(value).getTranslated();
    }
}
