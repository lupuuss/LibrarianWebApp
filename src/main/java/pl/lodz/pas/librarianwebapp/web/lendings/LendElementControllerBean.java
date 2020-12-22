package pl.lodz.pas.librarianwebapp.web.lendings;

import pl.lodz.pas.librarianwebapp.services.ElementsService;
import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;
import pl.lodz.pas.librarianwebapp.services.dto.ElementDto;
import pl.lodz.pas.librarianwebapp.web.localization.ElementCopyStateI18n;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Named("lendElementController")
@ViewScoped
public class LendElementControllerBean implements Serializable {

    @Inject
    private ElementsService service;

    @Inject
    private LendingsCartControllerBean lendingsCartControllerBean;

    private ElementDto element;

    private List<ElementCopyStateI18n> states;

    private String ref;

    private Integer issue;

    private ElementCopyStateI18n state;

    public void initializeBook() {
        Optional<ElementDto> element;

        if (issue == null) {
            element = service.getBook(ref).map(bookDto -> bookDto);
        } else {
            element = service.getMagazine(ref, issue).map(magazineDto -> magazineDto);
        }

        if (element.isEmpty()) {
            try {
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .redirect( "/error.xhtml?errorId=elementNotFound");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        this.element = element.get();

        List<ElementCopyDto.State> states;

        if (issue == null) {
            states = service.getAvailableStatesForBook(ref);
        } else {
            states = service.getAvailableStatesForMagazine(ref, issue);
        }

        this.states = states
                .stream()
                .map(ElementCopyStateI18n::from)
                .collect(Collectors.toList());
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Integer getIssue() {
        return issue;
    }

    public void setIssue(Integer issue) {
        this.issue = issue;
    }

    public ElementDto getElement() {
        return element;
    }

    public void setElement(ElementDto element) {
        this.element = element;
    }

    public ElementCopyStateI18n getState() {
        return state;
    }

    public void setState(ElementCopyStateI18n state) {

        this.state = state;
    }

    public List<ElementCopyStateI18n> getAvailableStates() {
        return states;
    }

    public String addCurrentElementToCart() {

        if (issue == null){
            return lendingsCartControllerBean.addToCart(ref, state.toElementState());
        }
        return lendingsCartControllerBean.addToCart(ref, issue, state.toElementState());
    }
}
