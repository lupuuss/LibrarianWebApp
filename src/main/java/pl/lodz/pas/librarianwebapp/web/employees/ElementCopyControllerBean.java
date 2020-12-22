package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.ElementsService;
import pl.lodz.pas.librarianwebapp.web.localization.ElementCopyStateI18n;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named("elementCopyController")
@RequestScoped
public class ElementCopyControllerBean {

    @Inject
    private ElementsService elementsService;

    private String reference;
    private ElementCopyStateI18n state;

    public List<String> getReferences() {

        var isbns = elementsService.getAllIsbns();
        var issns = elementsService.getAllMagazines()
                .stream()
                .map(magazineDto -> magazineDto.getIssn() + "_" + magazineDto.getIssue())
                .collect(Collectors.toList());

        isbns.addAll(issns);

        return isbns;
    }

    public ElementCopyStateI18n getState() {
        return state;
    }

    public void setState(ElementCopyStateI18n state) {
        this.state = state;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String createElementCopy() {

        boolean result;

        if (reference.contains("_")) {

            var issn = reference.substring(0, reference.indexOf("_"));
            var issue = Integer.parseInt(reference.substring(reference.indexOf("_") + 1));

            result = elementsService.addMagazineCopy(issn, issue, state.toElementState());
        } else {

            result = elementsService.addBookCopy(reference, state.toElementState());
        }

        if (result) {
            return "elementsList.xhtml?faces-redirect=true";
        } else {
            return "creationFailed.xhtml?faces-redirect=true";
        }
    }

    public List<ElementCopyStateI18n> getStates() {
        return Arrays.asList(ElementCopyStateI18n.values());
    }
}
