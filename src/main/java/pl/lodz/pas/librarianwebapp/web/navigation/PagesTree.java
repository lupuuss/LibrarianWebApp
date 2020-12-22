package pl.lodz.pas.librarianwebapp.web.navigation;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class PagesTree {

    private final List<PageNode> rootNodes = new ArrayList<>();
    private final Map<String, String> namesMap = new HashMap<>();

    @PostConstruct
    public void initializeTree() {
        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        namesMap.putAll(loadNamesMap(bundle));

        rootNodes.addAll(generatePageTreeNodes());
    }

    private Map<String, String> loadNamesMap(ResourceBundle bundle) {

        var namesMap = new HashMap<String, String>();

        namesMap.put("/lendings/elements.xhtml", bundle.getString("page.menu.lendings"));
        namesMap.put("/lendings/cart.xhtml", bundle.getString("page.lending.cart"));
        namesMap.put("/lendings/your.xhtml", bundle.getString("page.lending.yourLendings"));
        namesMap.put("/lendings/addElementToCart.xhtml", bundle.getString("page.lending.addcart"));

        namesMap.put("/employee/element.xhtml", bundle.getString("page.books.element"));
        namesMap.put("/employee/elementCopy.xhtml", bundle.getString("page.books.elementCopy"));
        namesMap.put("/employee/elementsList.xhtml", bundle.getString("page.menu.manage.books"));
        namesMap.put("/employee/lendings.xhtml", bundle.getString("page.menu.manage.lendings"));

        namesMap.put("/admin/user.xhtml", bundle.getString("page.users.type.user"));
        namesMap.put("/admin/usersList.xhtml", bundle.getString("page.menu.manage.users"));

        return namesMap;
    }

    private List<PageNode> generatePageTreeNodes() {

        List<PageNode> nodes = new ArrayList<>();

        var lendingsNode = PageNode.forLink("/lendings/elements.xhtml", namesMap);

        lendingsNode.getSubNodes().add(PageNode.forLink("/lendings/addElementToCart.xhtml", namesMap));

        var carNode = PageNode.forLink("/lendings/cart.xhtml", namesMap);
        var yourRents = PageNode.forLink("/lendings/your.xhtml", namesMap);

        nodes.add(lendingsNode);
        nodes.add(carNode);
        nodes.add(yourRents);

        var manageElementsNode = PageNode.forLink("/employee/elementsList.xhtml", namesMap);

        manageElementsNode.getSubNodes().add(PageNode.forLink("/employee/element.xhtml", namesMap));
        manageElementsNode.getSubNodes().add(PageNode.forLink("/employee/elementCopy.xhtml", namesMap));

        nodes.add(manageElementsNode);

        var manageLendingsNode = PageNode.forLink("/employee/lendings.xhtml", namesMap);

        nodes.add(manageLendingsNode);

        var manageUserNode = PageNode.forLink("/admin/usersList.xhtml", namesMap);

        manageUserNode.getSubNodes().add(PageNode.forLink("/admin/user.xhtml", namesMap));

        nodes.add(manageUserNode);

        return nodes;
    }

    public List<PageNode> getBreadcrumbs(String link, Map<String, String> params) {

        for (var node : rootNodes){

            var breadcrumbs = new ArrayList<PageNode>();

            var result = searchBreadcrumbsForNode(link, node, breadcrumbs);

            if (result) {

                var lastItem = breadcrumbs.get(breadcrumbs.size() - 1);

                breadcrumbs.remove(lastItem);

                breadcrumbs.add(lastItem.getCopyAppendParams(params));

                return breadcrumbs;
            }
        }

        return Collections.emptyList();
    }

    private boolean searchBreadcrumbsForNode(String link, PageNode node, List<PageNode> breadcrumbs) {
        breadcrumbs.add(node);

        if (node.getLink().equals(link)) {
            return true;
        }

        for (var subNode : node.getSubNodes()) {
            var subBreadcrumbs = new ArrayList<PageNode>();
            var result = searchBreadcrumbsForNode(link, subNode, subBreadcrumbs);

            if (result) {
                breadcrumbs.addAll(subBreadcrumbs);
                return true;
            }
        }

        return false;
    }

    public List<PageNode> getRootNodes() {
        return rootNodes;
    }

    public String getPageTitle(String currentLocation) {

        var breadcrumbs = getBreadcrumbs(currentLocation, Collections.emptyMap());

        if (breadcrumbs.isEmpty()) {
            return currentLocation;
        }

        var lastNode = breadcrumbs.get(breadcrumbs.size() - 1);

        return lastNode.getName();
    }
}
