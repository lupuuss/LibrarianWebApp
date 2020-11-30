package pl.lodz.pas.librarianwebapp.repository.events.data;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class LendingEvent extends ElementEvent {

    private UUID returnUuid;

    public LendingEvent(UUID uuid, LocalDateTime date, String customerLogin, UUID elementUuid) {
        super(uuid, date, customerLogin, elementUuid);
    }

    public LendingEvent(LocalDateTime date, String customerLogin, UUID elementUuid) {
        super(date, customerLogin, elementUuid);
    }

    public void setReturnUuid(UUID returnUuid) {
        this.returnUuid = returnUuid;
    }

    public Optional<UUID> getReturnUuid() {
        return Optional.ofNullable(returnUuid);
    }
}
