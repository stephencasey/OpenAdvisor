package MasterSchoolList;

import org.apache.commons.exec.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Schools {
    private List<School> SchoolList;

    public List<School> getSchoolList() {
        return SchoolList;
    }

    public void addSchools(Map<String, String> urlToSchool) {
        for(Map.Entry<String, String> entry : urlToSchool.entrySet()) {
            String url = entry.getKey();
            String school = entry.getValue();


        }
    }
}
