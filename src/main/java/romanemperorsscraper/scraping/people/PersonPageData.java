package romanemperorsscraper.scraping.people;

import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import romanemperorsscraper.scraping.dynasties.Dynasty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Class used to get and represent information
 * about people related to Roman Emperors Dynasties.
 *
 * @see Person
 * @see PersonNameUrl
 * @see PersonPageDataSerializer
 *
 * @author Radu Ionut Barbalata
 */
public class PersonPageData {
    /*
     * ATTRIBUTES
     */

    private PersonNameUrl personNameUrl;
    private String personDynastyPageUrl = null;

    private String imageUrl;
    private String role = "Normal";

    private String birthDate;
    private String deathDate;

    private String reignBeginningDate;
    private String reignEndDate;

    private PersonNameUrl motherNameUrl;
    private PersonNameUrl fatherNameUrl;
    private PersonNameUrl adoptiveFatherNameUrl;

    private ArrayList<PersonNameUrl> successors = new ArrayList<>();
    private ArrayList<PersonNameUrl> spouses = new ArrayList<>();
    private ArrayList<PersonNameUrl> children = new ArrayList<>();
    private ArrayList<PersonNameUrl> adoptedChildren = new ArrayList<>();

    /*
     * CLASS ATTRIBUTES
     */

    private static HashMap<String, PersonPageData> urlPersonPageDataMatches = new HashMap<>();
    private static HashMap<String, HashMap<String, String>> dynastiesPeopleList = new HashMap<>();

    private static HashSet<String> months = new HashSet<>();

    static {
        months.add("gennaio");
        months.add("febbraio");
        months.add("marzo");
        months.add("aprile");
        months.add("maggio");
        months.add("giugno");
        months.add("luglio");
        months.add("agosto");
        months.add("settembre");
        months.add("ottobre");
        months.add("novembre");
        months.add("dicembre");
    }

    /**
     * Construct a PersonPageData object or return it if it was already constructed
     *
     * @param webDriver the Web Driver instance to be used to scrape data
     * @param personNameUrl PersonNameUrl object of the person
     * @param dynastyPageUrl the dynasty's Wikipedia page url
     *
     * @return the constructed PersonPageData object
     */
    public static PersonPageData getPersonPageData(WebDriver webDriver, PersonNameUrl personNameUrl, String dynastyPageUrl) {
        String url = personNameUrl.getUrl();
        PersonPageData personPageData;

        if(urlPersonPageDataMatches.containsKey(url)) {
            personPageData = urlPersonPageDataMatches.get(url);
        } else {
            personPageData = new PersonPageData(webDriver, personNameUrl, dynastyPageUrl);
        }

        /*
         * IF:
         * - The person has a Wikipedia page;
         * - The person doesn't have a Dynasty
         *   OR the person has a dynasty, and it is equal
         *      to the one identified by the dynastyPageUrl.
         *
         * THEN:
         * - Add this person to that dynasty's HashMap
         *   so that it can be later retrieved for the
         *   PeopleList
         */

        if(personPageData.getPersonNameUrl().getUrl() != null
                && (personPageData.getPersonDynastyPageUrl() == null
                        || Dynasty.dynastyPageUrlEquals(dynastyPageUrl, personPageData.getPersonDynastyPageUrl()))) {

            /*
             * Create the dynasty's HashMap inside the dynastiesPeopleList HashMap
             * if it doesn't already exist
             */

            if(!dynastiesPeopleList.containsKey(dynastyPageUrl)){
                dynastiesPeopleList.put(dynastyPageUrl, new HashMap<>());
            }

            String key;

            /*
             * The ID will consist of the person's full name
             * concatenated with their date of birth (when it
             * is available) to prevent homonyms
             */

            if(personPageData.getBirthDate() != null) {
                key = personPageData.getPersonNameUrl().getFullName()+" ("+ personPageData.getBirthDate()+")";
            } else {
                key = personPageData.getPersonNameUrl().getFullName();
            }

            dynastiesPeopleList.get(dynastyPageUrl).put(key, personPageData.getPersonNameUrl().getUrl());
        }

        return personPageData;
    }

