package MasterSchoolList;

import org.apache.commons.exec.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Schools {
    private List<School> schoolList;

    public Schools() {
        schoolList = new ArrayList<>();
    }

    public List<School> getSchoolList() {
        return schoolList;
    }

    public void addSchools(Map<String, String> urlToSchool) {
        for(Map.Entry<String, String> entry : urlToSchool.entrySet()) {
            String url = entry.getKey();
            String schoolName = entry.getValue().toUpperCase();
            String domain = getDomain(url).toLowerCase();
            if (!schoolExists(domain)) {
                schoolList.add(new School(schoolName, domain));
            }
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
        URI uri = URI.create(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
