package pl.lodz.pas.librarianwebapp.web.localization;

import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public enum ElementCopyStateI18n {
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

    public ElementCopyDto.State toElementState() {
        return ElementCopyDto.State.valueOf(this.name());
    }

    public static ElementCopyStateI18n from(ElementCopyDto.State state) {

        return ElementCopyStateI18n.valueOf(state.name());
    }

    public static ElementCopyStateI18n fromTranslatedString(String translated) {

        var map = Arrays.stream(ElementCopyStateI18n.values())
                .collect(Collectors.toMap(ElementCopyStateI18n::getTranslated, state -> state));

        return map.get(translated);
    }
}
