package pl.lodz.pas.librarianwebapp.web;

import pl.lodz.pas.librarianwebapp.services.dto.UserDto;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public enum UserTypeI18n {
    ADMIN, EMPLOYEE, USER, EMPTY;


    public String getTranslated() {

        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        switch (this) {

            case ADMIN:
                return bundle.getString("page.users.type.admin");
            case EMPLOYEE:
                return bundle.getString("page.users.type.employee");
            case USER:
                return bundle.getString("page.users.type.user");
            case EMPTY:
                    return " - ";
        }

        throw new IllegalArgumentException("Mapping not found!");
    }

    @Override
    public String toString() {
        return getTranslated();
    }

    public UserDto.Type toUserType() {
        return UserDto.Type.valueOf(this.name());
    }

    public static UserTypeI18n from(UserDto.Type type) {

        return UserTypeI18n.valueOf(type.name());
    }

    public static UserTypeI18n fromTranslatedString(String translated) {

        var map = Arrays.stream(UserTypeI18n.values())
                .collect(Collectors.toMap(UserTypeI18n::getTranslated, type -> type));

        return map.get(translated);
    }
}