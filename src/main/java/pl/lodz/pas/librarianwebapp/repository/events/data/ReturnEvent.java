package pl.lodz.pas.librarianwebapp.repository.events.data;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReturnEvent extends BookEvent {

    enum PaymentCause {
        NONE, DAMAGED, LATE
    }

    private final PaymentCause cause;
    private final double amount;

    public ReturnEvent(UUID uuid, LocalDateTime date, String customerLogin, UUID bookUuid, PaymentCause cause, double amount) {
        super(uuid, date, customerLogin, bookUuid);
        this.cause = cause;
        this.amount = amount;
    }

    public ReturnEvent(UUID uuid, LocalDateTime date, String customerLogin, UUID bookUuid) {
        this(uuid, date, customerLogin, bookUuid, PaymentCause.NONE, 0.0);
    }

    public PaymentCause getCause() {
        return cause;
    }

    public double getAmount() {
        return amount;
    }
}
