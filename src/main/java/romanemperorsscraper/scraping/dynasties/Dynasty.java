package romanemperorsscraper.scraping.dynasties;

import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import romanemperorsscraper.scraping.people.PersonNameUrl;
import romanemperorsscraper.scraping.people.PersonPageData;
import romanemperorsscraper.scraping.people.PersonPageDataSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used to get and represent
 * roman emperors' dynasties data.
 *
 * @see DynastySerializer
 * @see DynastyTree
 *
 * @author Matteo Collica
 * @author Radu Ionut Barbalata
 */
public class Dynasty {
    /*
     * ATTRIBUTES
     */

    private final String wikipediaPageUrl;

    private final String pageTitle;

    private final String name;

    private final String foundationDate;

    private final String depositionDate;

    private final PersonPageData founderPageData;

    private DynastyTree dynastyTree;

    private static HashMap<String, Dynasty> storedDynasties = new HashMap<>();

    private static HashMap<String, String> dynastyNameTranslations = new HashMap<>();

    static {
        dynastyNameTranslations.put("costantiniana", "Constantinian");
        dynastyNameTranslations.put("flavia", "Flavian");
        dynastyNameTranslations.put("dei severi", "Severan");
        dynastyNameTranslations.put("degli antonini", "Nerva-Antonine");
        dynastyNameTranslations.put("giulio-claudia", "Julio-Claudian");
        dynastyNameTranslations.put("valeriana", "Valerian");
    }

    /*
     * CONSTRUCTORS
     */

    /**
     * Given a Web Driver and a dynasty's Wikipedia page URL,
     * scrape its data and/to construct a Dynasty object
     *
     * @param dynastyPageUrl the dynasty's Wikipedia page URL
     * @param dynastyName the dynasty's name
     * @param webDriver the Web Driver instance to be used to scrape data
     * @param makeDynastyTree whether the dynasty tree should be created or not
     */
    public Dynasty(String dynastyPageUrl, String dynastyName, WebDriver webDriver, boolean makeDynastyTree) {
        this.wikipediaPageUrl = dynastyPageUrl;
        this.name    = dynastyName;

        webDriver.get(dynastyPageUrl);

        /*
         * Retrieve the Wikipedia page's title from the first heading
         */

        this.pageTitle = webDriver.findElement(By.tagName("h1")).getText();

        /*
         * RETRIEVE NAMES AND REIGN PERIODS OF THE DYNASTY'S EMPERORS
         */

        /*
         * Retrieve names and reign periods of all the emperors in the page from
         * eligible h3 elements, their format will be "Name (Reign Period)",
         * e.g. "Augusto (27 a.C.-14 d.C.)", "Nerone (54-68 d.C.)", [...]
         */

        ArrayList<String> emperorsNameAndReignPeriod = retrieveEmperorsNamesAndReignPeriods(webDriver);

        /*
         * COMPUTE AND SET THE DYNASTY'S FOUNDING YEAR
         */

        String firstEmperorNameAndReignPeriod = emperorsNameAndReignPeriod.get(0);

        /*
         * Get the reign period of the first emperor
         * e.g. "Augusto (27 a.C.-14 d.C.)" => "27 a.C.-14 d.C.)"
         */

        String firstEmperorReignPeriod = firstEmperorNameAndReignPeriod.split("\\(")[1];

        /*
         * Use the beginning date of the first emperor's
         * reign period as the dynasty's deposition date
         * e.g. "27 a.C.-14 d.C.)" => "27 a.C."
         */

        this.foundationDate = firstEmperorReignPeriod.split("-")[0];

        /*
         * COMPUTE AND SET THE DYNASTY'S YEAR OF DEPOSITION
         */

        String lastEmperorNameAndReignPeriod = emperorsNameAndReignPeriod.get(emperorsNameAndReignPeriod.size() - 1);

        /*
         * Get the reign period of the last emperor
         * e.g. "Nerone (54-68 d.C.)" => "54-68 d.C.)"
         *
         * but without the closing round bracket:
         * e.g. "54-68 d.C.)" => "54-68 d.C."
         */

        String lastEmperorReignPeriod = lastEmperorNameAndReignPeriod.substring(0, lastEmperorNameAndReignPeriod.length() - 1).split("\\(")[1];

        /*
         * Use the ending date of the last emperor's
         * reign period as the dynasty's deposition date
         * e.g. "54-68 d.C." => "68 d.C."
         */

        String depositionDate = lastEmperorReignPeriod.split("-")[1];

        /* But first add 'd.C.' if it is missing */

        if(!depositionDate.contains("a.C.") && !depositionDate.contains("d.C.")) {
            depositionDate = depositionDate + " d.C.";
        }

        this.depositionDate = depositionDate;

        /*
         * Initiate the scraping of all the dynasty's descendants with their data
         * and the creation of the dynasty's tree data structure made of Person objects
         */

        if(dynastyPageUrl.equalsIgnoreCase("https://it.wikipedia.org/wiki/Dinastia_costantiniana")) {
            /*
             * In the Constantinian Dynasty we specifically pick
             * the first descendant of Costanzo Cloro as founder
             * because the Tetrarchs emperors have the Diocletian's
             * Tetrarchy put as their dynasty in the wikipedia page
             * instead even if they're listed in the Constantinian
             * Dynasty's Wikipedia page
             */

            founderPageData = PersonPageData.getPersonPageData(
                    webDriver,
                    new PersonNameUrl(
                            "Costanzo Cloro",
                            "https://it.wikipedia.org/wiki/Costanzo_Cloro"
                    ),
                    this.wikipediaPageUrl
            );
        } else if (dynastyPageUrl.equalsIgnoreCase("https://it.wikipedia.org/wiki/Dinastia_valeriana")){
            /*
             * In the Valerian Dynasty we specifically pick Valeriano
             * as founder because it is the first emperor of the dynasty
             * even if the dynasty is not listed in the summary table of
             * his Wikipedia page URL (and that's why we're not able to
             * retrieve him/her as the dynasty's founder just by scraping)
             */

            founderPageData = PersonPageData.getPersonPageData(
                    webDriver,
                    new PersonNameUrl(
                            "Valeriano",
                            "https://it.wikipedia.org/wiki/Valeriano"
                    ),
                    this.wikipediaPageUrl
            );
        } else {
            /*
             * In most cases we're able to automatically retrieve the
             * founder's Wikipedia page URL (and therefore his/her data)
             * through the dedicated retrieveFounderNameUrl method
             */

            founderPageData = PersonPageData.getPersonPageData(
                    webDriver,
                    this.retrieveFounderNameUrl(webDriver),
                    this.wikipediaPageUrl
            );
        }

        if(makeDynastyTree) {
            createDynastyTree(webDriver);

            storeDynasty(wikipediaPageUrl, this);
        }
    }

