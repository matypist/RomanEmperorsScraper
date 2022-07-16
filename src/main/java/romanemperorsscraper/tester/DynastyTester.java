package romanemperorsscraper.tester;

import org.openqa.selenium.WebDriver;
import romanemperorsscraper.run.NoSupportedBrowsersException;
import romanemperorsscraper.run.OsUtils;
import romanemperorsscraper.run.UnsupportedOsException;
import romanemperorsscraper.scraping.dynasties.Dynasty;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Class used to test the scraping of dynasty
 * information (Wikipedia page title, foundation
 * and deposition date and the founder name and
 * Wikipedia page URL). This test is relative to
 * the Julio-Claudian Dynasty.
 *
 * @see Dynasty
 * @see Tester
 *
 * @author Matteo Collica
 * @author Sara Lazzaroni
 * @author Radu Ionut Barbalata
 */

public class DynastyTester {
    private static WebDriver webDriver;

    private static int numberOfTests = 0;
    private static int numberOfPassedTests = 0;

    private static Dynasty dynasty;
    private static final String DYNASTYNAME = "Julio-Claudian Dynasty";
    private static final String DYNASTYURL = "https://it.wikipedia.org/wiki/Dinastia_giulio-claudia";

    private static final ArrayList<String> FIELDS;
    private static LinkedHashMap<String, String> expectedScrapedValues;

    static {
        FIELDS = new ArrayList<>();

        FIELDS.add("Wikipedia page title:");
        FIELDS.add("Foundation date:");
        FIELDS.add("Deposition date:");
        FIELDS.add("Founder:");
    }

    /**
     * Initialize the WebDriver needed to scrape data from the Wikipedia page
     * of the Julio-Claudian Dynasty and get its information through the Dynasty
     * object constructor
     *
     * @throws NoSupportedBrowsersException if there are no supported browsers installed in user's Operating System
     * @throws UnsupportedOsException if the user's Operating System isn't supported (not a Windows, macOS or Unix-based one)
     */
    public static void initialize() throws NoSupportedBrowsersException, UnsupportedOsException {
        OsUtils.initializeOs();

        webDriver = OsUtils.getWebDriver();

        dynasty = new Dynasty(DYNASTYURL, DYNASTYNAME, webDriver, false);
    }

    /**
     * Get information of the Julio-Claudian Dynasty through the Dynasty object constructor
     *
     * @param webDriver the WebDriver needed to scrape data from the Wikipedia page
     */
    public static void initialize(WebDriver webDriver) {
        DynastyTester.webDriver = webDriver;

        dynasty = new Dynasty(DYNASTYURL, DYNASTYNAME, webDriver, false);
    }

    /**
     * Perform a single test by comparing an expected value with a scraped value
     *
     * Update expectedScrapedValues by adding the expected value as key and the
     * scraped value as value, numberOfTests and numberOfPassedTests
     *
     * @param expectedValue the expected value
     * @param scrapedValue the scraped value
     */
    public static void doTest(String expectedValue, String scrapedValue) {
        expectedScrapedValues.put(expectedValue, scrapedValue);

        numberOfTests++;

        if(expectedValue.equals(scrapedValue)){
            numberOfPassedTests++;
        }
    }

    /**
     * Compare scraped value with expected
     * value for each Dynasty object's field
     *
     * @return numberOfPassedTests (the number of passed tests)
     */
    public static int doTests() {
        expectedScrapedValues = new LinkedHashMap<>();

        String scrapedValue = dynasty.getPageTitle();
        DynastyTester.doTest("Dinastia giulio-claudia", scrapedValue);

        scrapedValue = dynasty.getFoundationDate();
        DynastyTester.doTest("27 a.C.", scrapedValue);

        scrapedValue = dynasty.getDepositionDate();
        DynastyTester.doTest("68 d.C.", scrapedValue);

        scrapedValue = dynasty.getFounderPageData().getPersonNameUrl().getFullName() + "    " + dynasty.getFounderPageData().getPersonNameUrl().getUrl();
        DynastyTester.doTest("Augusto    https://it.wikipedia.org/wiki/Augusto", scrapedValue);

        return numberOfPassedTests;
    }

    /**
     * @return the total number of tests
     */
    public static int getNumberOfTests() {
        return numberOfTests;
    }

    /**
     * @return an ArrayList of fields
     */
    public static ArrayList<String> getFields() {
        return FIELDS;
    }

    /**
     * @return a LinkedHashMap (which follows the same order of fields)
     * of expected value : scraped value entries
     */
    public static LinkedHashMap<String, String> getExpectedScrapedValues() {
        return expectedScrapedValues;
    }
}
