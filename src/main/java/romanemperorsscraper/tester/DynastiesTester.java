package romanemperorsscraper.tester;

import org.openqa.selenium.WebDriver;
import romanemperorsscraper.run.NoSupportedBrowsersException;
import romanemperorsscraper.run.OsUtils;
import romanemperorsscraper.run.UnsupportedOsException;
import romanemperorsscraper.scraping.dynasties.Dynasty;

import java.util.HashMap;

/**
 * Class used to test the scraping of
 * dynasties names and Wikipedia page URLs
 *
 * @see Dynasty
 * @see Tester
 *
 * @author Matteo Collica
 * @author Sara Lazzaroni
 */

public class DynastiesTester {
    private static WebDriver webDriver;

    private static HashMap<String, String> expectedDynasties;
    private static HashMap<String, String> scrapedDynasties;

    /**
     * Initialize the WebDriver needed to scrape data from the Wikipedia page
     * of Roman Emperors and obtain the dynasties names and Wikipedia page URLs
     * to be compared with the expected ones
     *
     * @throws NoSupportedBrowsersException if there are no supported browsers installed in user's Operating System
     * @throws UnsupportedOsException if the user's Operating System isn't supported (not a Windows, macOS or Unix-based one)
     */
    public static void initialize() throws NoSupportedBrowsersException, UnsupportedOsException {
        OsUtils.initializeOs();

        webDriver = OsUtils.getWebDriver();

        scrapedDynasties = Dynasty.getDynasties(webDriver);
    }

    /**
     * Obtain the dynasties names and Wikipedia page URLs to be compared with the expected ones
     *
     * @param webDriver the WebDriver needed to scrape data from the Wikipedia page
     */
    public static void initialize(WebDriver webDriver) {
        DynastiesTester.webDriver = webDriver;

        scrapedDynasties = Dynasty.getDynasties(webDriver);
    }

    /**
     * Compare the scraped dynasties names and URLs with the expected ones
     *
     * @return true if the test was completed successfully, false otherwise
     */
    public static boolean doTest(){
        expectedDynasties = new HashMap<>();

        expectedDynasties.put("https://it.wikipedia.org/wiki/Dinastia_giulio-claudia", "Julio-Claudian Dynasty");
        expectedDynasties.put("https://it.wikipedia.org/wiki/Dinastia_flavia", "Flavian Dynasty");
        expectedDynasties.put("https://it.wikipedia.org/wiki/Imperatori_adottivi", "Nerva-Antonine Dynasty");
        expectedDynasties.put("https://it.wikipedia.org/wiki/Dinastia_dei_Severi", "Severan Dynasty");
        expectedDynasties.put("https://it.wikipedia.org/wiki/Dinastia_valeriana", "Valerian Dynasty");
        expectedDynasties.put("https://it.wikipedia.org/wiki/Dinastia_costantiniana", "Constantinian Dynasty");

        return expectedDynasties.equals(scrapedDynasties);
    }

    /**
     * @return the HashMap of scraped dynasties (where the key is
     * the Wikipedia page URL and the value is the dynasty name)
     */
    public static HashMap<String, String> getScrapedDynasties() {
        return scrapedDynasties;
    }

    /**
     * @return the HashMap of expected dynasties (where the key is
     * the Wikipedia page URL and the value is the dynasty name)
     */
    public static HashMap<String, String> getExpectedDynasties() {
        return expectedDynasties;
    }
}