    /**
     * Restore (and construct) a Dynasty from a JSONObject (its serialized form)
     *
     * @param wikipediaPageUrl the Wikipedia page URL of the dynasty
     * @param serializedDynasty the dynasty's serialized object
     */
    public Dynasty(String wikipediaPageUrl, JSONObject serializedDynasty) {
        this.wikipediaPageUrl = wikipediaPageUrl;

        this.pageTitle = PersonPageDataSerializer.parseJsonString(serializedDynasty.get("pageTitle"));
        this.name      = PersonPageDataSerializer.parseJsonString(serializedDynasty.get("name"));

        this.foundationDate = PersonPageDataSerializer.parseJsonString(serializedDynasty.get("foundationDate"));
        this.depositionDate = PersonPageDataSerializer.parseJsonString(serializedDynasty.get("depositionDate"));

        String founderPageUrl = PersonPageDataSerializer.parseJsonString(serializedDynasty.get("founderPageUrl"));
        this.founderPageData  = PersonPageData.getCachedPersonPageData(founderPageUrl, wikipediaPageUrl);

        storeDynasty(wikipediaPageUrl, this);
    }

    /**
     * Given a Web Driver, create (and set as field) the dynasty's DynastyTree object
     *
     * @param webDriver the Web Driver instance to be used to scrape data
     */

    public void createDynastyTree(WebDriver webDriver) {
        this.dynastyTree = new DynastyTree(webDriver, founderPageData, this);
    }

    /*
     * UTILITY CLASS METHODS
     */

