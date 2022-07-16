package romanemperorsscraper.scraping.dynasties;

import org.json.simple.JSONObject;
import romanemperorsscraper.run.RomanEmperorsScraper;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class providing methods for
 * dynasties' Dynasty objects serialization.
 *
 * @see Dynasty
 *
 * @author Matteo Collica
 * @author Radu Ionut Barbalata
 */
public class DynastySerializer {
    /*
     * PREVENT INSTANTIATION WITH A PRIVATE CONSTRUCTOR
     */

    private DynastySerializer() {
        throw new java.lang.UnsupportedOperationException(
            "As a utility class, this class must not be instantiated"
        );
    }

    /*
     * CLASS UTILITY METHODS
     */

    /**
     * Create and return a serialized form of a given Dynasty object
     *
     * @param dynasty the Dynasty object to be serialized
     *
     * @return the Dynasty object serialized as a JSONObject
     */
    public static JSONObject serializeDynasty(Dynasty dynasty) {
        /*
         * The following is an example of the serialized form this method produces:
         * {
         *      "pageTitle": "Dinastia costantiniana",
         *      "name": "Constantinian Dynasty",
         *      "founderPageUrl": "https://it.wikipedia.org/wiki/Costanzo_Cloro",
         *      "foundationDate": "293",
         *      "depositionDate": "363 d.C."
         * }
         */

        JSONObject values = new JSONObject();

        values.put("pageTitle", dynasty.getPageTitle());

        values.put("name", dynasty.getName());

        values.put("foundationDate", dynasty.getFoundationDate());
        values.put("depositionDate", dynasty.getDepositionDate());

        values.put("founderPageUrl", dynasty.getFounderPageData().getPersonNameUrl().getUrl());

        return values;
    }

    /**
     * Given an HashMap containing dynasties' data by URL, serialize it in a
     * single JSONObject and then return it
     *
     * @param unserializedDynasties the HashMap of dynasties to be serialized
     * @return a JSONObject containing the dynasties' serialized data,
     *         where the key for each dynasty is its Wikipedia page URL
     */
    public static JSONObject serializeDynastiesHashMap(HashMap<String, Dynasty> unserializedDynasties) {
        JSONObject serializedHashMap = new JSONObject();

        /*
         * We iterate over all the dynasties in the given HashMap and put
         * them in a JSONObject using their Wikipedia page URL as key and
         * their serialized data as value
         *
         * The result will be something like this:
         *   {
         *      "https://it.wikipedia.org/wiki/Dinastia_costantiniana": {
         *        "pageTitle": "Dinastia costantiniana",
         *        "name": "Constantinian Dynasty",
         *        "founderPageUrl": "https://it.wikipedia.org/wiki/Costanzo_Cloro",
         *        "foundationDate": "293",
         *        "depositionDate": "363 d.C."
         *      },
         *      "https://it.wikipedia.org/wiki/Imperatori_adottivi": {
         *        [...]
         *      },
         *      [...]
         *   }
         */

        Dynasty unserializedDynasty;
        JSONObject serializedDynasty;

        for (Map.Entry<String, Dynasty> set : unserializedDynasties.entrySet()) {
            String dynastyPageUrl = set.getKey();

            /*
             * Skip fake urls (they're used, for example, for the Graph Legend
             * Dynasty) during the dynasties and people serialization process
             */
            if(!dynastyPageUrl.startsWith("fakeurl://")) {
                if(RomanEmperorsScraper.isDebugEnabled()) {
                    System.out.println("\nSerialization in progress for " + dynastyPageUrl + "\n");
                }

                unserializedDynasty = set.getValue();

                serializedDynasty = DynastySerializer.serializeDynasty(unserializedDynasty);

                serializedHashMap.put(dynastyPageUrl, serializedDynasty);
            }
        }

        return serializedHashMap;
    }

    /**
     * Given a JSONObject which represents the serialized form of an HashMap
     * containing some dynasties' data by their Wikipedia page URL, reconstruct
     * the corresponding Dynasty objects and, once the deserialization is completed,
     * return the original HashMap
     *
     * @param serializedHashMap the JSONObject containing the serialized HashMap data
     * @return the reconstructed dynasties HashMap
     */
    public static HashMap<String, Dynasty> deserializeDynastiesHashMap(JSONObject serializedHashMap) {
        HashMap<String, Dynasty> deserializedHashMap = new HashMap<>();

        /*
         * If the JSONObject is empty we just return an empty HashMap
         */

        if(serializedHashMap.isEmpty()) {
            return deserializedHashMap;
        }

        /*
         * Each key in the JSONObject represents a Dynasty's Wikipedia page URL,
         * while to deserialize their data in a Dynasty object (their original form)
         * we use the dedicated constructor from Dynasty class
         */

        String dynastyPageUrl;

        for(Object dynastyPageUrlObject : serializedHashMap.keySet()) {
            dynastyPageUrl = (String) dynastyPageUrlObject;

            if(RomanEmperorsScraper.isDebugEnabled()) {
                System.out.println("\nDe-serialization in progress for " + dynastyPageUrl + "\n");
            }

            deserializedHashMap.put(dynastyPageUrl, new Dynasty(dynastyPageUrl, (JSONObject) serializedHashMap.get(dynastyPageUrl)));
        }

        /*
         * Once the deserialization process is completed we'll have an
         * HashMap containing the dynasties' Dynasty objects by URL
         */

        return deserializedHashMap;
    }
}
