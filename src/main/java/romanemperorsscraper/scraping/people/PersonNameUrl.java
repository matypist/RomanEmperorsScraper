package romanemperorsscraper.scraping.people;

import org.json.simple.JSONObject;

/**
 * Class used to store names and Wikipedia page URLs
 * of people related to Roman Emperors Dynasties.
 *
 * @see PersonPageData
 *
 * @author Matteo Collica
 * @author Radu Ionut Barbalata
 */
public class PersonNameUrl {
    /*
     * ATTRIBUTES
     */

    private final String fullName;

    private String url;

    /*
     * CONSTRUCTORS
     */

    /**
     * Construct a PersonNameUrl object by setting its full name and Wikipedia page URL
     *
     * @param url the Wikipedia page url to be set
     * @param fullName the full name to be set
     */
    public PersonNameUrl(String fullName, String url) {
        this.fullName = fullName;
        this.url      = url;
    }

    /**
     * Deserialize the person's full name and Wikipedia page URL
     *
     * @param serializedPersonNameUrl the JSONObject to deserialize data from
     */
    public PersonNameUrl(JSONObject serializedPersonNameUrl) {
        this.fullName = PersonPageDataSerializer.parseJsonString(serializedPersonNameUrl.get("fullName"));
        this.url      = PersonPageDataSerializer.parseJsonString(serializedPersonNameUrl.get("url"));
    }

    /*
     * GETTERS
     */

    /**
     * @return the full name of the person represented by this PersonNameUrl object
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @return the Wikipedia Page URL of the person represented by this PersonNameUrl object
     */
    public String getUrl() {
        return url;
    }
}
