package BuildSchoolDatabase;

import common.WebScraper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;

public class ExtractFromHtmlTable extends SchoolExtractor {
    private final int SCHOOL_COLUMN;
    private final int URL_COLUMN;

    public ExtractFromHtmlTable(WebScraper scraper, List<String> columnNames) {
        super(scraper);
        SCHOOL_COLUMN = columnNames.indexOf("school");
        URL_COLUMN = columnNames.indexOf("url");
    }

    @Override
    public Map<String, String> scrapeUrlList(String mainUrl) {
        scraper.loadPage(mainUrl);
        Elements tableRows = scraper.getRowsOfLargestTable();
        extractSchoolsAndUrlsFromTable(tableRows);
        printScrapingResults();
        return urlToSchool;
    }

    private void extractSchoolsAndUrlsFromTable(Elements tableRows){
        for(int i=1; i<tableRows.size(); i++){
            Element row = tableRows.get(i);
            extractSchoolAndUrlFromRow(row);
        }
    }

    private void extractSchoolAndUrlFromRow(Element row){
        Elements columns = row.getElementsByTag("td");
        String schoolName = columns.get(SCHOOL_COLUMN).text();
        String schoolUrl = columns.get(URL_COLUMN).text();
        urlToSchool.put(schoolUrl, schoolName);
    }

}
