package BuildSchoolDatabase;

import java.util.Map;

public class UrlMasterListSite {
    private String mainUrl;
    private SchoolExtractor schoolExtractor;
    private Map<String, String> urlToSchool;

    public UrlMasterListSite(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public void setExtractionProcess(SchoolExtractor schoolExtractor){
        this.schoolExtractor = schoolExtractor;
    }

    public void scrapeUrls(){
        urlToSchool = schoolExtractor.scrapeUrlList(mainUrl);
    }

    public Map<String, String> getUrlToSchool() {
        return urlToSchool;
    }
}
