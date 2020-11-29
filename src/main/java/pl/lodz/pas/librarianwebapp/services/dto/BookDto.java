package pl.lodz.pas.librarianwebapp.services.dto;

public class BookDto extends ElementDto {

    private String isbn;

    private String author;

    public BookDto(String title, String publisher, String isbn, String author) {
        super(title, publisher);
        this.isbn = isbn;
        this.author = author;
    }

    public BookDto() {
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }
}