    /**
     * Scrape dynasty names and URLs from the roman emperors page
     * (https://it.wikipedia.org/wiki/Imperatori_romani), translate
     * names from Italian to English language and return them in form
     * of an HashMap using the dynasties' Wikipedia page URL as key
     *
     * @param webDriver the WebDriver to be used to get the roman emperors page
     *                  content and then search for dynasty names and URLs
     * @return an HashMap of (dynasty Wikipedia page URL : dynasty english name) entries
     */
    public static HashMap<String, String> getDynasties(WebDriver webDriver) {
        /*
         * Navigate to the Wikipedia page containing the
         * list of roman emperors and get its content
         */

        webDriver.get("https://it.wikipedia.org/wiki/Imperatori_romani");

        /*
         * Get the name and the Wikipedia page URL for each dynasty and put
         * them in an HashMap with the URLs as keys and the names as values
         */

        HashMap<String, String> dynasties = new HashMap<>();

        List<WebElement> anchorElements = webDriver.findElements(By.tagName("a"));

        String dynastyName;

        for(WebElement anchorElement : anchorElements) {
            dynastyName = anchorElement.getText();
            
            if(dynastyName.startsWith("Dinastia")){
                String dynastyPageUrl = anchorElement.getAttribute("href");

                /* Translate the Dynasty name from Italian to English language */

                dynastyName = Dynasty.getDynastyNameTranslation(dynastyName);

                /*
                 * Replace the url of a dynasty known to have more than
                 * one Wikipedia page url with just one of them to avoid
                 * duplicate entries by just overwriting them on put
                 */

                dynastyPageUrl = dynastyPageUrl.replace(
                        "https://it.wikipedia.org/wiki/Dinastia_degli_Antonini",
                        "https://it.wikipedia.org/wiki/Imperatori_adottivi"
                );

                /* Remove any specific location within the Wikipedia page URL */

                int indexOfHashTag = dynastyPageUrl.indexOf('#');

                if(indexOfHashTag != -1) {
                    dynastyPageUrl = dynastyPageUrl.substring(0, indexOfHashTag);
                }

                /*
                 * The dynasty name and Wikipedia page URL are finally clean
                 * and ready to be put in the HashMap we're creating
                 */

                dynasties.put(dynastyPageUrl, dynastyName);
            }
        }

        return dynasties;
    }

    /**
     * @return All the stored dynasties' names in a dashed list form, e.g.:<br>
     * - Dinastia costantiniana<br>
     * - Dinastia giulio-claudia<br>
     * [...]
     */
    public static StringBuilder getStoredDynastiesNames() {
        StringBuilder dynastiesNames = new StringBuilder();

        for (Map.Entry<String, Dynasty> set : storedDynasties.entrySet()) {
            Dynasty dynasty = set.getValue();

            dynastiesNames.append("- ").append(dynasty.getName()).append("\n");
        }

        return dynastiesNames;
    }

    /**
     * @param storedDynasties the HashMap containing the dynasties to
     *                        be stored by their Wikipedia page url
     */
    public static void setStoredDynasties(HashMap<String, Dynasty> storedDynasties) {
        Dynasty.storedDynasties = storedDynasties;
    }

    /**
     * @return the HashMap containing the stored
     *         dynasties by their Wikipedia page URL
     */
    public static HashMap<String, Dynasty> getStoredDynasties() {
        return storedDynasties;
    }

    /**
     * Put a new Dynasty in the stored dynasties HashMap by its Wikipedia page URL
     * 
     * @param dynastyPageUrl the dynasty's Wikipedia page URL
     * @param dynasty the dynasty's Dynasty object
     */
    public static void storeDynasty(String dynastyPageUrl, Dynasty dynasty) {
        storedDynasties.put(dynastyPageUrl, dynasty);
    }

    /**
     * Given a dynasty's Wikipedia page URL, return the corresponding Dynasty object
     *
     * @param dynastyPageUrl the dynasty's Wikipedia page URL
     * @return the dynasty's Dynasty object
     */
    public static Dynasty getStoredDynastyByUrl(String dynastyPageUrl) {
        return storedDynasties.getOrDefault(dynastyPageUrl, null);
    }

