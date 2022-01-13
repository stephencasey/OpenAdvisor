package common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ScrapingMethods {
    private static ChromeDriver driver;

    // Selenium Methods

    public static void startDriver(){
        // Disable image loading
        ChromeOptions options =new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    public static String extractPageHtml(String url){
        driver.get(url);
        return  driver.getPageSource();
    }

    public static void closeDriver() {
        driver.quit();
    }


    // Jsoup Methods

    private static Document getSoup(String url) {
        String html = extractPageHtml(url);
        return Jsoup.parse(html);
    }

    public static Elements getTables(String url){
        return getSoup(url).select("table");
    }

    public static Elements getTableRows(Element table){
        return table.select("tr");
    }

    public static Elements getRowsOfLargestTable(String url){
        Elements tables = getTables(url);
        int maxRows = 0;
        Element largestTable = null;
        for(Element table : tables){
            Elements currentTableRows = table.select("tr");
            if(currentTableRows.size() > maxRows) {
                largestTable = table;
                maxRows = currentTableRows.size();
            }
        }
        return getTableRows(largestTable);
    }

    public static List<String> getAllLinkHrefs(Elements elements) {
        List<String> hrefList = new ArrayList<>();
        for(Element element : elements.select("a")) {
                hrefList.add(element.attr("href"));
        }
        return hrefList;
    }

    public static List<String> getAllLinkTexts(Elements elements) {
        List<String> linkTextList = new ArrayList<>();
        for(Element element : elements.select("a")) {
                linkTextList.add(element.text());
        }
        return linkTextList;
    }

}
