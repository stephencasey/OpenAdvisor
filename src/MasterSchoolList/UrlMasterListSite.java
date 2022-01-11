package MasterSchoolList;

import java.util.Map;

public class UrlMasterListSite {
    private String mainUrl;
    private ExtractionProcess extractionProcess;
    private Map<String, String> urlToSchool;

    public UrlMasterListSite(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public void setExtractionProcess(ExtractionProcess extractionProcess){
        this.extractionProcess = extractionProcess;
    }

    public void scrapeUrls(){
        urlToSchool = extractionProcess.scrapeUrlList(mainUrl);
    }

    public Map<String, String> getUrlToSchool() {
        return urlToSchool;
    }
}
