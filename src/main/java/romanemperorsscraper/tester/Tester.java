package romanemperorsscraper.tester;

import org.openqa.selenium.WebDriver;
import romanemperorsscraper.run.NoSupportedBrowsersException;
import romanemperorsscraper.run.OsUtils;
import romanemperorsscraper.run.UnsupportedOsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class tests the following methods:
 *
 * - PersonPageData's constructor by scraping
 *   public PersonPageData(WebDriver webDriver, PersonNameUrl personNameUrl, String dynastyPageUrl) {[...]}
 *   (through PersonPageDataTester)
 *
 * - Dynasty's constructor by scraping
 *   public Dynasty(String dynastyPageUrl, String dynastyName, WebDriver webDriver, boolean makeDynastyTree) {[...]}
 *   (through DynastyTester)
 *
 * - Dynasty's getDynasties method
 *   (through DynastiesTester)
 *
 * @author Matteo Collica
 * @author Sara Lazzaroni
 * @author Radu Ionut Barbalata
 */
public class Tester {
    /**
     * The main method executes and keeps count of the single passed tests
     * and test groups (PersonPageDataTester, DynastyTester and DynastiesTester)
     * passed over the total amount
     *
     * @param args currently there is no support for any command-line argument
     */
    public static void main(String[] args) throws UnsupportedOsException, NoSupportedBrowsersException {
        final int numberOfTests = 3;
        int numberOfSingleTests = 0;

        int numberOfPassedTests = 0;
        int numberOfSinglePassedTests = 0;

        OsUtils.initializeOs();
        WebDriver webDriver = OsUtils.getWebDriver();

        /*
         * Test of Dynasty's getDynasties method (through DynastiesTester)
         */

        DynastiesTester.initialize(webDriver);

        System.out.println("*----------------------------------------------------------------------------");
        System.out.println(
            "Test (through DynastiesTester) of Dynasty's getDynasties method"
        );

        boolean dynastiesTesterCompletedSuccessfully = DynastiesTester.doTest();

        System.out.println("\nExpected: ");
        for(Map.Entry<String, String> entry : DynastiesTester.getExpectedDynasties().entrySet()) {
            System.out.println("- " + entry.getKey() + " : " + entry.getValue());
        }

        System.out.println("\nScraped: ");
        for(Map.Entry<String, String> entry : DynastiesTester.getScrapedDynasties().entrySet()) {
            System.out.println("- " + entry.getKey() + " : " + entry.getValue());
        }

        System.out.println();
        if(dynastiesTesterCompletedSuccessfully) {
            System.out.println("Test completed without errors");

            numberOfPassedTests++;
            numberOfSinglePassedTests++;
        } else {
            System.out.println("Test completed with some errors");
        }

        numberOfSingleTests++;

        System.out.println("-----------------------------------------------------------------------------*");

        /*
         * Test of Dynasty's constructor (through DynastyTester)
         */

        DynastyTester.initialize(webDriver);

        System.out.println("\n*----------------------------------------------------------------------------");
        System.out.println(
            "Test (through DynastyTester) of Dynasty's constructor with the Julio-Claudian"
            + "\nDynasty Wikipedia page"
        );

        int dynastyTesterPassedTests = DynastyTester.doTests();
        int dynastyTesterTests       = DynastyTester.getNumberOfTests();

        printValues(DynastyTester.getFields(), DynastyTester.getExpectedScrapedValues());

        numberOfSinglePassedTests += dynastyTesterPassedTests;
        numberOfSingleTests       += dynastyTesterTests;

        System.out.println();

        numberOfPassedTests += computeResults(dynastyTesterPassedTests, dynastyTesterTests);

        System.out.println("-----------------------------------------------------------------------------*");

        /*
         * Test of PersonPageData's constructor (through PersonPageDataTester)
         */

        System.out.println("");

        PersonPageDataTester.initialize(webDriver);

        System.out.println("*----------------------------------------------------------------------------");
        System.out.println(
            "Test (through DynastyTester) of PersonPageData's constructor with the"
            + "\nWikipedia page of the Roman Emperor \"Augusto\" from the Julio-Claudian Dynasty"
        );

        int personPageDataTesterPassedTests = PersonPageDataTester.doTests();
        int personPageDataTesterTests       = PersonPageDataTester.getNumberOfTests();

        numberOfSinglePassedTests += personPageDataTesterPassedTests;
        numberOfSingleTests       += personPageDataTesterTests;

        printValues(PersonPageDataTester.getFields(), PersonPageDataTester.getExpectedScrapedValues());

        System.out.println();

        numberOfPassedTests += computeResults(personPageDataTesterPassedTests, personPageDataTesterTests);

        System.out.println("-----------------------------------------------------------------------------*");

        /*
         * Print the final results
         */

        System.out.println(numberOfPassedTests + " test group(s) of " + numberOfTests + " completed succesfully");
        System.out.println(numberOfSinglePassedTests + " single test(s) of " + numberOfSingleTests + " completed succesfully");

        webDriver.quit();
    }

    /**
     * Given an ArrayList of fields and their expected and scraped value, print them all
     *
     * @param fields an ArrayList of the fields we have to print expected values and scraped values of
     * @param expectedScrapedValues the expected value : scraped value entries to be printed
     */
    public static void printValues(ArrayList<String> fields, HashMap<String, String> expectedScrapedValues) {
        int counter = 0;

        /*
         * The expectedScrapedValues elements have been added
         * in parallel positions with the fields array elements
         * so that by iterating over one of these two we can print
         * both the field name and its corresponding values
         */
        for(Map.Entry<String, String> expectedScrapedValue : expectedScrapedValues.entrySet()) {
            System.out.println("\n" + fields.get(counter));

            System.out.println("- Expected = " + expectedScrapedValue.getKey());
            System.out.println("- Scraped  = " + expectedScrapedValue.getValue());

            counter++;
        }
    }

    /**
     * Compare the number of single passed tests with the
     * total amount of single tests and print out the outcome
     *
     * @param numberOfSinglePassedTests the number of single passed tests
     * @param numberOfSingleTests the total amount of single tests
     *
     * @return 1 if the number of single passed tests is the same as the total
     * amount of tests, 0 otherwise
     */
    public static int computeResults(int numberOfSinglePassedTests, int numberOfSingleTests) {
        int returnValue = 0;

        if(numberOfSinglePassedTests == numberOfSingleTests) {
            System.out.println("Test completed without errors");

            returnValue = 1;
        } else {
            System.out.println(
                    "Test completed with some errors ("
                            + numberOfSinglePassedTests
                            + " passed over a total amount of "
                            + numberOfSingleTests
                            + ")");
        }

        return returnValue;
    }
}
