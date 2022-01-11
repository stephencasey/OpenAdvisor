package MasterSchoolList;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        UrlMasterListSite siteTheedadvocate = new UrlMasterListSite("https://www.theedadvocate.org/an-a-z-list-of-u-s-colleges-and-universities/");
        UrlMasterListSite site4icu = new UrlMasterListSite("https://www.4icu.org/us/a-z/");

        siteTheedadvocate.setExtractionProcess(new ExtractFromHtmlTable(Arrays.asList("school", "url", "state")));
        site4icu.setExtractionProcess(new ExtractFrom4icuMethod());

        ScrapingMethods.startDriver();

        siteTheedadvocate.scrapeUrls();
        site4icu.scrapeUrls();

        ScrapingMethods.closeDriver();

        Schools schools = new Schools();
        schools.addSchools(siteTheedadvocate.getUrlToSchool());
        schools.addSchools(site4icu.getUrlToSchool());

        System.out.println(schools.getSchoolList().size() +" schools saved");

    }
}
