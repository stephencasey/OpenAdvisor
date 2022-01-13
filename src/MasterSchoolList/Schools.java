package MasterSchoolList;

import common.PostgreSQL;

import java.sql.ResultSet;
import java.util.*;

public class Schools {
    private List<School> schoolList;

    public Schools() {
        schoolList = new ArrayList<>();
    }

    public void exportSchoolsToPostgres(){
        PostgreSQL app = new PostgreSQL();
        app.createDb();
        app.connect();
        app.executeUpdate("CREATE TABLE IF NOT EXISTS schools" +
                "(name varchar(100), domain varchar(50) UNIQUE, catalog_type varchar(20))");

        for(School school : schoolList){
            app.executeUpdate("INSERT INTO schools(name, domain) " +
                    "VALUES($$"+ school.getSchoolName() +"$$,'" + school.getDomain() + "');");
        }
        ResultSet countResultSet = app.executeQuery("SELECT COUNT(*) FROM schools;");
        System.out.println("Number of schools saved to psql:");
        app.printResultSet(countResultSet);
        app.close();

    }

    public void addSchools(Map<String, String> urlToSchool) {
        for(Map.Entry<String, String> entry : urlToSchool.entrySet()) {
            String url = entry.getKey();
            String schoolName = entry.getValue().toUpperCase().strip();
            String domain = getDomain(url).toLowerCase().strip();
            if (!schoolExists(domain)) {
                schoolList.add(new School(schoolName, domain));
            }
            Collections.sort(schoolList);
        }
    }

    private boolean schoolExists(String domain) {
        for(School school : schoolList) {
            if(school.getDomain().equals(domain)){
                return true;
            }
        }
        return false;
    }

    private static String getDomain(String url) {
        return url.replaceFirst("^https?://(www[.])?", "");
    }

    public void printSchools(){
        for(School school : schoolList){
            System.out.println(school.getDomain() + " | " + school.getSchoolName());
        }
    }
}