    /**
     * Check if a given URL points to the same dynasty's Wikipedia page whose URL
     * is given - this is preferable over a simple .equalsIgnoreCase() between
     * the two strings because there are different URLs for the same dynasties
     *
     * @param dynastyPageUrl the known dynasty's Wikipedia page URL
     * @param urlToBeChecked the URL to compare dynastyPageUrl against
     * @return true if they point to the same dynasty's Wikipedia page, false otherwise
     */
    public static boolean dynastyPageUrlEquals(String dynastyPageUrl, String urlToBeChecked) {
        String wikipediaUrlPrefix = "https://it.wikipedia.org/wiki/";

        switch(dynastyPageUrl) {
            case "https://it.wikipedia.org/wiki/Imperatori_adottivi":
                if(urlToBeChecked.startsWith(wikipediaUrlPrefix + "Dinastia_antoniniana") ||
                        urlToBeChecked.startsWith(wikipediaUrlPrefix + "Imperatori_adottivi") ||
                        urlToBeChecked.startsWith(wikipediaUrlPrefix + "Dinastia_degli_Antonini")) {
                    return true;
                }

                break;

            /*
             * The one in Severan Dynasty whose Wikipedia page URL
             * starts with "https://it.wikipedia.org/w/" is Macrino,
             * who represents the "missing link" between two Severan
             * emperors: "Caracalla" and "Eliogabalo"
             */

            case "https://it.wikipedia.org/wiki/Dinastia_dei_Severi":
                if(urlToBeChecked.startsWith(wikipediaUrlPrefix + "Dinastia_severiana") ||
                        urlToBeChecked.startsWith("https://it.wikipedia.org/w/") ||
                        urlToBeChecked.startsWith(wikipediaUrlPrefix + "Gens_Cornelia")) {

                    return true;
                }

                break;
        }

        return dynastyPageUrl.equalsIgnoreCase(urlToBeChecked);
    }

    /**
     * Given a driver loaded with a dynasty's Wikipedia page,
     * retrieve the dynasty founder's name and Wikipedia page URL
     * by iterating through elements of class 'vedi-anche' and
     * picking the first link whose page is an emperor's one
     *
     * @param webDriver the Web Driver instance to be used to scrape data
     * @return the dynasty founder's PersonNameUrl object
     */
    public static PersonNameUrl retrieveFounderNameUrl(WebDriver webDriver) {
        /*
         * We get elements of class "vedi-anche" because they have this content:
         * "Lo stesso argomento in dettaglio: <links>."
         * And they're the only place where we can get the emperors' page links
         */

        List<WebElement> vediAncheElements = webDriver.findElements(By.className("vedi-anche"));

        List<WebElement> anchorElements;
        WebElement anchorElement, summaryTable;
        String name, url, summaryTableText;

        /*
         * We iterate over these elements until we get the right one
         * (the one with the Wikipedia page URL of an emperor at least),
         * it should also be the founder because in the wikipedia dynasties'
         * pages the emperors are always put in chronological order
         */

        for(WebElement seeAlsoElement : vediAncheElements) {
            anchorElements = seeAlsoElement.findElements(By.tagName("a"));

            if(anchorElements.size() == 1) {
                anchorElement = anchorElements.get(0);

                if(!anchorElement.getText().startsWith("Albero") && !anchorElement.getText().startsWith("Età")) {
                    name = anchorElement.getText();
                    url  = anchorElement.getAttribute("href");

                    webDriver.get(url);

                    /*
                     * To check if a Wikipedia page URL is the one of an emperor
                     * we first check if it contains a "sinottico" HTML element,
                     * which is the one containing the summary table, then we check
                     * if in its summary table it is written somewhere that it
                     * is an emperor or a dictator through two dedicated methods
                     */

                    try {
                        summaryTable = webDriver.findElement(By.className("sinottico"));

                        summaryTableText = summaryTable.getText();

                        if (PersonPageData.textImpliesDictatorRole(summaryTableText) ||
                                PersonPageData.textImpliesEmperorRole(summaryTableText)) {

                            return new PersonNameUrl(name, url);
                        }
                    } catch(NoSuchElementException ignored) {}
                }
            }
        }

        return null;
    }