    /**
     * Return an already created PersonPageData object or null
     *
     * @param personPageUrl the person's Wikipedia page URL
     * @param dynastyPageUrl the dynasty's Wikipedia page URL
     *
     * @return PersonPageData object relative to the given URL
     */
    public static PersonPageData getCachedPersonPageData(String personPageUrl, String dynastyPageUrl) {
        if(urlPersonPageDataMatches.containsKey(personPageUrl)) {
            PersonPageData personPageData = urlPersonPageDataMatches.get(personPageUrl);

            if(!dynastiesPeopleList.containsKey(dynastyPageUrl)){
                dynastiesPeopleList.put(dynastyPageUrl, new HashMap<>());
            }

            String key;

            /*
             * The ID will consist of the person's full name
             * concatenated with their date of birth (when it
             * is available) to prevent homonyms
             */

            if(personPageData.getBirthDate() != null) {
                key = personPageData.getPersonNameUrl().getFullName()+" ("+ personPageData.getBirthDate()+")";
            } else {
                key = personPageData.getPersonNameUrl().getFullName();
            }

            /*
             * Differently than in the getPersonPageData method, here we don't
             * check whether the person has a Wikipedia page and if he/she is of
             * the same dynasty because this method is only used for dynasty founders
             */

            dynastiesPeopleList.get(dynastyPageUrl).put(key, personPageData.getPersonNameUrl().getUrl());

            return personPageData;
        }

        return null;
    }

    /**
     * Fulfill the fields of a PersonPageData object from a JSON object
     *
     * @param personPageUrl the person's Wikipedia page URL
     * @param serializedPersonPageData the JSONObject to deserialize data from
     */
    public PersonPageData(String personPageUrl, JSONObject serializedPersonPageData) {
        this.personNameUrl = new PersonNameUrl((String) serializedPersonPageData.get("fullName"), personPageUrl);

        /*
         * We add this PersonPageData instance to the urlPersonPageDataMatches
         * HashMap so that it can be later retrieved by its Wikipedia page URL
         */

        if(personNameUrl.getUrl() != null) {
            addToUrlPersonPageDataMatches(personNameUrl.getUrl(), this);
        }

        /*
         * Personal information
         */

        this.personDynastyPageUrl = PersonPageDataSerializer.parseJsonString(serializedPersonPageData.get("dynastyPageUrl"));

        this.birthDate = PersonPageDataSerializer.parseJsonString(serializedPersonPageData.get("birthDate"));
        this.deathDate = PersonPageDataSerializer.parseJsonString(serializedPersonPageData.get("deathDate"));

        this.reignBeginningDate = PersonPageDataSerializer.parseJsonString(serializedPersonPageData.get("reignBeginningDate"));
        this.reignEndDate = PersonPageDataSerializer.parseJsonString(serializedPersonPageData.get("reignEndDate"));

        this.role = PersonPageDataSerializer.parseJsonString(serializedPersonPageData.get("role"));
        this.imageUrl = PersonPageDataSerializer.parseJsonString(serializedPersonPageData.get("imageUrl"));

        /*
         * Kinship degrees which admit only one person (like biological mother and father)
         */

        this.motherNameUrl = PersonPageDataSerializer.deserializePersonNameUrl((JSONObject) serializedPersonPageData.get("mother"));

        this.fatherNameUrl = PersonPageDataSerializer.deserializePersonNameUrl((JSONObject) serializedPersonPageData.get("father"));

        this.adoptiveFatherNameUrl = PersonPageDataSerializer.deserializePersonNameUrl((JSONObject) serializedPersonPageData.get("adoptiveFather"));

        /*
         * Kinship degrees which admit more than one person (like successors, spouses and children)
         */

        this.successors = PersonPageDataSerializer.deserializeArrayOfPersonNameUrl((JSONObject) serializedPersonPageData.get("successors"));

        this.spouses = PersonPageDataSerializer.deserializeArrayOfPersonNameUrl((JSONObject) serializedPersonPageData.get("spouses"));

        this.children = PersonPageDataSerializer.deserializeArrayOfPersonNameUrl((JSONObject) serializedPersonPageData.get("children"));

        this.adoptedChildren = PersonPageDataSerializer.deserializeArrayOfPersonNameUrl((JSONObject) serializedPersonPageData.get("adoptedChildren"));

    }

