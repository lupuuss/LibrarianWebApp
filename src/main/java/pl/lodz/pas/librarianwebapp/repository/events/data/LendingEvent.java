package pl.lodz.pas.librarianwebapp.repository.events.data;

import pl.lodz.pas.librarianwebapp.repository.events.data.BookEvent;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class LendingEvent extends BookEvent {

    private UUID returnUuid;

    public LendingEvent(UUID uuid, LocalDateTime date, String customerLogin, UUID bookUuid) {
        super(uuid, date, customerLogin, bookUuid);
    }

    public void setReturnUuid(UUID returnUuid) {
        this.returnUuid = returnUuid;
    }

    public Optional<UUID> getReturnUuid() {
        return Optional.ofNullable(returnUuid);
    }
}
