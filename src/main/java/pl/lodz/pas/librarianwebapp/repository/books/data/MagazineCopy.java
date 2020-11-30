package pl.lodz.pas.librarianwebapp.repository.books.data;

import java.util.UUID;

public class MagazineCopy extends ElementCopy<MagazineCopy> {


    public MagazineCopy(UUID uuid, UUID elementUuid, int number, State state) {
        super(uuid, elementUuid, number, state);
    }

    public MagazineCopy(UUID elementUuid, int number, State state) {
        super(elementUuid, number, state);
    }


    @Override
    public MagazineCopy copy() {
        return new MagazineCopy(
                getUuid(),
                getElementUuid(),
                getNumber(),
                getState()
        );
    }
}
