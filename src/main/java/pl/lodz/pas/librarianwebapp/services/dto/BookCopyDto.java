package pl.lodz.pas.librarianwebapp.services.dto;

import pl.lodz.pas.librarianwebapp.repository.books.data.Book;

public class BookCopyDto {

    private final Book book;

    private final int number;

    public enum State {
        NEW, GOOD, USED, NEED_REPLACEMENT, DAMAGED
    }

    private State state;

    public BookCopyDto(int number, Book book, State state) {
        this.number = number;
        this.book = book;
        this.state = state;
    }

    public int getNumber() {
        return number;
    }

    public Book getBook() {
        return book;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
