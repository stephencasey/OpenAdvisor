package MasterSchoolList;

import java.util.HashMap;
import java.util.Map;

public abstract class ExtractionProcess {
    protected Map<String, String> urlToSchool;

    public ExtractionProcess(){
        urlToSchool = new HashMap<>();
    }

    public abstract Map<String, String> scrapeUrlList(String mainUrl);

    public void printScrapingResults(){
        System.out.println("\n" + urlToSchool.size() + " schools were collected.");
    }

    public Map<String, String> getUrlToSchool() {
        return urlToSchool;
    }

}
