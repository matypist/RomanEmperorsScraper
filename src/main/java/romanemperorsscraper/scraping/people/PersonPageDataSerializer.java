package romanemperorsscraper.scraping.people;

import org.json.simple.JSONObject;
import romanemperorsscraper.run.RomanEmperorsScraper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class providing methods for dynasties
 * people' PersonPageData objects serialization.
 *
 * @see PersonPageData
 * @see PersonNameUrl
 *
 * @author Matteo Collica
 * @author Radu Ionut Barbalata
 */
public final class PersonPageDataSerializer {
    /*
     * PREVENT INSTANTIATION WITH A PRIVATE CONSTRUCTOR
     */

    private PersonPageDataSerializer() {
        throw new java.lang.UnsupportedOperationException("As a utility class, this class must not be instantiated");
    }

    /**
     * Create and return a serialized form of a given PersonPageData object
     *
     * @param personPageData the PersonPageData object to be serialized
     *
     * @return the PersonPageData object serialized as a JSONObject
     */
    public static JSONObject serializePersonPageData(PersonPageData personPageData) {
        JSONObject values = new JSONObject();

        /*
         * Personal information
         */

        values.put("fullName", personPageData.getPersonNameUrl().getFullName());

        values.put("dynastyPageUrl", personPageData.getPersonDynastyPageUrl());

        values.put("birthDate", personPageData.getBirthDate());
        values.put("deathDate", personPageData.getDeathDate());

        values.put("reignBeginningDate", personPageData.getReignBeginningDate());
        values.put("reignEndDate", personPageData.getReignEndDate());

        values.put("role", personPageData.getRole());

        values.put("imageUrl", personPageData.getImageUrl());

        /*
         * Kinship degrees which admit only one person (like biological mother and father)
         */

        values.put("mother", serializePersonNameUrl(personPageData.getMotherNameUrl()));

        values.put("father", serializePersonNameUrl(personPageData.getFatherNameUrl()));

        values.put("adoptiveFather", serializePersonNameUrl(personPageData.getAdoptiveFatherNameUrl()));

        /*
         * Kinship degrees which admit more than one person (like successors, spouses and children)
         */

        values.put("successors", serializeArrayOfPersonNameUrl(personPageData.getSuccessors()));

        values.put("spouses", serializeArrayOfPersonNameUrl(personPageData.getSpouses()));

        values.put("children", serializeArrayOfPersonNameUrl(personPageData.getChildren()));

        values.put("adoptedChildren", serializeArrayOfPersonNameUrl(personPageData.getAdoptedChildren()));

        return values;
    }

    /**
     * Cast an Object to a String
     *
     * @param object the object to be casted as String
     *
     * @return null if the Object is null, the Object casted as String otherwise
     */
    public static String parseJsonString(Object object) {
        if(object == null) {
            return null;
        }

        return object.toString();
    }


    /**
     * Create and return a serialized form of a given PersonNameUrl object
     *
     * @param personNameUrl the PersonNameUrl object to be serialized
     *
     * @return the PersonNameUrl object serialized as a JSONObject
     */
    private static JSONObject serializePersonNameUrl(PersonNameUrl personNameUrl) {
        JSONObject serializedObject = new JSONObject();

        if (personNameUrl == null) {
            return null;
        }

        String fullName = personNameUrl.getFullName();
        String url      = personNameUrl.getUrl();

        if(url == null && fullName == null) {
            return null;
        } else {
            serializedObject.put("fullName", fullName);
            serializedObject.put("url", url);
        }

        return serializedObject;
    }

    /**
     * Restore (and construct) a PersonNameUrl from a JSONObject (its serialized form)
     *
     * @param serializedPersonNameUrl the JSONObject to deserialize data from
     *
     * @return null if the serialized PersonNameUrl is empty or null,
     *         the deserialized PersonNameUrl instance otherwise
     */
    public static PersonNameUrl deserializePersonNameUrl(JSONObject serializedPersonNameUrl) {
        if(serializedPersonNameUrl == null || serializedPersonNameUrl.isEmpty()) {
            return null;
        }

        PersonNameUrl personNameUrl = new PersonNameUrl(serializedPersonNameUrl);

        if(personNameUrl.getFullName() != null || personNameUrl.getUrl() != null) {
            return personNameUrl;
        } else {
            return null;
        }
    }


