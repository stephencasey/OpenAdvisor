package common;

import java.util.Set;

public class School implements Comparable<School>{
    private String schoolName;
    private String domain;
    private String catalogType;
    private Set<String> courseBlocks;
    private int schoolID;

    public School(String schoolName, String domain) {
        this.schoolName = schoolName;
        this.domain = domain;
    }

    public School(String schoolName, String domain, String catalogType) {
        this(schoolName, domain);
        this.catalogType = catalogType;
    }

    public School(String schoolName, String domain, String catalogType, int schoolID) {
        this(schoolName, domain);
        this.catalogType = catalogType;
        this.schoolID = schoolID;
    }

    public void setSchoolID(int schoolID) {
        this.schoolID = schoolID;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getDomain() {
        return domain;
    }

    public String getCatalogType() {
        return catalogType;
    }

    public int getID() {
        return schoolID;
    }

    public void setCatalogType(String catalogType) {
        this.catalogType = catalogType;
    }

    public void setCourseBlocks(Set<String> courseBlocks) {
        this.courseBlocks = courseBlocks;
        PostgreSQL postgres = new PostgreSQL();
        postgres.connect();
        postgres.executeUpdate("CREATE TABLE IF NOT EXISTS courses" +
                "(school_id smallint, html text, course_code varchar(20), credits varchar(20), title text, body text)");
        for(String courseBlock : courseBlocks) {
            postgres.executeUpdate("INSERT INTO courses(html, school_id) " +
                    "VALUES($$"+ courseBlock + "$$,$$" + schoolID +"$$);");
        }
        postgres.close();
    }

    public Set<String> getCourseBlocks() {
        return courseBlocks;
    }

    @Override
    public int compareTo(School otherSchool) {
        return schoolName.compareTo(otherSchool.schoolName);
    }
}
