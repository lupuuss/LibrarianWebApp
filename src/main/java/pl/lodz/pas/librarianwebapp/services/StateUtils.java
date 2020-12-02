package pl.lodz.pas.librarianwebapp.services;

import pl.lodz.pas.librarianwebapp.repository.books.data.ElementCopy;
import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;

public class StateUtils {

    public static ElementCopyDto.State mapState(ElementCopy.State state) {
        return ElementCopyDto.State.valueOf(state.name());
    }

    public static ElementCopy.State mapState(ElementCopyDto.State state) {

        switch (state) {

            case NEW:
                return ElementCopy.State.NEW;
            case GOOD:
                return ElementCopy.State.GOOD;
            case USED:
                return ElementCopy.State.USED;
            case NEED_REPLACEMENT:
                return ElementCopy.State.NEED_REPLACEMENT;
            case DAMAGED:
                return ElementCopy.State.DAMAGED;
        }

        throw new IllegalArgumentException("Mapping for passed state not found!");
    }
}
