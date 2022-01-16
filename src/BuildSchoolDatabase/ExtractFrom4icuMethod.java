package BuildSchoolDatabase;

import common.WebScraper;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtractFrom4icuMethod extends SchoolExtractor {
    private List<String> schoolNames;
    private List<String> subDomains;
    private String domain;

    public ExtractFrom4icuMethod(WebScraper scraper) {
        super(scraper);
    }

    @Override
    public Map<String, String> scrapeUrlList(String mainUrl) {
        scraper.loadPage(mainUrl);
        Elements rows = scraper.getRowsOfLargestTable();
        schoolNames = new ArrayList<>(WebScraper.getAllLinkTexts(rows));
        subDomains = new ArrayList<>(WebScraper.getAllLinkHrefs(rows));
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
        scraper.loadPage(subpageUrl);
        Elements tables = scraper.getTables();
        List<String> candidateUrls = WebScraper.getAllLinkHrefs(tables);
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
