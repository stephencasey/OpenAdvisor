package BuildSchoolDatabase;

import common.WebScraper;

import java.util.HashMap;
import java.util.Map;

public abstract class SchoolExtractor {
    protected Map<String, String> urlToSchool;
    protected WebScraper scraper;

    public SchoolExtractor(WebScraper scraper){
        urlToSchool = new HashMap<>();
        this.scraper = scraper;
    }

    public abstract Map<String, String> scrapeUrlList(String mainUrl);

    public void printScrapingResults(){
        System.out.println("\n" + urlToSchool.size() + " schools were collected.");
    }
}