    /**
     * Create and return a serialized form of a given ArrayList of PersonNameUrl objects
     *
     * @param arrayOfPersonNameUrls an ArrayList of PersonNameUrl objects to be serialized
     *
     * @return the ArrayList of PersonNameUrl(s) serialized as a JSONObject
     */
    private static JSONObject serializeArrayOfPersonNameUrl(ArrayList<PersonNameUrl> arrayOfPersonNameUrls) {
        JSONObject serializedArrayOfPersonNameUrls = new JSONObject();

        if(arrayOfPersonNameUrls != null) {
            for (PersonNameUrl personNameUrl : arrayOfPersonNameUrls) {
                serializedArrayOfPersonNameUrls.put(personNameUrl.getFullName(), personNameUrl.getUrl());
            }
        }

        return serializedArrayOfPersonNameUrls;
    }

    /**
     * Restore (and construct) an ArrayList of PersonNameUrl(s) from a JSONObject (its serialized form)
     *
     * @param serializedArrayOfPersonNameUrls the JSONObject to deserialize data from
     *
     * @return null if the ArrayList of serialized PersonNameUrl(s) is empty or null,
     *         the deserialized ArrayList of PersonNameUrl instances otherwise
     */
    public static ArrayList<PersonNameUrl> deserializeArrayOfPersonNameUrl(JSONObject serializedArrayOfPersonNameUrls) {
        ArrayList<PersonNameUrl> deserializedArrayOfPersonNameUrls = new ArrayList<>();

        if(serializedArrayOfPersonNameUrls == null || serializedArrayOfPersonNameUrls.isEmpty()) {
            return deserializedArrayOfPersonNameUrls;
        }

        String personName, personPageUrl;

        for(Object personNameObject : serializedArrayOfPersonNameUrls.keySet()) {
            personName    = (String) personNameObject;
            personPageUrl = PersonPageDataSerializer.parseJsonString(serializedArrayOfPersonNameUrls.get(personName));

            deserializedArrayOfPersonNameUrls.add(new PersonNameUrl(personName, personPageUrl));
        }

        return deserializedArrayOfPersonNameUrls;
    }

    /**
     * Given an HashMap containing PersonPageData instances by URL,
     * serialize it in a single JSONObject and then return it
     *
     * @param urlPersonPageDataMatches the HashMap of PersonPageData
     *                                 instances to be serialized
     *
     * @return a JSONObject containing the serialized PersonPageData instances,
     *         where the key for each PersonPageData is its Wikipedia page URL
     */
    public static JSONObject serializePersonPageDataHashMap(HashMap<String, PersonPageData> urlPersonPageDataMatches) {
        JSONObject serializedHashMap = new JSONObject();

        PersonPageData personPageData;
        String personDynastyPageUrl;
        JSONObject serializedPersonPageData;

        for (Map.Entry<String, PersonPageData> set : urlPersonPageDataMatches.entrySet()) {
            personPageData = set.getValue();

            personDynastyPageUrl = personPageData.getPersonDynastyPageUrl();

            /*
             * Due to the graph legend dynasty being a fake local
             * dynasty there is no need to import or export it to file
             */

            if(personDynastyPageUrl == null || !personDynastyPageUrl.startsWith("fakeurl://")) {
                if(RomanEmperorsScraper.isDebugEnabled()) {
                    System.out.println("Serialization in progress for " + set.getKey());
                }

                serializedPersonPageData = PersonPageDataSerializer.serializePersonPageData(personPageData);

                serializedHashMap.put(set.getKey(), serializedPersonPageData);
            }
        }

        return serializedHashMap;
    }

    /**
     * Given a JSONObject which represents the serialized form of an HashMap
     * containing some PersonPageData instances by their Wikipedia page URL,
     * reconstruct the corresponding PersonPageData objects and, once the
     * deserialization is completed, return the original HashMap
     *
     * @param serializedHashMap the JSONObject to deserialize data from
     *
     * @return the deserialized HashMap of PersonPageData
     *         instances by their Wikipedia page URL
     */
    public static HashMap<String, PersonPageData> deserializePersonPageDataHashMap(JSONObject serializedHashMap) {
        HashMap<String, PersonPageData> deserializedHashMap = new HashMap<>();

        if(serializedHashMap.isEmpty()) {
            return deserializedHashMap;
        }

        String url;

        for(Object urlObject : serializedHashMap.keySet()) {
            url = (String) urlObject;

            if(RomanEmperorsScraper.isDebugEnabled()) {
                System.out.println("De-serialization in progress for " + url);
            }

            deserializedHashMap.put(url, new PersonPageData(url, (JSONObject) serializedHashMap.get(url)));
        }

        return deserializedHashMap;
    }
}
