package pl.lodz.pas.librarianwebapp.web.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageNode {

    private final String name;
    private final String link;

    private final List<PageNode> subNodes;

    private PageNode(String name, String link) {
        this.name = name;
        this.link = link;
        subNodes = new ArrayList<>();
    }

    public static PageNode forLink(String link, Map<String, String> names) {
        return new PageNode(names.get(link), link);
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    PageNode getCopyAppendParams(Map<String, String> params) {

        if (params == null || params.size() == 0) {
            return new PageNode(name, link);
        }

        StringBuilder builder = new StringBuilder(link);

        builder.append("?");

        for (var entry : params.entrySet()) {

            builder.append(entry.getKey());
            builder.append('=');
            builder.append(entry.getValue());

            builder.append('&');
        }

        builder.deleteCharAt(builder.length() - 1);

        return new PageNode(name, builder.toString());
    }

    public List<PageNode> getSubNodes() {
        return subNodes;
    }

    @Override
    public String toString() {
        return "PageNode{" +
                "link='" + link + '\'' +
                '}';
    }
}
