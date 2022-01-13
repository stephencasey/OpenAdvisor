package MasterSchoolList;

import common.ScrapingMethods;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtractFrom4icuMethod extends ExtractionProcess {
    private List<String> schoolNames;
    private List<String> subDomains;
    private String domain;

    public ExtractFrom4icuMethod() {
        super();
    }

    @Override
    public Map<String, String> scrapeUrlList(String mainUrl) {
        Elements rows = ScrapingMethods.getRowsOfLargestTable(mainUrl);
        schoolNames = new ArrayList<>(ScrapingMethods.getAllLinkTexts(rows));
        subDomains = new ArrayList<>(ScrapingMethods.getAllLinkHrefs(rows));
        domain = mainUrl.substring(0,20);

        getSchoolUrlsFromSubDirectories();
        printScrapingResults();
        return urlToSchool;
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
        return url.replaceAll("/$", "").endsWith(".edu");
    }
}
