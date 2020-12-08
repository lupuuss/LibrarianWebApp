package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.ElementsService;
import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;
import pl.lodz.pas.librarianwebapp.web.MarksController;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named("elementsListController")
@RequestScoped
public class ElementsListControllerBean extends MarksController<ElementCopyDto> {

    @Inject
    private ElementsService elementsService;

    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<ElementCopyDto> getAllCopies() {
        return elementsService.getCopiesByIssnIsbnContains(query);
    }

    public String degradeMarkedCopies() {

        elementsService.degradeCopies(getMarkedItems());

        return "elementsList.xhtml?faces-redirect=true";
    }

    public String removeMarkedCopies() {

        elementsService.deleteCopies(getMarkedItems());

        return "elementsList.xhtml?faces-redirect=true";
    }

    public String filter() {
        return "elementsList.xhtml?faces-redirect=true&query=" + query;
    }
}
