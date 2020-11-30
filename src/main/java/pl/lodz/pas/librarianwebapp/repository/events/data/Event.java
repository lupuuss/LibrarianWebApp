package pl.lodz.pas.librarianwebapp.repository.events.data;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class Event {

    private final UUID uuid;
    private final LocalDateTime date;
    private final String customerLogin;

    public Event(UUID uuid, LocalDateTime date, String customerLogin) {
        this.uuid = uuid;
        this.date = date;
        this.customerLogin = customerLogin;
    }

    public Event(LocalDateTime date, String customerLogin) {
        this(UUID.randomUUID(), date, customerLogin);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCustomerLogin() {
        return customerLogin;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return uuid.equals(event.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
