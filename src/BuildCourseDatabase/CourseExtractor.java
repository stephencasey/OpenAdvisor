package BuildCourseDatabase;

import common.School;
import common.Schools;
import common.WebScraper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriverException;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public abstract class CourseExtractor {
    protected Set<String> visitedSites;
    protected WebScraper scraper;
    protected String currentDomain;

    public CourseExtractor(WebScraper scraper) {
        visitedSites = new HashSet<>();
        this.scraper = scraper;
    }

    public void extractCourseBlocksFromSchools(Schools schools) {
        for(School school : schools) {
            if(isCompleted(school)){
                continue;
            }
            currentDomain = "catalog." + school.getDomain();
            Set<String> candidateUrls = getCandidateCourseDirectoryUrls(currentDomain);
            candidateUrls.removeAll(visitedSites);

            if (candidateUrls.isEmpty()) {
                writeSchoolCompleted(school);
                continue;
            }
            Set<String> courseBlocks = extractCourseBlocksFromUrls(candidateUrls);
            school.setCourseBlocks(courseBlocks);
            writeSchoolCompleted(school);
        }
    }

    public abstract Set<String> extractCourseBlocksFromUrls(Set<String> urls);

    public Set<String> getCandidateCourseDirectoryUrls(String domain) {
        String mainUrl = "http://" + domain;
        Elements linksOnMainPage = scrapeInDomainPageLinks(mainUrl, domain);
        Set<String> candidateUrls = getUrlsWithCourseInTitle(linksOnMainPage);
        if(candidateUrls.isEmpty()) {
            for(String subUrl : getUniqueUrls(linksOnMainPage)){
                Elements linksOnSubPage = scrapeInDomainPageLinks(subUrl, domain);
                Set<String> subPageCandidateLinks = getUrlsWithCourseInTitle(linksOnSubPage);
                candidateUrls.addAll(subPageCandidateLinks);
            }
        }
        return candidateUrls;
    }

    protected Elements scrapeInDomainPageLinks(String url, String domain){
        Elements links;
        try {
             links = scraper.getInDomainPageLinks(url, domain);
            visitedSites.add(url);
            links.removeIf(link -> visitedSites.contains(link.absUrl("href")));
        } catch (WebDriverException ex) {
//            logs.addMissedPage(subPageUrl);
            links = new Elements();
        }
        return links;
    }

    protected Set<String> getUrlsWithCourseInTitle(Elements links) {
        Set<String> candidateUrls = new HashSet<>();
        for(Element element : links) {
            if(element.text().toLowerCase().contains("course")) {
                candidateUrls.add(element.absUrl("href"));
            }
        }
        return candidateUrls;
    }

    protected Set<String> getUniqueUrls(Elements links) {
        Set<String> urls = new HashSet<>();
        links.forEach(link -> urls.add(link.absUrl("href")));
        return urls;
    }

    protected void writeSchoolCompleted(School school) {
        try (java.io.PrintWriter output = new PrintWriter(new FileWriter("schools_completed.txt", true), true)) {
            output.println(school.getID());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected boolean isCompleted(School school) {
        java.io.File file = new java.io.File("schools_completed.txt");
        try {
            Scanner input = new Scanner(file);
            while(input.hasNextInt()) {
                int completedSchoolID = input.nextInt();
                if(school.getID() == completedSchoolID) {
                    return true;
                }
            }
        } catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
        return false;
    }
}