    /**
     * Given a driver loaded with a dynasty's Wikipedia page,
     * get all the emperors' names and reign periods from the
     * eligible h3 HTML elements
     *
     * @param webDriver the Web Driver instance to be used to scrape data
     * @return an array containing strings in 'Name (Reign Period)' format
     */
    public static ArrayList<String> retrieveEmperorsNamesAndReignPeriods(WebDriver webDriver) {
        /*
         * This method was tested working for the following dynasties:
         * - https://it.wikipedia.org/wiki/Dinastia_costantiniana - Constantinian Dynasty
         * - https://it.wikipedia.org/wiki/Dinastia_dei_Severi - Severan Dynasty
         * - https://it.wikipedia.org/wiki/Dinastia_flavia - Flavian Dynasty
         * - https://it.wikipedia.org/wiki/Dinastia_giulio-claudia - Julio–Claudian Dynasty
         * - https://it.wikipedia.org/wiki/Imperatori_adottivi - Nerva–Antonine Dynasty
         */

        List<WebElement> headerElements = webDriver.findElements(By.tagName("h3"));

        ArrayList<String> arrayOfEmperorsNamesAndReignPeriods = new ArrayList<>();

        List<WebElement> spanElements;

        for(WebElement headerElement : headerElements) {
            /*
             * To be eligible an H3 element shouldn't start with "Albero"
             * (a similar H3 element is present in each dynasty page),
             * should contain a span element of class 'mw-headline',
             * which is the one containing the H3 element's content,
             * and should have this format: 'Name (Reign Period)';
             * the latter one is ensured through the use of RegEx.
             */

            if(!headerElement.getText().startsWith("Albero")) {
                spanElements = headerElement.findElements(By.tagName("span"));

                for (WebElement spanElement : spanElements) {
                    if (!spanElement.getText().isEmpty()) {
                        if (spanElement.getAttribute("class").equals("mw-headline")) {
                            String headerTitle = spanElement.getText();

                            /*
                             * To ensure the format is 'Name (Reign Period)'
                             * we have to be as generic as possible to include
                             * some different cases:
                             * - Augusto (27 a.C.-14 d.C.)
                             * - Vespasiano (69-79)
                             * - Tiberio (14-37 d.C.)
                             * - Geta (211)
                             *
                             * So we check if the text starts with something,
                             * then has a space and an opening bracket after
                             * it, inside there is at least one number followed
                             * by anything, and finally it ends with a closing
                             * bracket. As generic as that.
                             */

                            Pattern pattern = Pattern.compile("^.* \\([0-9]+.*\\)$", Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(headerTitle);

                            if (matcher.find()) {
                                arrayOfEmperorsNamesAndReignPeriods.add(headerTitle);

                                break;
                            }
                        }
                    }
                }
            }
        }

        return arrayOfEmperorsNamesAndReignPeriods;
    }

    /**
     * Given a Dynasty's (scraped) italian name,
     * translate and return it in English language
     *
     * @param dynastyItalianName the italian dynasty name
     * @return the given italian dynasty name's english translation
     */
    public static String getDynastyNameTranslation(String dynastyItalianName) {
        /* Obtain the part of the dynasty name after 'Dinastia'
         * e.g. Dinastia giulio-claudia --> giulio-claudia
         */

        String justTheName = dynastyItalianName.split("Dinastia ")[1];

        /* Translate the dynasty name from English to Italian language
         * e.g. giulio-claudia --> Julio-Claudian Dynasty
         */

        return dynastyNameTranslations.get(justTheName.toLowerCase()) + " Dynasty";
    }

    /*
     * GETTERS
     */

    /**
     * @return the dynasty's Wikipedia page URL
     */
    public String getWikipediaPageUrl() {
        return wikipediaPageUrl;
    }

    /**
     * @return the dynasty's Wikipedia page title
     */
    public String getPageTitle() {
        return pageTitle;
    }

    /**
     * @return the dynasty's DynastyTree object
     */
    public DynastyTree getDynastyTree() {
        return dynastyTree;
    }

    /**
     * @return the dynasty's foundation date in String format (e.g. '27 a.C.')
     */
    public String getFoundationDate() {
        return foundationDate;
    }

    /**
     * @return the dynasty's deposition date in String format (e.g. '68 d.C.')
     */
    public String getDepositionDate() {
        return depositionDate;
    }

    /**
     * @return the dynasty founder's PersonPageData object
     */
    public PersonPageData getFounderPageData() {
        return founderPageData;
    }

    /**
     * @return the dynasty's name (already translated in English language)
     */
    public String getName() {
        return name;
    }
}
