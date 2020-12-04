package pl.lodz.pas.librarianwebapp.repository.books.data;

import java.util.Objects;
import java.util.UUID;

public abstract class Element<T extends Element<?>> {
    private final UUID uuid;
    private String publisher;
    private String title;

    public Element(UUID uuid, String publisher, String title) {
        this.uuid = uuid;
        this.publisher = publisher;
        this.title = title;
    }

    public Element(String publisher, String title) {
        this(UUID.randomUUID(), publisher, title);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public abstract T copy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Element<?> element = (Element<?>) o;
        return Objects.equals(uuid, element.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
