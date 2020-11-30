package pl.lodz.pas.librarianwebapp.repository.events.data;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class ElementEvent extends Event {

    private final UUID elementUuid;

    public ElementEvent(UUID uuid, LocalDateTime date, String customerLogin, UUID elementUuid) {
        super(uuid, date, customerLogin);
        this.elementUuid = elementUuid;
    }

    public ElementEvent(LocalDateTime date, String customerLogin, UUID elementUuid) {
        super(date, customerLogin);
        this.elementUuid = elementUuid;
    }

    public UUID getElementUuid() {
        return elementUuid;
    }
}
