package BuildCourseDatabase;

import common.WebScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;

import java.util.*;

public class CourseExtractorAcalog extends CourseExtractor {

    public CourseExtractorAcalog() {
        super();
    }

    @Override
    protected Set<String> extractCourseBlocksFromUrls(Set<String> candidateUrls){
        Set<String> schoolCourseBlocks = new HashSet<>();
        int pageNumber = 1;
        for(String candidateUrl : candidateUrls) {
            Elements links = scrapeInDomainPageLinks(candidateUrl, currentDomain);
            String nextPageUrlStem = getNextPageUrlStem(links);
            if(nextPageUrlStem == null){
                continue;       // Assumes there are AT LEAST 2 pages of courses
            }

            String nextPageUrlEnd;
            String html;
            do {
                openAllPreviewLinks();
                waitForPageToLoad();
                html = scraper.extractPageHtml();
                Document soup = Jsoup.parse(html);
                Elements pageCourseBlocks = soup.getElementsByClass("coursepadding");

                pageCourseBlocks.forEach(courseBlock -> schoolCourseBlocks.add(courseBlock.toString()));
                pageNumber += 1;
                nextPageUrlEnd = pageNumber + "#acalog_template_course_filter";
                String nextCourseDirectoryUrl = nextPageUrlStem + nextPageUrlEnd;
                scraper.loadPage(nextCourseDirectoryUrl);
            } while (html.contains(nextPageUrlEnd));

        }
        clearIfCourseBlocksMissing(schoolCourseBlocks, pageNumber);
        return schoolCourseBlocks;
    }

    private String getNextPageUrlStem(Elements links) {
        String stem = null;
        for(Element link : links) {
            String url = link.absUrl("href");
            if(url.contains("#acalog_template_course_filter")) {
                stem = url.substring(0,url.lastIndexOf("#acalog_template_course_filter")-1);
                break;
            }
        }
        return stem;
    }

    private void openAllPreviewLinks() {
        List<WebElement> previewLinks = getPreviewLinkWebElements();
        for(int numberOfTries=1; numberOfTries<10; numberOfTries++){
            // Open links starting at the bottom and moving up the page (so links don't move other links)
            for(int i=previewLinks.size()-1; i>=0; i--) {
                WebElement previewLink = previewLinks.get(i);
                try{
                    scraper.scrollToElement(previewLink);
                    WebScraper.pause(3);
                    previewLink.click();
                } catch (ElementClickInterceptedException ex) {
                    scraper.scrollToElement(previewLink);
                } catch (ElementNotInteractableException | StaleElementReferenceException ex){
                    // Ignore for now (missing a few courseblocks is acceptable)
                }
            }
            waitForPageToLoad();
        }
    }

    private void clearIfCourseBlocksMissing(Set<String> schoolCourseBlocks, int pageNumber) {
        // Should be 100 courses per page (excluding the last page)
        if(schoolCourseBlocks.size()/100 < pageNumber-2 || schoolCourseBlocks.size() <= 100){
            schoolCourseBlocks.clear();
        }
    }

    private void waitForPageToLoad() {
        String oldHtml;
        String html = scraper.extractPageHtml();
        do {
            oldHtml = html;
            WebScraper.pause(300);
            html = scraper.extractPageHtml();
        } while (!html.equals(oldHtml));
    }

    private List<WebElement> getPreviewLinkWebElements() {
        return scraper.getElementsByXpath("//a[contains(@href,\"preview_course_nopop.php\")]");
    }

}


