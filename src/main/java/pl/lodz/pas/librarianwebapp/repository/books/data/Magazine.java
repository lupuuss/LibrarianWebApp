package pl.lodz.pas.librarianwebapp.repository.books.data;

import java.util.UUID;

public class Magazine extends Element<Magazine> {

    private final String issn;
    private final int issue;

    public Magazine(UUID uuid, String publisher, String title, String issn, int issue) {
        super(uuid, publisher, title);
        this.issn = issn;
        this.issue = issue;
    }

    public String getIssn() {
        return issn;
    }

    public int getIssue() {
        return issue;
    }

    @Override
    public Magazine copy() {
        return new Magazine(
                getUuid(),
                getPublisher(),
                getTitle(),
                issn,
                issue
        );
    }
}
