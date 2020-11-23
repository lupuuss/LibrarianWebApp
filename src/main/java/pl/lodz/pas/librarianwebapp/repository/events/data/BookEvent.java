package pl.lodz.pas.librarianwebapp.repository.events.data;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BookEvent extends Event {

    private final UUID bookUuid;

    public BookEvent(UUID uuid, LocalDateTime date, String customerLogin, UUID bookUuid) {
        super(uuid, date, customerLogin);
        this.bookUuid = bookUuid;
    }

    public UUID getBookUuid() {
        return bookUuid;
    }
}
