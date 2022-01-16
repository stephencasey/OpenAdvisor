package BuildCourseDatabase;

import common.PostgreSQL;
import common.Schools;
import common.WebScraper;

public class Main {
    public static void main(String[] args) {
        PostgreSQL.deleteTable("courses");

        Schools schoolsAcalog = new Schools();
        schoolsAcalog.importSchoolsFromPostgres("acalog");

        Schools schoolsCourseleaf = new Schools();
        schoolsCourseleaf.importSchoolsFromPostgres("courseleaf");

        WebScraper scraper = new WebScraper();
        CourseExtractorCourseleaf courseExtractorCourseleaf = new CourseExtractorCourseleaf(scraper);
        CourseExtractorAcalog courseExtractorAcalog = new CourseExtractorAcalog(scraper);

        scraper.startDriver();
        courseExtractorAcalog.extractCourseBlocksFromSchools(schoolsAcalog);
        courseExtractorCourseleaf.extractCourseBlocksFromSchools(schoolsCourseleaf);
        scraper.closeDriver();
    }
}
