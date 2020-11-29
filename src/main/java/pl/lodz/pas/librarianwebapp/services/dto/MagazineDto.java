package pl.lodz.pas.librarianwebapp.services.dto;

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
}
