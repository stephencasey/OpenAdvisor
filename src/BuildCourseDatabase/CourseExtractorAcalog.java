package BuildCourseDatabase;

import common.WebScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CourseExtractorAcalog extends CourseExtractor {

    public CourseExtractorAcalog() {
        super();
    }

    @Override
    protected Set<String> extractCourseBlocksFromUrls(Set<String> candidateUrls){
        Set<String> schoolCourseBlocks = new HashSet<>();

        for(String candidateUrl : candidateUrls) {
            Elements links = scrapeInDomainPageLinks(candidateUrl, currentDomain);
            String nextPageUrlStem = getNextPageUrlStem(links);
            if(nextPageUrlStem == null){
                continue;       // Assumes there are AT LEAST 2 pages of courses
            }

            Set<String> oldSiteCourseBlocks;
            int pageNumber = 1;
            do {
                oldSiteCourseBlocks = new HashSet<>(schoolCourseBlocks);
                openAllPreviewLinks();
                Document soup = getSoupAfterPageLoads();
                Elements pageCourseBlocks = soup.getElementsByClass("coursepadding");

                pageCourseBlocks.forEach(courseBlock -> schoolCourseBlocks.add(courseBlock.toString()));

                pageNumber += 1;
                String nextCourseDirectoryUrl = nextPageUrlStem + pageNumber + "#acalog_template_course_filter";
                scraper.loadPage(nextCourseDirectoryUrl);
            } while (!schoolCourseBlocks.equals(oldSiteCourseBlocks));
        }
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
        for(int numberOfTries=1; numberOfTries<4; numberOfTries++){
            for(int i=previewLinks.size()-1; i>=0; i--) {
                WebElement previewLink = previewLinks.get(i);
                try{
                    previewLink.click();
                } catch (ElementClickInterceptedException ex) {
                    scraper.scrollToMiddleOfWebElement(previewLink);
                } catch (ElementNotInteractableException | StaleElementReferenceException ex){
                    // Ignore for now (missing a few courseblocks is acceptable)
                }
            }
        }
    }

    private Document getSoupAfterPageLoads() {
        String oldHtml;
        String html = scraper.extractPageHtml();
        do {
            oldHtml = html;
            pause(300);
            html = scraper.extractPageHtml();
        } while (!html.equals(oldHtml));
        return Jsoup.parse(html);
    }

    private void pause(int pauseMilliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(pauseMilliseconds);
        }
        catch(InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private List<WebElement> getPreviewLinkWebElements() {
        return scraper.getElementsByXpath("//a[contains(@href,\"preview_course_nopop.php\")]");
    }

}