    /**
     * Fulfill the fields of a PersonPageData object with
     * the information obtained during the scraping of
     * a dynasty member's Wikipedia page
     *
     * @param webDriver the Web Driver instance to be used to scrape data
     * @param personNameUrl the person's PersonNameUrl object
     * @param dynastyPageUrl the Wikipedia page URL of the dynasty we're currently scraping on
     */
    public PersonPageData(WebDriver webDriver, PersonNameUrl personNameUrl, String dynastyPageUrl) {
        this.personNameUrl = personNameUrl;

        /*
         * We add this PersonPageData instance to the urlPersonPageDataMatches
         * HashMap so that it can be later retrieved by its Wikipedia page URL
         */

        if(this.personNameUrl.getUrl() != null) {
            addToUrlPersonPageDataMatches(this.personNameUrl.getUrl(), this);
        }

        String wikipediaPageUrl = personNameUrl.getUrl();

        webDriver.get(wikipediaPageUrl);

        /*
         * imageUrl
         */

        WebElement imageElement = null;

        try {
            imageElement = webDriver.findElement(By.className("floatnone"));
        } catch(Exception ex) {
            try{
                imageElement = webDriver.findElement(By.className("thumbinner"));
            } catch (Exception ignored) {}
        }

        /*
         * The image may also not be present in the page
         */

        if (imageElement != null){
            this.imageUrl = imageElement.findElement(By.tagName("a")).findElement(By.tagName("img")).getAttribute("src");
        } else {
            this.imageUrl = null;
        }

        /*
         * The person's information are contained in the "sinottico"
         * HTML element, if it isn't found on the page then we stop
         * immediately as there are no more data to scrape
         */

        WebElement summaryElement;

        try {
            summaryElement = webDriver.findElement(By.className("sinottico"));
        } catch(Exception ignored) {
            return;
        }

        boolean searchForRole = true;

        List<WebElement> linesOfSummaryTable = summaryElement.findElements(By.tagName("tr"));
        for (WebElement line : linesOfSummaryTable) {
            try {
                WebElement categoryInformationElement = line.findElement(By.tagName("th"));

                String lineText = line.getText();

                /*
                 * We can't really expect the exact line where the
                 * role (Emperor or Dictator) will be written in,
                 * so we just check for it in each line of the
                 * summary table
                 */

                if(searchForRole) {
                    if (textImpliesDictatorRole(lineText)) {
                        this.role = "Dictator";

                        searchForRole = false;

                    } else if (textImpliesEmperorRole(lineText)) {
                        this.role = "Emperor";

                        searchForRole = false;
                    }
                }

                if (categoryInformationElement != null) {
                    String informationType = categoryInformationElement.getText().replace("\n", "");
                    WebElement informationDataElement = line.findElement(By.tagName("td"));

                    switch (informationType.toLowerCase()) {
                        case ("successore"):
                            /*
                             * We clear the data from Brackets and their content as
                             * they never contain successors but instead stuff like
                             * numbers
                             */

                            String successorsText = informationDataElement.getAttribute("innerText");

                            /*
                             * We iterate over all the successors and add them
                             * to the PersonPageData successors ArrayList
                             */

                            String successorsNames = clearBrackets(successorsText).toString();

                            List<WebElement> successorsAnchorElements = informationDataElement.findElements(By.tagName("a"));

                            for (WebElement successorAnchorElement : successorsAnchorElements) {
                                String successorLinkedName = successorAnchorElement.getText();

                                if (successorsNames.contains(successorLinkedName)) {
                                    this.successors.add(new PersonNameUrl(
                                        successorLinkedName,
                                        successorAnchorElement.getAttribute("href")
                                    ));
                                }
                            }

                            break;


                        case ("dinastia"):
                            WebElement dynastyAnchorElement = informationDataElement.findElement(By.tagName("a"));

                            this.personDynastyPageUrl = dynastyAnchorElement.getAttribute("href");

                            break;


                        case ("morte"):
                        case ("nascita"):
                            String informationText = informationDataElement.getAttribute("innerText");

                            /*
                             * We clear the string from \n because it can be a separator
                             * between number and words (which can represent a month)
                             *
                             * We also clear the data from the brackets and their content
                             * because they contain superficial or useless information
                             * for our goal, and we replace "°" with "" because there can
                             * be numbers presented like [1°]
                             */

                            informationText = informationText.replace("\n", " ");
                            informationText = informationText.replace("°", "");

                            StringBuilder clearedInformations = clearBrackets(informationText);
                            StringBuilder currentInformation = new StringBuilder();

                            /*
                             * Normalize all non-breaking spaces (having ASCII code 160)
                             * to simple space characters (having ASCII code 32), a
                             * replacement needed to be able to split on spaces later on
                             */

                            for (int indx = 0; indx < clearedInformations.length(); indx += 1) {
                                Character character = clearedInformations.charAt(indx);

                                int asciiValue = character;

                                if (asciiValue == 32 || asciiValue == 160) {
                                    currentInformation.append(" ");
                                } else {
                                    currentInformation.append(character);
                                }
                            }

                            String[] singleInformation = String.valueOf(currentInformation).split(" ");
                            StringBuilder date = new StringBuilder(extractDates(singleInformation));

                            if (informationType.equalsIgnoreCase("morte")) {
                                this.deathDate = date.toString().trim();
                            } else {
                                this.birthDate = date.toString().trim();
                            }

                            break;


                        case ("dittatura"):
                        case ("in carica"):
                        case ("regno"):
                            ArrayList<String> datesOfReign = new ArrayList<>();
                            String reignData = informationDataElement.getAttribute("innerText");

                            reignData = clearBrackets(reignData).toString();
                            reignData = reignData.replace("º", "");

                            /*
                             * The separator between the beginning date of reign and
                             * the reign end date can be "–", "-" or "\n"
                             */

                            reignData = reignData.replace("–", "\n");
                            reignData = reignData.replace("-", "\n");

                            String[] listDates = reignData.split("\n");

                            for (String reignDate : listDates) {
                                datesOfReign.add((extractDates(reignDate.split(" "))).toString());
                            }

                            this.reignBeginningDate = datesOfReign.get(0).trim();

                            /*
                             * Search the reign end date starting from the
                             * last array index backwards
                             */

                            int indexOfString = datesOfReign.size() - 1;

                            while (datesOfReign.get(indexOfString).isEmpty()){
                                indexOfString--;
                            }

                            this.reignEndDate = datesOfReign.get(indexOfString).trim();

                            /*
                             * Since in the extractDates method we automatically put
                             * d.C. if the date doesn't contain neither d.C. neither a.C.,
                             * we replace it here for the reignBeginningDate with a.C.
                             * if the reignEndDate contains a.C.
                             *
                             * e.g. reignBeginningDate = 16 gennaio 27 d.C.
                             *      reignEndDate = 10 agosto 14 a.C.
                             *      => reignBeginningDate = 16 gennaio 27 a.C.
                             */

                            if (this.reignEndDate.contains("a.C")){
                                this.reignBeginningDate = this.reignBeginningDate.replace("d.C", "a.C");
                            }

                            break;


                        case ("consorte di"):
                        case ("consorte"):
                        case ("coniugi"):
                        case ("coniuge"):
                            ArrayList<String> spousesNames = new ArrayList<>();

                            String[] cleanedLines = clearBrackets(informationDataElement.getAttribute("innerText")).toString().replace(";", "\n").replace(",", "\n").split("\n");
                            for (String cleanedLine : cleanedLines) {
                                /*
                                 * If a line starts with an UpperCase letter and
                                 * doesn't end with the '?' symbol then it's
                                 * supposed to be a valid name as invalid
                                 * content were already cleaned before from
                                 * the line. We then add it to the ArrayList.
                                 */

                                if (!cleanedLine.isEmpty() && cleanedLine.substring(0, 1).equals(cleanedLine.substring(0, 1).toUpperCase()) && !cleanedLine.startsWith(" ")  && !cleanedLine.endsWith("?") ){
                                    spousesNames.add(cleanedLine.trim());
                                }
                            }

                            /*
                             * getPersonNameUrls returns an ArrayList of PersonNameUrl(s) by
                             * connecting each spouse name to the URLs it points to
                             */

                            ArrayList<PersonNameUrl> spousesPersonNameUrls = getPersonNameUrls(spousesNames, informationDataElement);
                            for (PersonNameUrl spousePersonNameUrl : spousesPersonNameUrls) {
                                String spouseName = spousePersonNameUrl.getFullName();

                                spousesNames.remove(spouseName);

                                this.spouses.add(spousePersonNameUrl);
                            }

                            /*
                             * The remaining spouses (those without a Wikipedia page URL) are
                             * added with a PersonNameUrl which contains an empty URL instead
                             */

                            if (!spousesNames.isEmpty()){
                                for (String spouseName : spousesNames){
                                    PersonNameUrl person = new PersonNameUrl(spouseName, null);

                                    this.spouses.add(person);
                                }
                            }

                            break;


                        case ("figli"):
                            ArrayList<String> children             = new ArrayList<>();
                            ArrayList<String> adoptedChildrenNames = new ArrayList<>();
                            ArrayList<String> allChildrenNames     = new ArrayList<>();

                            boolean adoptiveChildrenState = false;

                            String childrenString = informationDataElement.getAttribute("innerText");

                            String[] childrenNames = childrenString.split("\n");
                            for (String childData : childrenNames) {
                                /*
                                 * Check if it's a valid name, so if it starts with an UpperCase
                                 */

                                if (childData.substring(0, 1).equals(childData.substring(0, 1).toLowerCase())){
                                    continue;
                                }

                                /*
                                 * Pick as the name all the characters until a
                                 * bracket ('(' or '['), comma (',') or semicolon
                                 * (';') is found (as it usually mean the end of
                                 * the name)
                                 */

                                StringBuilder name = new StringBuilder();
                                for (int i = 0; i < childData.length(); i++) {

                                    if (childData.charAt(i) == '(' | (childData.charAt(i) == '[') | (childData.charAt(i) ==';') | (childData.charAt(i) ==',')) {
                                        break;
                                    }

                                    name.append(childData.charAt(i));
                                }

                                /*
                                 * The start of the adopted children names list
                                 * is usually indicated by an "Adott" substring
                                 * like "Adottivi:"
                                 */

                                if (name.toString().toLowerCase().contains("adott")){
                                    adoptiveChildrenState = true;
                                    continue;
                                }

                                if (!adoptiveChildrenState) {
                                    /*
                                     * If a person have few children usually
                                     * the adopted ones are represented like:
                                     * "<name> (adottato/adottivo)"
                                     */

                                    if (childData.contains("adott")) {
                                        adoptedChildrenNames.add(name.toString().trim());
                                    } else {
                                        /*
                                         * Twins are usually represented with a
                                         * middle "e" separator like:
                                         * "<first_son_name> e <second_son_name>"
                                         */

                                        for (String child : name.toString().split(" e ")) {
                                            children.add(child.trim());
                                        }
                                    }
                                } else {
                                    adoptedChildrenNames.add(name.toString().trim());
                                }

                                allChildrenNames.add(name.toString().trim());
                            }

                            ArrayList<PersonNameUrl> childrenPersonNameUrls = getPersonNameUrls(allChildrenNames, informationDataElement);
                            for (PersonNameUrl childPersonNameUrl : childrenPersonNameUrls) {
                                String childName = childPersonNameUrl.getFullName();

                                if (children.contains(childName)) {
                                    this.children.add(childPersonNameUrl);

                                    children.remove(childName);
                                } else {
                                    this.adoptedChildren.add(childPersonNameUrl);

                                    adoptedChildrenNames.remove(childName);
                                }
                            }

                            /*
                             * The remaining children (those without a Wikipedia page URL) are
                             * added with a PersonNameUrl which contains an empty URL instead
                             */

                            if (!children.isEmpty()){
                                for (String child : children) {
                                    PersonNameUrl
                                            name = new PersonNameUrl(child, null);

                                    this.children.add(name);
                                }
                            }

                            /*
                             * The remaining adopted children (those without a Wikipedia page URL)
                             * are added with a PersonNameUrl which contains an empty URL instead
                             */

                            if (!adoptedChildrenNames.isEmpty()){
                                for(String child : adoptedChildrenNames){
                                    PersonNameUrl
                                            name = new PersonNameUrl(child, null);
                                    this.adoptedChildren.add(name);
                                }
                            }

                            break;


                        case ("madre"):

                            ArrayList<String> mothers = new ArrayList<>();

                            String mothersString = informationDataElement.getAttribute("innerText");
                            mothersString = clearBrackets(mothersString).toString().replace(";", ",").replace(";", "\n");

                            String[] mothersNames = mothersString.split("\n");

                            for (String motherName : mothersNames) {
                                /*
                                 * Check if it's a valid name, so if it starts with an UpperCase
                                 */
                                motherName = motherName.trim();
                                if (motherName.substring(0, 1).equals(motherName.substring(0, 1).toUpperCase())){
                                    mothers.add(motherName);
                                }
                            }

                            ArrayList<PersonNameUrl> momsPersonNameUrls = getPersonNameUrls(mothers, informationDataElement);

                            if (momsPersonNameUrls.isEmpty()){
                                this.motherNameUrl = new PersonNameUrl(mothers.get(0), null);
                            }
                            else {
                                this.motherNameUrl = momsPersonNameUrls.get(0);
                            }

                            break;


                        case ("padre"):
                            ArrayList<String> fathersNames = new ArrayList<>();
                            ArrayList<String> fatherNamesPosition = new ArrayList<>();

                            String fathersString = informationDataElement.getAttribute("innerText");
                            fathersString = clearBrackets(fathersString).toString();
                            fathersString = fathersString.replace("Adottivo:", "").replace(",", ";").replace(";", "\n");
                            String[] fathersPossibleNames = fathersString.split("\n");

                            for (String fatherName : fathersPossibleNames) {
                                /*
                                 * Check if it's a valid name, so if it starts with an UpperCase
                                 */
                                fatherName = fatherName.trim();
                                if (!fatherName.isEmpty() && fatherName.substring(0, 1).equals(fatherName.substring(0, 1).toUpperCase())){
                                    fathersNames.add(fatherName);
                                    fatherNamesPosition.add(fatherName);
                                }
                            }

                            /*
                             * [1] The adoptive father is usually (from our researches)
                             * in the second line of the fathersString, like:
                             * "<blood_father_name> \n <adoptive_father_name>"
                             */

                            ArrayList<PersonNameUrl> fathersPersonNameUrls = getPersonNameUrls(fathersNames, informationDataElement);

                            for (PersonNameUrl fatherPersonNameUrl : fathersPersonNameUrls){
                                /*
                                 * [1]
                                 */

                                if (this.fatherNameUrl == null && fatherNamesPosition.get(0).equals(fatherPersonNameUrl.getFullName())){
                                    this.fatherNameUrl = fatherPersonNameUrl;
                                } else {
                                    this.adoptiveFatherNameUrl = fatherPersonNameUrl;
                                }

                                fatherNamesPosition.remove(fatherPersonNameUrl.getFullName());
                            }

                            for (String father : fatherNamesPosition) {
                                /*
                                 * [1]
                                 */

                                if(this.fatherNameUrl == null) {
                                    this.fatherNameUrl = new PersonNameUrl(father, null);
                                } else {
                                    this.adoptiveFatherNameUrl = new PersonNameUrl(father, null);
                                }
                            }

                            break;
                    }
                }
            } catch(Exception ignored) {}
        }

        /*
         * There may be some dynasty members which
         * have a reign beginning and end date even
         * if they aren't emperors themselves (like
         * emperors' spouses), in these cases we
         * empty the values
         */

        if(!isEmperorOrDictator()) {
            this.successors = null;

            this.reignBeginningDate = null;
            this.reignEndDate = null;
        }

        /*
         * IF:
         * - The person has a Wikipedia page;
         * - The person doesn't have a Dynasty
         *   OR the person has a dynasty, and it is equal
         *      to the one identified by the dynastyPageUrl.
         *
         * THEN:
         * - Add this person to that dynasty's HashMap
         *   so that it can be later retrieved for the
         *   PeopleList
         */

        if(this.personNameUrl.getUrl() != null && getDynastiesPeopleList() != null &&
                (getPersonDynastyPageUrl() == null || Dynasty.dynastyPageUrlEquals(dynastyPageUrl, getPersonDynastyPageUrl()))) {
            /*
             * Create the dynasty's HashMap inside the dynastiesPeopleList HashMap
             * if it doesn't already exist
             */

            if(!dynastiesPeopleList.containsKey(dynastyPageUrl)){
                dynastiesPeopleList.put(dynastyPageUrl, new HashMap<>());
            }

            String key;

            /*
             * The ID will consist of the person's full name
             * concatenated with their date of birth (when it
             * is available) to prevent homonyms
             */

            if(getBirthDate() != null) {
                key = this.personNameUrl.getFullName() + " (" + getBirthDate() + ")";
            } else {
                key = this.personNameUrl.getFullName();
            }

            dynastiesPeopleList.get(dynastyPageUrl).put(key, this.personNameUrl.getUrl());
        }
    }


