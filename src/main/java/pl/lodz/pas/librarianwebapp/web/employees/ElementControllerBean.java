package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.ElementsService;
import pl.lodz.pas.librarianwebapp.services.dto.BookDto;
import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;
import pl.lodz.pas.librarianwebapp.services.dto.ElementDto;
import pl.lodz.pas.librarianwebapp.services.dto.MagazineDto;
import pl.lodz.pas.librarianwebapp.web.ElementCopyStateI18n;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Named("elementController")
@ViewScoped
public class ElementControllerBean implements Serializable {

    @Inject
    private ElementsService elementsService;

    private ElementDto elementDto;

    private String elementType;

    private String reference;

    private Integer issue;

    public void initializeElement() {


        if (elementType != null && !elementType.isBlank() && elementType.toLowerCase().equals("magazine")) {
            elementDto = new MagazineDto();
            return;
        } else if (elementType != null && !elementType.isBlank() && elementType.toLowerCase().equals("book")){
            elementDto = new BookDto();
            return;
        }
        Optional<ElementDto> element;

        if (issue == null) {
            element = elementsService.getBook(reference).map(bookDto -> bookDto);
        } else {
            element = elementsService.getMagazine(reference, issue).map(magazineDto -> magazineDto);
        }

        if (element.isEmpty()) {
            try {
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .redirect( "/librarian/error.xhtml?errorId=elementNotFound");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        elementDto = element.get();

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

    public String saveElement() {

        boolean result;

        if (elementType != null) {
            result = elementsService.addElement(elementDto);
        } else {
            result = elementsService.updateElement(elementDto);
        }

        if (result) {
            return "elementsList.xhtml?faces-redirect=true";
        } else {
            return "/error.xhtml?faces-redirect=true&errorId=operationFailed";
        }
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getIssue() {
        return issue;
    }

    public void setIssue(Integer issue) {
        this.issue = issue;
    }
}


