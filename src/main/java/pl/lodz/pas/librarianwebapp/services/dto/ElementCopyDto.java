package pl.lodz.pas.librarianwebapp.services.dto;

public class ElementCopyDto {

    private final  int number;
    private final  ElementDto element;

    public enum State {
        NEW, GOOD, USED, NEED_REPLACEMENT, DAMAGED
    }

    private State state;

    public ElementCopyDto(int number,ElementDto element,  State state) {
        this.element = element;
        this.number = number;
        this.state = state;
    }

    public ElementDto getElement() {
        return element;
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
}