    /**
     * Add a PersonPageData instance to the urlPersonPageDataMatches HashMap
     *
     * @param url the Wikipedia page URL to be used to later retrieve it
     * @param personPageData the PersonPageData instance
     */
    public static void addToUrlPersonPageDataMatches(String url, PersonPageData personPageData) {
        urlPersonPageDataMatches.put(url, personPageData);
    }

    /**
     * Replace urlPersonPageDataMatches with the given one. Used to replace all
     * the PersonPageData stored instances with new ones (e.g. when importing
     * data from JSON files)
     *
     * @param urlPersonPageDataMatches the new urlPersonPageDataMatches HashMap content
     */
    public static void setUrlPersonPageDataMatches(HashMap<String, PersonPageData> urlPersonPageDataMatches) {
        PersonPageData.urlPersonPageDataMatches = urlPersonPageDataMatches;
    }

    /**
     * @return the urlPersonPageDataMatches HashHap of Wikipedia page URL :
     * PersonPageData entries
     */
    public static HashMap<String, PersonPageData> getUrlPersonPageDataMatches() {
        return urlPersonPageDataMatches;
    }

    /**
     * Check if a given line of text contains something which
     * implies the emperor role
     *
     * @param textLine the line of text to check
     * @return true if the line implies the emperor role, false otherwise
     */
    public static boolean textImpliesEmperorRole(String textLine) {
        String lowerCaseTextLine = textLine.toLowerCase();
        lowerCaseTextLine = clearBrackets(lowerCaseTextLine).toString();

        return !lowerCaseTextLine.contains("consorte del") &&
                (lowerCaseTextLine.contains("imperatore romano") ||
                lowerCaseTextLine.contains("augusto d'occidente dell'impero romano") ||
                lowerCaseTextLine.contains("cesare d'occidente dell'impero romano") ||
                lowerCaseTextLine.contains("cesare e poi augusto dell'impero romano") ||
                lowerCaseTextLine.contains("augusto d'oriente dell'impero romano") ||
                lowerCaseTextLine.contains("augusto dell'impero romano"));
    }

