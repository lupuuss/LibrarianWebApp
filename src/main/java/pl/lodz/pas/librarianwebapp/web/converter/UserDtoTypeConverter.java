package pl.lodz.pas.librarianwebapp.web.converter;

import pl.lodz.pas.librarianwebapp.services.dto.UserDto;
import pl.lodz.pas.librarianwebapp.web.UserTypeI18n;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("pl.lodz.pas.librarianwebapp.web.converter.UserDtoTypeConverter")
public class UserDtoTypeConverter implements Converter<UserDto.Type> {
    @Override
    public UserDto.Type getAsObject(FacesContext context, UIComponent component, String value) {
        return UserTypeI18n.fromTranslatedString(value).toUserType();
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, UserDto.Type value) {
        return UserTypeI18n.from(value).getTranslated();
    }
}



