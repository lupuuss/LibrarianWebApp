package pl.lodz.pas.librarianwebapp.repository.books.data;

import java.util.UUID;

public abstract class Element<T extends Element> {
    private final UUID uuid;
    private final String publisher;
    private final String title;

    public Element(UUID uuid, String publisher, String title) {
        this.uuid = uuid;
        this.publisher = publisher;
        this.title = title;
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

    public abstract T copy();
}
