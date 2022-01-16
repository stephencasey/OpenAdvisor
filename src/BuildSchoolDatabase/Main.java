package BuildSchoolDatabase;

import common.Schools;
import common.WebScraper;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        UrlMasterListSite siteTheedadvocate = new UrlMasterListSite("https://www.theedadvocate.org/an-a-z-list-of-u-s-colleges-and-universities/");
        UrlMasterListSite site4icu = new UrlMasterListSite("https://www.4icu.org/us/a-z/");

        WebScraper scraper = new WebScraper();

        siteTheedadvocate.setExtractionProcess(new ExtractFromHtmlTable(scraper, Arrays.asList("school", "url", "state")));
        site4icu.setExtractionProcess(new ExtractFrom4icuMethod(scraper));

        scraper.startDriver();
        siteTheedadvocate.scrapeUrls();
        site4icu.scrapeUrls();
        scraper.closeDriver();

        Schools schools = new Schools();
        schools.addSchools(siteTheedadvocate.getUrlToSchool());
        schools.addSchools(site4icu.getUrlToSchool());

        CatalogType.setCatalogTypes(schools);

        schools.exportSchoolsToPostgres();
    }
}