    /**
     * Check if a given line of text contains something which
     * implies the dictator role
     *
     * @param textLine the line of text to check
     *
     * @return true if the line implies the dictator role, false otherwise
     */
    public static boolean textImpliesDictatorRole(String textLine) {
        String lowerCaseTextLine = textLine.toLowerCase();

        return !lowerCaseTextLine.contains("consorte del") &&
                lowerCaseTextLine.contains("dittatore");
    }

    /**
     * Check for every element in the input if it can be
     * part of a possible date, so the output contain
     * numbers, months, a.C or d.C, and ->
     * as well it contains particular cases like an or in the middle of two dates
     * date1 or date2, and the output will be date1 or date2
     *
     * @param possibleDates a list of Strings
     * @return StringBuilder object with the date
     */
    private static StringBuilder extractDates(String[] possibleDates) {
        StringBuilder cleanedDate = new StringBuilder();
        int datesCount = 0;

        for (String data : possibleDates) {
            /*
             * Each element of the possibleDates String array may be:
             * - a number;
             * - something else (like a.C., d.C., ->, [...]).
             *
             * We check if it's a number by trying the parse as integer,
             * if it raises a NumberFormatException then we manage it
             * as something else.
             */

            try {
                Integer.parseInt(data);
                cleanedDate.append(data).append(" ");
            } catch (NumberFormatException e) {
                if (months.contains(data) || data.equals("a.C.") || data.equals("d.C.") || data.equals("->")) {
                    if (data.equals("->") && datesCount < 1) {
                        /*
                         * -> means than we are at the end of the
                         * information about the current date, so
                         * if neither a d.C. nor an a.C. was found
                         * we append a d.C. before the -> symbol
                         */

                        cleanedDate.append("d.C. ");
                        datesCount = 1;
                    }

                    if (data.equals("a.C.") || data.equals("d.C.")) {
                        datesCount += 1;
                    }

                    cleanedDate.append(data).append(" ");

                } else if (data.contains("/")) {
                    cleanedDate.append(data).append(" ");

                } else if (data.equals("o")){
                    cleanedDate.append("o ");
                }
            }
        }

        /*
         * If neither a d.C. nor an a.C. was found
         * we append a d.C. to the end of the string
         */

        if (datesCount == 0 && cleanedDate.length() != 0) {
            cleanedDate.append("d.C");
        }

        return cleanedDate;
    }

