package MasterSchoolList;

public class School implements Comparable<School>{
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

    @Override
    public int compareTo(School otherSchool) {
        return schoolName.compareTo(otherSchool.schoolName);
    }
}
