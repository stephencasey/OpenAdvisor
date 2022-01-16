package BuildSchoolDatabase;

import common.School;
import common.Schools;
import common.WebScraper;
import org.openqa.selenium.WebDriverException;

public class CatalogType {
    protected static WebScraper scraper;

    public static void setCatalogTypes(Schools schools){
        scraper = new WebScraper();
        scraper.startDriver();
        for(School school : schools) {
            String type = extractCatalogType(school);
            school.setCatalogType(type);
        }
        scraper.closeDriver();
    }

    private static String extractCatalogType(School school) {
        String domain = school.getDomain();
        String html;
        try {
            // Catalog url's that start with "http://catalog." are low hanging fruit. Start with those.
            html = scraper.extractPageHtml("http://catalog." + domain);
        } catch (WebDriverException ex) {
            return "unknown";
        }

        if (html.contains("acalog")) {
            return "acalog";
        } else if (html.contains("courseleaf")) {
            return "courseleaf";
        } else {
            return "unknown";
        }
    }
}
