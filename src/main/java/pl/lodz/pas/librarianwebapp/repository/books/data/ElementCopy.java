package pl.lodz.pas.librarianwebapp.repository.books.data;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public abstract class ElementCopy <T extends ElementCopy<?>> {

    public enum State {
        NEW(3), GOOD(2), USED(1), NEED_REPLACEMENT(0), DAMAGED(-1);

        private final int level;

        State(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        public static State degrade(BookCopy.State state) {

            if (state.getLevel() == -1) {
                return DAMAGED;
            }

            if (state.getLevel() == 0) {
                return NEED_REPLACEMENT;
            }

            var level = state.getLevel() - 1;

            return Arrays.stream(values())
                    .filter(s -> s.getLevel() == level)
                    .findFirst()
                    .orElseThrow();

        }
    }

    private final UUID uuid;

    private final UUID elementUuid;

    private final int number;

    private State state;

    public ElementCopy(UUID uuid, UUID elementUuid, int number, State state) {
        this.uuid = uuid;
        this.elementUuid = elementUuid;
        this.number = number;
        this.state = state;
    }

    public ElementCopy(UUID elementUuid, int number, State state) {
        this(UUID.randomUUID(), elementUuid, number, state);
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getElementUuid() {
        return elementUuid;
    }

    public int getNumber() {
        return number;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementCopy<?> that = (ElementCopy<?>) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    abstract T copy();
}
