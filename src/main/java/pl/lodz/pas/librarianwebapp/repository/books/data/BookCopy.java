package pl.lodz.pas.librarianwebapp.repository.books.data;

import java.util.UUID;

public class BookCopy extends ElementCopy<BookCopy> {

    public BookCopy(UUID uuid, UUID bookUuid, int number, State state) {
        super(uuid, bookUuid, number, state);
    }

    public BookCopy(UUID bookUuid, int number, State state) {
        super(bookUuid, number, state);
    }

    @Override
    public BookCopy copy() {
        return new BookCopy(
                getUuid(),
                getElementUuid(),
                getNumber(),
                getState()
        );
    }
}
