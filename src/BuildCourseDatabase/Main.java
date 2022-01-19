package BuildCourseDatabase;

import common.PostgreSQL;
import common.Schools;
import common.WebScraper;

public class Main {
    public static void main(String[] args) {

        Schools schoolsAcalog = new Schools("acalog");
        Schools schoolsCourseleaf = new Schools("courseleaf");

        CourseExtractorAcalog courseExtractorAcalog = new CourseExtractorAcalog();
        courseExtractorAcalog.extractCourseBlocksFromSchools(schoolsAcalog);

        CourseExtractorCourseleaf courseExtractorCourseleaf = new CourseExtractorCourseleaf();
        courseExtractorCourseleaf.extractCourseBlocksFromSchools(schoolsCourseleaf);

    }
}