    /**
     * Clean a given information string by
     * removing brackets and characters inside
     *
     * @param information the given information string
     * @return the cleaned result as a StringBuilder instance
     */
    private static StringBuilder clearBrackets(String information) {
        StringBuilder cleanedInformation = new StringBuilder();

        int bracketsOpen = 0;

        /*
         * Ignore characters when they're inside brackets,
         * add them to the cleaned string otherwise
         */

        for (int i = 0; i < information.length(); i++) {
            String currentChar = information.substring(i, i + 1);

            if (bracketsOpen == 0) {
                if (currentChar.equals("(") || currentChar.equals("[")) {
                    bracketsOpen++;
                } else {
                    cleanedInformation.append(currentChar);
                }
            } else {
                if (currentChar.equals(")") || currentChar.equals("]")) {
                    bracketsOpen--;
                } else if (currentChar.equals("(") || currentChar.equals("[")) {
                    bracketsOpen++;
                }
            }
        }

        return cleanedInformation;
    }

    /**
     * For each link contained in the informationDataElement we check if its
     * text is also contained in the peopleNames ArrayList of strings and
     * eventually add it to an output ArrayList if that's true
     *
     * @param peopleNames an ArrayList containing the people names
     * @param informationDataElement a WebElement containing the people anchor
     *                               elements with their text and pointed page URL
     * @return an ArrayList containing the PersonNameUrl(s) of all the people
     * with a Wikipedia page URL
     */
    private static ArrayList<PersonNameUrl> getPersonNameUrls(ArrayList<String> peopleNames, WebElement informationDataElement) {
        ArrayList<PersonNameUrl> personNameUrls = new ArrayList<>();
        List<WebElement> peopleElements = informationDataElement.findElements(By.tagName("a"));

        for (WebElement personElement : peopleElements) {
            String personName = personElement.getText();
            String personUrl  = personElement.getAttribute("href");

            if (peopleNames.contains(personName)){
                /*
                 * Urls which point to an empty Wikipedia page
                 * (recognized for their w/ prefix) are here
                 * considered as invalid URLs
                 */

                if (personUrl.startsWith("w/", 25)){
                    personUrl = null;
                }

                PersonNameUrl
                        personNameUrl = new PersonNameUrl(personName, personUrl);
                personNameUrls.add(personNameUrl);
                peopleNames.remove(personName);
            }
        }

        return personNameUrls;
    }

