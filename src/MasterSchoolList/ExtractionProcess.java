package MasterSchoolList;

import java.util.HashMap;
import java.util.Map;

public abstract class ExtractionProcess {
    protected Map<String, String> urlToSchool;

    public ExtractionProcess(){
        urlToSchool = new HashMap<>();
    }

    public abstract void scrapeUrlList(String mainUrl);

    public void printScrapingResults(){
        for(Map.Entry<String, String> entry : urlToSchool.entrySet()) {
            System.out.println(entry.getKey() + " | " + entry.getValue());
        }
        System.out.println("\n" + urlToSchool.size() + " schools were collected.");
    }

    public Map<String, String> getUrlToSchool() {
        return urlToSchool;
    }

}
