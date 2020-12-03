package pl.lodz.pas.librarianwebapp.services.dto;

import java.util.Comparator;

public class MagazineDto extends  ElementDto{

    private  String issn;
    private  int issue;

    public MagazineDto(String title, String publisher, String issn, int issue) {
        super(title, publisher);
        this.issn = issn;
        this.issue = issue;
    }

    public MagazineDto() {
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public int getIssue() {
        return issue;
    }

    public void setIssue(int issue) {
        this.issue = issue;
    }

    @Override
    public int compareTo(ElementDto o) {
        var superResult = super.compareTo(o);

        if (superResult != 0) {
            return superResult;
        }

        var comparator = Comparator.comparing(MagazineDto::getIssn)
                .thenComparing(MagazineDto::getIssue);

        return comparator.compare(this, (MagazineDto) o);
    }
}
