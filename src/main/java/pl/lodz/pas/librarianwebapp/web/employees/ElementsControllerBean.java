package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.ElementsService;
import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("elementsController")
@RequestScoped
public class ElementsControllerBean {

    @Inject
    private ElementsService elementsService;
    private Map<ElementCopyDto, Boolean> marks = new HashMap<>();

    public List<ElementCopyDto> getAllCopies() {
        return elementsService.getAllCopies();
    }

    public Map<ElementCopyDto, Boolean> getMarks() {
        return marks;
    }

    public void setMarks(Map<ElementCopyDto, Boolean> marks) {
        this.marks = marks;
    }

    public String degradeMarkedCopies() {

        elementsService.degradeCopies(getMarkedCopies());

        return "elements.xhtml?faces-redirect=true";
    }

    public String removeMarkedCopies() {

        elementsService.deleteCopies(getMarkedCopies());

        return "elements.xhtml?faces-redirect=true";
    }

    private List<ElementCopyDto> getMarkedCopies() {
        return marks.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
