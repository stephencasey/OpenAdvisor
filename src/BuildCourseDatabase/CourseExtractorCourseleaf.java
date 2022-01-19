package BuildCourseDatabase;

import common.WebScraper;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriverException;

import java.util.HashSet;
import java.util.Set;

public class CourseExtractorCourseleaf extends CourseExtractor{

    public CourseExtractorCourseleaf() {
        super();
    }

    @Override
    protected Set<String> extractCourseBlocksFromUrls(Set<String> candidateUrls) {
        Set<String> schoolCourseBlocks = new HashSet<>();
        int levelsDeep = 1;
        while(schoolCourseBlocks.isEmpty() && levelsDeep <= 2) {
            Set<String> candidateSubPageUrls = new HashSet<>();
            Elements candidateSubPageLinks = new Elements();
            for(String candidateUrl : candidateUrls) {
                Elements subPageLinks = scrapeInDomainPageLinks(candidateUrl, currentDomain);
                subPageLinks.forEach(link -> candidateSubPageUrls.add(link.absUrl("href")));
                candidateSubPageLinks.addAll(subPageLinks);
            }

            for(String subPageUrl : candidateSubPageUrls) {
                try {
                    scraper.loadPage(subPageUrl);
                    Document soup = scraper.getSoup();
                    Elements pageCourseBlocks = soup.getElementsByClass("courseblock");
                    pageCourseBlocks.forEach(courseBlock -> schoolCourseBlocks.add(courseBlock.toString()));
                } catch (WebDriverException ex) {
//                    logs.addMissedPage(subPageUrl);

                }

            }
            candidateUrls = getUrlsWithCourseInTitle(candidateSubPageLinks);
            levelsDeep++;
            System.out.println(schoolCourseBlocks.size());
        }
        return schoolCourseBlocks;
    }
}