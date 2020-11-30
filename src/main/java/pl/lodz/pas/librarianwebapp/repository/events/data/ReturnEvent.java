package pl.lodz.pas.librarianwebapp.repository.events.data;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReturnEvent extends ElementEvent {

    enum PaymentCause {
        NONE, DAMAGED, LATE
    }

    private final PaymentCause cause;
    private final double amount;

    public ReturnEvent(UUID uuid, LocalDateTime date, String customerLogin, UUID elementUuid, PaymentCause cause, double amount) {
        super(uuid, date, customerLogin, elementUuid);
        this.cause = cause;
        this.amount = amount;
    }

    public ReturnEvent(LocalDateTime date, String customerLogin, UUID elementUuid, PaymentCause cause, double amount) {
        super(UUID.randomUUID(), date, customerLogin, elementUuid);
        this.cause = cause;
        this.amount = amount;
    }


    public ReturnEvent(UUID uuid, LocalDateTime date, String customerLogin, UUID elementUuid) {
        this(uuid, date, customerLogin, elementUuid, PaymentCause.NONE, 0.0);
    }

    public ReturnEvent(LocalDateTime date, String customerLogin, UUID elementUuid) {
        this(date, customerLogin, elementUuid, PaymentCause.NONE, 0.0);
    }

    public PaymentCause getCause() {
        return cause;
    }

    public double getAmount() {
        return amount;
    }
}
