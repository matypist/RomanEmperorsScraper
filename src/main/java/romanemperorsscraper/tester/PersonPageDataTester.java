package romanemperorsscraper.tester;

import org.openqa.selenium.WebDriver;
import romanemperorsscraper.run.NoSupportedBrowsersException;
import romanemperorsscraper.run.OsUtils;
import romanemperorsscraper.run.UnsupportedOsException;
import romanemperorsscraper.scraping.people.PersonNameUrl;
import romanemperorsscraper.scraping.people.PersonPageData;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Class used to test the scraping of people'
 * information (reign period, birth and death date,
 * successor, spouses, children, adopted children,
 * father, [...]). This test is relative to the
 * Roman Emperor "Augusto" of the Julio-Claudian Dynasty.
 *
 * @see PersonPageData
 * @see PersonNameUrl
 * @see Tester
 *
 * @author Matteo Collica
 * @author Sara Lazzaroni
 * @author Radu Ionut Barbalata
 */
public class PersonPageDataTester {
    private static WebDriver webDriver;

    private static PersonPageData personPageData;
    private static PersonNameUrl personNameUrl;

    private static int numberOfTests = 0;
    private static int numberOfPassedTests = 0;

    private static final ArrayList<String> FIELDS;
    private static LinkedHashMap<String, String> expectedScrapedValues;

    static {
        FIELDS = new ArrayList<>();

        FIELDS.add("Regno:");
        FIELDS.add("Nascita:");
        FIELDS.add("Morte:");
        FIELDS.add("Successore:");
        FIELDS.add("Coniuge:");
        FIELDS.add("Figli:");
        FIELDS.add("Figli Adottivi:");
        FIELDS.add("Padre:");
        FIELDS.add("Padre Adottivo:");
        FIELDS.add("Madre:");
    }

    /**
     * Initialize the WebDriver needed to scrape data from the Wikipedia page of
     * Augusto and get its information through the PersonPageData object constructor
     *
     * @throws NoSupportedBrowsersException if there are no supported browsers installed in user's Operating System
     * @throws UnsupportedOsException if the user's Operating System isn't supported (not a Windows, macOS or Unix-based one)
     */
    public static void initialize() throws NoSupportedBrowsersException, UnsupportedOsException {
        OsUtils.initializeOs();

        webDriver = OsUtils.getWebDriver();

        personNameUrl = new PersonNameUrl("Augusto", "https://it.wikipedia.org/wiki/Augusto");

        personPageData = new PersonPageData(
                webDriver,
                personNameUrl,
                "https://it.wikipedia.org/wiki/Dinastia_giulio-claudia"
        );
    }

    /**
     * Get information of Augusto through the PersonPageData object constructor
     *
     * @param webDriver the WebDriver needed to scrape data from the Wikipedia page
     */
    public static void initialize(WebDriver webDriver) {
        PersonPageDataTester.webDriver = webDriver;

        personNameUrl = new PersonNameUrl("Augusto", "https://it.wikipedia.org/wiki/Augusto");

        personPageData = new PersonPageData(
                webDriver,
                personNameUrl,
                "https://it.wikipedia.org/wiki/Dinastia_giulio-claudia"
        );
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
     * Compare scraped value with expected value
     * for each PersonPageData object's field
     *
     * @return numberOfPassedTests (the number of passed tests)
     */
    public static int doTests() {
        expectedScrapedValues = new LinkedHashMap<>();

        /*
         * REIGN PERIOD
         */

        doTest(
                "16 gennaio 27 a.C. -->  19 agosto 14 d.C",
                personPageData.getReignBeginningDate() + " -->  " + personPageData.getReignEndDate()
        );

        /*
         * BIRTH DATE
         */

        doTest(
            "23 settembre 63 a.C.",
                personPageData.getBirthDate().trim()
        );

        /*
         * DEATH DATE
         */

        doTest(
                "19 agosto 14 d.C",
                personPageData.getDeathDate().trim()
        );

        /*
         * SUCCESSORS
         */

        StringBuilder successors = new StringBuilder();

        String separator = "    ";

        for (PersonNameUrl successor : personPageData.getSuccessors()){
            successors.append(successor.getFullName()).append(" : ").append(successor.getUrl()).append(separator);
        }

        doTest(
                "Tiberio : https://it.wikipedia.org/wiki/Tiberio" + separator,
                String.valueOf(successors)
        );

        /*
         * SPOUSES
         */

        StringBuilder spouses = new StringBuilder();

        for (PersonNameUrl spouse : personPageData.getSpouses()){
            spouses.append(spouse.getFullName()).append(separator);
        }

        doTest(
                "Clodia Pulcra" + separator
                        + "Scribonia" + separator
                        + "Livia Drusilla" + separator,
                String.valueOf(spouses)
        );

        /*
         * CHILDREN
         */

        StringBuilder children = new StringBuilder();

        for (PersonNameUrl child : personPageData.getChildren()){
            children.append(child.getFullName()).append(separator);
        }

        doTest(
                "Giulia maggiore" + separator,
                String.valueOf(children)
        );

        /*
         * ADOPTED CHILDREN
         */

        StringBuilder adoptedChildren = new StringBuilder();

        for (PersonNameUrl adoptedChild : personPageData.getAdoptedChildren()){
            adoptedChildren.append(adoptedChild.getFullName()).append(separator);
        }

        doTest(
                "Lucio Cesare" + separator
                        + "Gaio Cesare" + separator
                        + "Marco Vipsanio Agrippa Postumo" + separator
                        + "Tiberio" + separator,
                String.valueOf(adoptedChildren)
        );

        /*
         * FATHER
         */

        doTest(
                "Gaio Ottavio",
                personPageData.getFatherNameUrl().getFullName()
        );

        /*
         * ADOPTIVE FATHER
         */

        doTest(
                "Gaio Giulio Cesare",
                personPageData.getAdoptiveFatherNameUrl().getFullName()
        );

        /*
         * MOTHER
         */

        doTest(
                "Azia maggiore",
                personPageData.getMotherNameUrl().getFullName()
        );

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