    /**
     * @return true if the person's role is Emperor or Dictator, false otherwise
     */
    public boolean isEmperorOrDictator() {
        return this.getRole().equals("Dictator") || this.getRole().equals("Emperor");
    }

    /*
     * GETTERS
     */

    /**
     * @return the PersonNameUrl instance related to this PersonPageData
     */
    public PersonNameUrl getPersonNameUrl() {
        return personNameUrl;
    }


    /**
     * @return the PersonNameUrl instance related to this person's mother
     */
    public PersonNameUrl getMotherNameUrl() {
        return motherNameUrl;
    }


    /**
     * @return the PersonNameUrl instance related to this person's father
     */
    public PersonNameUrl getFatherNameUrl() {
        return fatherNameUrl;
    }


    /**
     * @return the PersonNameUrl instance related to this person's adoptive father
     */
    public PersonNameUrl getAdoptiveFatherNameUrl() {
        return adoptiveFatherNameUrl;
    }


    /**
     * @return an ArrayList containing the PersonNameUrl instance of each successor
     */
    public ArrayList<PersonNameUrl> getSuccessors() {
        return successors;
    }


    /**
     * @return an ArrayList containing the PersonNameUrl instance of each spouse
     */
    public ArrayList<PersonNameUrl> getSpouses() {
        return spouses;
    }


