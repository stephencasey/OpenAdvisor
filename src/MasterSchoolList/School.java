package MasterSchoolList;

public class School {
    private String schoolName;
    private String domain;

    public School(String schoolName, String domain) {
        this.schoolName = schoolName;
        this.domain = domain;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getDomain() {
        return domain;
    }

}
