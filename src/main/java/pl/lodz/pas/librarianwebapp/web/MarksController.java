package pl.lodz.pas.librarianwebapp.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MarksController<T> {

    private final Map<T, Boolean> marks = new HashMap<>();

    protected List<T> getMarkedItems() {
        return marks
                .entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Map<T, Boolean> getMarks() {
        return marks;
    }
}