    /**
     * @return an ArrayList containing the PersonNameUrl instance of each child
     */
    public ArrayList<PersonNameUrl> getChildren() {
        return children;
    }


    /**
     * @return an ArrayList containing the PersonNameUrl instance of each adopted child
     */
    public ArrayList<PersonNameUrl> getAdoptedChildren() {
        return adoptedChildren;
    }


    /**
     * @return the person's birthdate
     */
    public String getBirthDate() {
        return birthDate;
    }


    /**
     * @return the person's death date
     */
    public String getDeathDate() {
        return deathDate;
    }


    /**
     * @return the person's reign beginning date. It may be null if it
     * isn't an emperor or a dictator.
     */
    public String getReignBeginningDate() {
        return reignBeginningDate;
    }


    /**
     * @return the person's reign end date. It may be null if it
     * isn't an emperor or a dictator.
     */
    public String getReignEndDate() {
        return reignEndDate;
    }


    /**
     * @return the Wikipedia page URL of the person's dynasty
     */
    public String getPersonDynastyPageUrl() {
        return personDynastyPageUrl;
    }

    /**
     * @return the person's role (Emperor or Dictator)
     */
    public String getRole() {
        return role;
    }


    /**
     * @return the person's image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @return an HashMap having as key the dynasties' Wikipedia page URLs and as
     * value another HashMap containing the dynasty people with the name-birthdate
     * as key and the Wikipedia page URL as value
     */
    public static HashMap<String, HashMap<String, String>> getDynastiesPeopleList() {
        return dynastiesPeopleList;
    }
}