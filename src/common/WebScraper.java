package common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WebScraper {
    private static ChromeDriver driver;
    private static Document soup;

    // Selenium Methods

    public void startDriver(){
        ChromeOptions options =new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2);    // Disable image loading
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    public void closeDriver() {
        driver.quit();
    }

    private void openPageOnDriver(String url) throws WebDriverException {
        driver.get(url);
    }

    public String extractPageHtml() {
        return driver.getPageSource();
    }

    public String extractPageHtml(String url){
        openPageOnDriver(url);
        return  driver.getPageSource();
    }

    public List<WebElement> getElementsByXpath(String xpath){
        return driver.findElements(By.xpath(xpath));
    }

    public void scrollToElement(WebElement webElement) {
        String scrollToMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, " +
                "window.innerHeight || 0);var elementTop = arguments[0].getBoundingClientRect()." +
                "top;window.scrollBy(0, elementTop-(viewPortHeight/5));";
        driver.executeScript(scrollToMiddle, webElement);
    }



    // Jsoup Methods

    public Document getSoup() {
        String html = extractPageHtml();
        soup = Jsoup.parse(html);
        return soup;
    }

    public void loadPage(String url) {
        openPageOnDriver(url);
        String html = extractPageHtml();
        soup = Jsoup.parse(html);
    }

    public void loadPage(String url, String baseUri) {
        openPageOnDriver(url);
        String html = extractPageHtml();
        soup = Jsoup.parse(html, baseUri);
    }

    public Elements getTables() {
        return soup.select("table");
    }

    public Elements getRowsOfLargestTable(){
        Elements tables = getTables();
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

    public Elements getInDomainPageLinks(String url, String domain) {
        String baseUri = "http://" + domain;
        loadPage(url, baseUri);
        Elements aElements = soup.select("a");
        aElements.removeIf(element -> element.absUrl("href").isBlank());
        aElements.removeIf(element -> !element.absUrl("href").contains(domain));
        return aElements;
    }

    public static List<String> getAllLinkHrefs(Elements elements) {
        List<String> hrefList = new ArrayList<>();
        elements.select("a").forEach(element -> hrefList.add(element.attr("href")));
        return hrefList;
    }

    public static List<String> getAllLinkTexts(Elements elements) {
        List<String> linkTextList = new ArrayList<>();
        elements.select("a").forEach(element -> linkTextList.add(element.text()));
        return linkTextList;
    }

    public static Elements getTableRows(Element table){
        if(table == null){
            return null;
        }
        return table.select("tr");
    }

    public static void pause(int pauseMilliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(pauseMilliseconds);
        }
        catch(InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}


