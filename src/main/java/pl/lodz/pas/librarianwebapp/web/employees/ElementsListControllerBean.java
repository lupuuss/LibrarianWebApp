package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.ElementsService;
import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;
import pl.lodz.pas.librarianwebapp.web.MarksController;

import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("elementsListController")
@ViewScoped
public class ElementsListControllerBean extends MarksController<ElementCopyDto> implements Serializable {

    @Inject
    private ElementsService elementsService;

    private List<ElementCopyDto> copies;

    private String query = "";

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {

        if (this.query != null && this.query.equals(query)) return;

        this.query = query;
        this.copies = null;
    }

    public List<ElementCopyDto> getFilteredCopies() {

        if (copies != null) {
            return copies;
        }

        copies = elementsService.getCopiesByIssnIsbnContains(query);

        return copies;
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
