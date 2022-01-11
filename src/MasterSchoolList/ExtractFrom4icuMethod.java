package MasterSchoolList;

import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ExtractFrom4icuMethod extends ExtractionProcess {
    private List<String> schoolNames;
    private List<String> subDomains;
    private String domain;

    public ExtractFrom4icuMethod() {
        super();
    }

    @Override
    public void scrapeUrlList(String mainUrl) {
        Elements rows = ScrapingMethods.getRowsOfLargestTable(mainUrl);
        schoolNames = new ArrayList<>(ScrapingMethods.getAllLinkTexts(rows));
        subDomains = new ArrayList<>(ScrapingMethods.getAllLinkHrefs(rows));
        domain = mainUrl.substring(0,20);

        getSchoolUrlsFromSubDirectories();
        printScrapingResults();
    }

    private void getSchoolUrlsFromSubDirectories() {
        for(int i=0; i<schoolNames.size(); i++){

            String subDomain = subDomains.get(i);

            if (!isSchoolReviewPage(subDomain)){
                continue;
            }

            List<String> dotEduUrls = getDotEduUrls(subDomain);

            if(dotEduUrls.isEmpty()) {
                continue;
            }

            String url = dotEduUrls.get(0);
            String school = schoolNames.get(i);
            urlToSchool.put(url, school);
        }
    }

    private boolean isSchoolReviewPage(String subDomain) {
        return subDomain.startsWith("/reviews/");
    }

    private List<String> getDotEduUrls(String subDomain){
        String subpageUrl = domain + subDomain;
        Elements tables = ScrapingMethods.getTables(subpageUrl);
        List<String> candidateUrls = ScrapingMethods.getAllLinkHrefs(tables);
        return filterDotEduUrls(candidateUrls);
    }

    private List<String> filterDotEduUrls(List<String> candidateUrls){
        List<String> schoolUrls = new ArrayList<>();
        for(String url : candidateUrls){
            if(endsInDotEdu(url)) {
                schoolUrls.add(url);
            }
        }
        return schoolUrls;
    }
    
    private boolean endsInDotEdu(String url){
        if(url.replaceAll("/$", "").endsWith(".edu")){
            return true;
        }
        return false;
    }
}
