package pl.lodz.pas.librarianwebapp.repository.books.data;

import java.util.Objects;
import java.util.UUID;

public class Book extends Element<Book> {

    private final String isbn;

    private String author;

    public Book(UUID uuid, String isbn, String title, String author, String publisher) {
        super(uuid, publisher, title);
        this.isbn = isbn;
        this.author = author;
    }

    public Book(String isbn, String title, String author, String publisher) {
        super(publisher, title);
        this.isbn = isbn;
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public Book copy() {
        return new Book(
                getUuid(),
                isbn,
                getTitle(),
                author,
                getPublisher()
        );
    }
}
