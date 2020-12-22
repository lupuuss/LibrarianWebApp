package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.LendingsService;
import pl.lodz.pas.librarianwebapp.services.dto.LendEventDto;
import pl.lodz.pas.librarianwebapp.web.MarksController;

import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named("lendingsManagementController")
@ViewScoped
public class LendingsManagementControllerBean extends MarksController<LendEventDto> implements Serializable {

    private String clientQuery = "";
    private String elementQuery = "";

    private List<LendEventDto> events;

    @Inject
    private LendingsService service;

    public String getClientQuery() {
        return clientQuery;
    }

    public void setClientQuery(String clientQuery) {

        if (this.clientQuery != null && this.clientQuery.equals(clientQuery)) return;

        this.clientQuery = clientQuery;
        events = null;
    }

    public String getElementQuery() {
        return elementQuery;
    }

    public void setElementQuery(String elementQuery) {

        if (this.elementQuery != null && this.elementQuery.equals(elementQuery)) return;

        this.elementQuery = elementQuery;
        events = null;
    }

    public List<LendEventDto> getFilteredLendings(){

        if (events != null) {

            return events;
        }

        events = service.getFilteredLendings(clientQuery, elementQuery);

        return events;
    }

    public String removeMarkedNotReturned(){

        service.removeNotReturnedLendings(getMarkedItems());

        return "lendings.xhtml?faces-redirect=true&" + produceParamsString();
    }

    public String returnMarked(){
        var itemsToReturn = getMarkedItems()
                .stream()
                .filter(lend -> lend.getReturnDate() == null)
                .collect(Collectors.toList());

        service.returnLendings(itemsToReturn);

        return "lendings.xhtml?faces-redirect=true&" + produceParamsString();
    }

    public String filter() {
        return "lendings.xhtml?faces-redirect=true&" + produceParamsString();
    }

    private String produceParamsString() {
        return "clientQuery=" + clientQuery +
                "&elementQuery=" + elementQuery;
    }
}
