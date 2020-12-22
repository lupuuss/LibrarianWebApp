package pl.lodz.pas.librarianwebapp.web.employees;

import pl.lodz.pas.librarianwebapp.services.ElementsService;
import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;
import pl.lodz.pas.librarianwebapp.services.pages.Page;
import pl.lodz.pas.librarianwebapp.web.MarksController;

import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Named("elementsListController")
@ViewScoped
public class ElementsListControllerBean extends MarksController<ElementCopyDto> implements Serializable {

    private final List<Integer> pageSizeVariants = List.of(5, 10, 15, 30, 50);

    @Inject
    private ElementsService elementsService;

    private List<ElementCopyDto> copies;

    private Page<ElementCopyDto> page;

    private String query = "";
    private int pageSize;
    private int pageNumber;

    public void init() {
        if (pageSize == 0) {
            pageSize = pageSizeVariants.get(0);
        }

        if (pageNumber < 0) {
            pageNumber = 0;
        }
    }

    public List<Integer> getPageSizeVariants() {
        return pageSizeVariants;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {

        if (this.query != null && this.query.equals(query)) return;

        this.query = query;
        this.page = null;
    }

    public List<Integer> getPages() {

        var pages = elementsService.getCopiesPagesCountByIssnIsbn(query, pageSize);

        return IntStream.range(0, pages).boxed().collect(Collectors.toList());
    }

    public List<ElementCopyDto> getFilteredCopies() {

        if (copies != null) {
            return copies;
        }

        copies = elementsService.getCopiesByIssnIsbnContains(query);

        return copies;
    }

    public Page<ElementCopyDto> getCurrentPage() {
        if (page != null) {
            return page;
        }

        page = elementsService.getCopiesPageByIssnIsbnContains(query, pageSize, pageNumber);
        return page;
    }

    public int getPageSize() {

        return pageSize;
    }

    public void setPageSize(int pageSize) {

        if (this.pageSize == pageSize) return;

        this.pageSize = pageSize;
        this.page = null;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {

        if (this.pageNumber == pageNumber) return;

        this.pageNumber = pageNumber;
        this.page = null;
    }

    private String produceParamsString() {
        return "query=" + query +
                "&pageSize=" + pageSize +
                "&pageNumber=" + pageNumber;
    }

    public String degradeMarkedCopies() {

        elementsService.degradeCopies(getMarkedItems());

        return "elementsList.xhtml?faces-redirect=true&" + produceParamsString();
    }

    public String removeMarkedCopies() {

        elementsService.deleteCopies(getMarkedItems());

        return "elementsList.xhtml?faces-redirect=true&" + produceParamsString();
    }
}
