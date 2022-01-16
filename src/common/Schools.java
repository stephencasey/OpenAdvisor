package common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Schools implements Iterable<School>{
    private List<School> schoolList;

    public Schools() {
        schoolList = new ArrayList<>();
    }

    public void addSchools(Map<String, String> urlToSchool) {
        for(Map.Entry<String, String> entry : urlToSchool.entrySet()) {
            String url = entry.getKey();
            String schoolName = entry.getValue().toUpperCase().strip();
            String domain = getDomain(url).toLowerCase().strip();
            if (!schoolExists(domain)) {
                schoolList.add(new School(schoolName, domain));
            }
        }
        Collections.sort(schoolList);
        int id = 1;
        for(School school : schoolList){
            // Todo: Make schoolID immutable
            school.setSchoolID(id++);
        }
    }

    public void exportSchoolsToPostgres(){
        PostgreSQL postgres = new PostgreSQL();
        postgres.createDb();
        postgres.connect();
        postgres.executeUpdate("CREATE TABLE IF NOT EXISTS schools" +
                "(school_id smallint PRIMARY KEY, name varchar(100), domain varchar(50) UNIQUE, catalog_type varchar(20))");

        for(School school : schoolList){
            postgres.executeUpdate("INSERT INTO schools(school_id, name, domain, catalog_type) " +
                    "VALUES(" + school.getID() +
                    ",$$"+ school.getSchoolName() +"$$,'"
                    + school.getDomain() + "','"
                    + school.getCatalogType() +"');");
        }
        ResultSet countResultSet = postgres.executeQuery("SELECT COUNT(*) FROM schools;");
        System.out.println("Number of schools saved to psql:");
        postgres.printResultSet(countResultSet);
        postgres.close();
    }

    public void importSchoolsFromPostgres(String catalog_type) {
        String query = catalog_type.isBlank() ? "SELECT * FROM schools" :
                "SELECT * FROM schools WHERE catalog_type = '" + catalog_type + "';";

        try {
            PostgreSQL postgres = new PostgreSQL();
            postgres.connect();
            ResultSet resultSet = postgres.executeQuery(query);
            while(resultSet.next()) {
                schoolList.add(new School(resultSet.getString("name"),
                        resultSet.getString("domain"),
                        resultSet.getString("catalog_type"),
                        resultSet.getInt("school_id")));
            }
            postgres.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void importSchoolsFromPostgres() {
        importSchoolsFromPostgres("");
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
            System.out.println(school.getDomain() + " | " + school.getSchoolName() + " | " + school.getCatalogType());
        }
    }

    public List<School> getSchoolList() {
        return schoolList;
    }

    @Override
    public Iterator<School> iterator() {
        return schoolList.iterator();
    }
}
