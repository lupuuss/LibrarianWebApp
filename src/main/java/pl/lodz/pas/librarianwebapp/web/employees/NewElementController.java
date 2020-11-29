package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.ElementsService;
import pl.lodz.pas.librarianwebapp.services.dto.BookDto;
import pl.lodz.pas.librarianwebapp.services.dto.ElementDto;
import pl.lodz.pas.librarianwebapp.services.dto.MagazineDto;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;


@Named("newElementController")
@ViewScoped
public class NewElementController implements Serializable {

    @Inject
    private ElementsService elementsService;

    private ElementDto elementDto;

    private String elementType;

    public void initializeElement() {

        if (elementType.toLowerCase().equals("magazine")) {
            elementDto = new MagazineDto();
        } else {
            elementDto = new BookDto();
        }
    }

    public ElementDto getElement() {
        return elementDto;
    }

    public void setElement(ElementDto elementDto) {
        this.elementDto = elementDto;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String createElement() {

        var result = elementsService.addElement(elementDto);

        if (result) {
            return "elements.xhtml?faces-redirect=true";
        } else {
            return "creationFailed.xhtml?faces-redirect=true";
        }
    }
}


