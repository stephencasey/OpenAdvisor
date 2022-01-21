package BuildCourseDatabase;

import common.School;
import common.Schools;
import common.WebScraper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriverException;

import java.io.*;
import java.util.*;

public abstract class CourseExtractor {
    protected Set<String> visitedSites;
    protected WebScraper scraper;
    protected String currentDomain;
    protected long startTime;

    public CourseExtractor() {
        visitedSites = new HashSet<>();
        startTime = System.currentTimeMillis();
    }

    public void extractCourseBlocksFromSchools(Schools schools) {
        initializeFiles();
        scraper = new WebScraper();
        scraper.startDriver();
        for(School school : schools) {
            if(isInFile(school, "schools_completed.txt") || isInFile(school, "schools_skipped.txt")){
                continue;
            }
            currentDomain = "catalog." + school.getDomain();
            Set<String> candidateUrls = getCandidateCourseDirectoryUrls(currentDomain);
            candidateUrls.removeAll(visitedSites);

            Set<String> courseBlocks = extractCourseBlocksFromUrls(candidateUrls);

            if(courseBlocks.isEmpty()) {
                writeSkipped(school);
            } else {
                school.setCourseBlocks(courseBlocks);
                writeCompleted(school);
            }
        }
        scraper.closeDriver();
    }

    protected abstract Set<String> extractCourseBlocksFromUrls(Set<String> urls);

    private Set<String> getCandidateCourseDirectoryUrls(String domain) {
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
        } catch (NoSuchWindowException ex) {
            System.exit(1);
            links = new Elements();
        } catch (WebDriverException ex) {
            ex.printStackTrace();
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

    private Set<String> getUniqueUrls(Elements links) {
        Set<String> urls = new HashSet<>();
        links.forEach(link -> urls.add(link.absUrl("href")));
        return urls;
    }

    private void writeSkipped(School school) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("schools_skipped.txt", true))) {
            writer.write(String.valueOf(school.getID()));
            writer.newLine();
            System.out.print("Skipped " + school.getSchoolName()+ ".");
            printTimeElapsed();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void writeCompleted(School school) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("schools_completed.txt", true))) {
            writer.write(String.valueOf(school.getID()));
            writer.newLine();
            removeFromSkipped(school);
            System.out.print("Completed " + school.getSchoolName() + ".");
            System.out.print("Number of courses: " + school.getCourseBlocks().size() + ". ");
            printTimeElapsed();
        } catch (IOException ex) {
            writeSkipped(school);
            ex.printStackTrace();
        }
    }

    private void removeFromSkipped(School school) {
        File tempFile = new File("temp.txt");
        File schoolsSkippedFile = new File("schools_skipped.txt");

        try(
                BufferedReader skippedReader = new BufferedReader(new FileReader(schoolsSkippedFile));
                BufferedWriter skippedWriter = new BufferedWriter(new FileWriter(tempFile))
        ) {
            Scanner input = new Scanner(skippedReader);
            while(input.hasNextInt()) {
                int completedSchoolID = input.nextInt();
                if(school.getID() != completedSchoolID) {
                    skippedWriter.write(String.valueOf(school.getID()));
                }
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }

        if(!schoolsSkippedFile.delete()){
            System.out.println("not deleted");
        }
        if(!tempFile.renameTo(schoolsSkippedFile)){
            System.out.println("not replaced");
        }
    }

    private boolean isInFile(School school, String fileName) {
        java.io.File file = new java.io.File(fileName);
        try {
            Scanner input = new Scanner(file);
            while(input.hasNextInt()) {
                int schoolID = input.nextInt();
                if(school.getID() == schoolID) {
                    return true;
                }
            }
        } catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
        return false;
    }

    private void initializeFiles() {
        try {
            boolean skippedFileCreated = new File("schools_skipped.txt").createNewFile();
            boolean completedFileCreated = new File("schools_completed.txt").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printTimeElapsed() {
        long timeElapsed = System.currentTimeMillis()- startTime;
        System.out.println("Time elapsed: " + timeElapsed/1000 + " seconds.");
        startTime = System.currentTimeMillis();
    }
}
