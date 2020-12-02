package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.ElementsService;
import pl.lodz.pas.librarianwebapp.services.LendingsService;
import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;
import pl.lodz.pas.librarianwebapp.services.dto.LendEventDto;
import pl.lodz.pas.librarianwebapp.web.MarksController;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("lendingsManagementController")
@RequestScoped
public class LendingsManagementControllerBean extends MarksController<LendEventDto> {

    private String clientQuery;
    private String elementQuery;

    @Inject
    private LendingsService service;

    public String getClientQuery() {
        return clientQuery;
    }

    public void setClientQuery(String clientQuery) {
        this.clientQuery = clientQuery;
    }

    public String getElementQuery() {
        return elementQuery;
    }

    public void setElementQuery(String elementQuery) {
        this.elementQuery = elementQuery;
    }

    public List<LendEventDto> getLendings() {
        return service.getAllLendings();
    }

    public List<LendEventDto> getFilteredLendings(){

        return service.getFilteredLendings(clientQuery, elementQuery);
    }

    public String removeMarked(){

        service.removeLendingEvents(getMarkedItems());

        return "lendings.xhtml?faces-redirect=true";
    }

    public String returnMarked(){
        var itemsToReturn = getMarkedItems()
                .stream()
                .filter(lend -> lend.getReturnDate() == null)
                .collect(Collectors.toList());

        service.returnLendings(itemsToReturn);

        return "lendings.xhtml?faces-redirect=true";
    }

    public String filter() {
        return "lendings.xhtml?faces-redirect=true" +
                "&clientQuery=" + clientQuery +
                "&elementQuery=" + elementQuery;
    }
}
