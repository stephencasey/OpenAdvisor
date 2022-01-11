package MasterSchoolList;

import java.util.Map;

public class UrlMasterListSite {
    private String mainUrl;
    private ExtractionProcess extractionProcess;
    private Map<String, String> schoolToUrl;

    public UrlMasterListSite(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public void setExtractionProcess(ExtractionProcess extractionProcess){
        this.extractionProcess = extractionProcess;
    }

    public void scrapeUrls(){
        extractionProcess.scrapeUrlList(mainUrl);
    }

    public Map<String, String> getSchoolToUrl() {
        return schoolToUrl;
    }
}
