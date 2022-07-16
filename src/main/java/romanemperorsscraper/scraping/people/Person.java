package romanemperorsscraper.scraping.people;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.openqa.selenium.WebDriver;
import romanemperorsscraper.scraping.dynasties.Dynasty;
import romanemperorsscraper.scraping.dynasties.DynastyTree;
import romanemperorsscraper.scraping.dynasties.GraphStreamUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class offering a recursive constructor to
 * visit, starting from a Dynasty founder,
 * all the dynasty members' Wikipedia pages
 *
 * @see PersonPageData
 * @see DynastyTree
 *
 * @author Matteo Collica
 * @author Radu Ionut Barbalata
 * @author Sara Lazzaroni
 */
public class Person {
    private static HashMap<String, Person> storedPersonObjects = new HashMap<>();
    private static HashSet<String> visitedUrls = new HashSet<>();

    private PersonPageData personPageData;

    private Person mother;
    private Person father;
    private Person adoptiveFather;

    private ArrayList<Person> successors = new ArrayList<>();
    private ArrayList<Person> spouses;
    private ArrayList<Person> children;
    private ArrayList<Person> adoptedChildren;

    /**
     * Construct a Person object and set the corresponding graph node's style
     *
     * @param personPageData the PersonPageData this Person instance is related to
     * @param graph the Graph instance this Person instance's graph node is placed into
     */
    public Person(PersonPageData personPageData, Graph graph) {
        this.personPageData = personPageData;

        PersonNameUrl personNameUrl = personPageData.getPersonNameUrl();
        String wikipediaPageUrl = personNameUrl.getUrl();

        if (personPageData.isEmperorOrDictator()) {
            Node node = graph.getNode(wikipediaPageUrl);
            String customClass = "regnant";

            GraphStreamUtils.addClassToElement(node, customClass);
        }

        storedPersonObjects.put(wikipediaPageUrl, this);
        visitedUrls.add(wikipediaPageUrl);
    }

    /**
     * Construct a Person object and set the corresponding graph node's style,
     * then, if the PersonPageData to create a Person object from is an emperor
     * or dictator, scrape relatives' Wikipedia pages, add their nodes to the
     * graph and finally connect them to its node.
     *
     * @param webDriver the WebDriver needed to scrape relatives' data
     * @param personPageData the PersonPageData this Person instance is related to
     * @param dynastyTree the DynastyTree instance this Person instance belongs to
     */
    public Person(WebDriver webDriver, PersonPageData personPageData, DynastyTree dynastyTree) {
        this.personPageData = personPageData;

        PersonNameUrl personNameUrl = personPageData.getPersonNameUrl();
        String wikipediaPageUrl     = personNameUrl.getUrl();

        Graph graph = dynastyTree.getGraph();

        if(personPageData.isEmperorOrDictator()) {
            Node node = graph.getNode(wikipediaPageUrl);
            String customClass = "regnant";

            GraphStreamUtils.addClassToElement(node, customClass);
        }

        String dynastyPageUrl = dynastyTree.getDynasty().getWikipediaPageUrl();

        int offsetX = 0;
        int offsetY = 0;
        boolean added;

        visitedUrls.add(wikipediaPageUrl);
        storedPersonObjects.put(wikipediaPageUrl, this);

        /*
         * Only scrape relatives if the PersonPageData instance
         * this Person instance is related to is those of one
         * emperor or dictator, because the focus should be on the
         * emperors' kinship degrees and not on the relatives of
         * their relatives. It is also in order not to generate excessively
         * large genealogical trees, which would be difficult to consult.
         */

        if(personPageData.isEmperorOrDictator()) {
            offsetX += 1;

            /*
             * FATHER
             */

            PersonNameUrl fatherNameUrl = personPageData.getFatherNameUrl();

            if (fatherNameUrl != null) {
                String fatherPageUrl = fatherNameUrl.getUrl();

                if (fatherPageUrl != null) {
                    dynastyTree.addPersonToGraph(fatherNameUrl);

                    dynastyTree.addPersonConnection(personPageData, fatherNameUrl, DynastyTree.KinshipDegree.FATHER, offsetX, offsetY);

                    /*
                     * [1] The presence of a Wikipedia page URL
                     * in the visitedUrls HashSet means that its
                     * Wikipedia page has already been visited
                     * to create a Person object, in this case
                     * we retrieve its instance from the
                     * storedPersonObjects HashMap instead of
                     * creating it again.
                     */

                    if (!visitedUrls.contains(fatherPageUrl)) {
                        this.father = new Person(PersonPageData.getPersonPageData(webDriver, fatherNameUrl, dynastyPageUrl), graph);
                    } else {
                        this.father = storedPersonObjects.getOrDefault(fatherPageUrl, null);
                    }
                }
            }

            /*
             * ADOPTIVE FATHER
             */

            PersonNameUrl adoptiveFatherNameUrl = personPageData.getAdoptiveFatherNameUrl();

            if (adoptiveFatherNameUrl != null) {
                String adoptiveFatherPageUrl = adoptiveFatherNameUrl.getUrl();

                if (adoptiveFatherPageUrl != null) {
                    dynastyTree.addPersonToGraph(adoptiveFatherNameUrl);

                    dynastyTree.addPersonConnection(personPageData, adoptiveFatherNameUrl, DynastyTree.KinshipDegree.ADOPTIVEFATHER, offsetX, offsetY);

                    /*
                     * [1]
                     */

                    if (!visitedUrls.contains(adoptiveFatherPageUrl)) {
                        this.adoptiveFather = new Person(PersonPageData.getPersonPageData(webDriver, adoptiveFatherNameUrl, dynastyPageUrl), graph);
                    } else {
                        this.adoptiveFather = storedPersonObjects.getOrDefault(adoptiveFatherPageUrl, null);
                    }
                }
            }

            /*
             * MOTHER
             */

            PersonNameUrl motherNameUrl = personPageData.getMotherNameUrl();

            if (motherNameUrl != null) {
                String motherPageUrl = motherNameUrl.getUrl();

                if (motherPageUrl != null) {
                    if (!visitedUrls.contains(motherPageUrl)) {
                        dynastyTree.addPersonToGraph(motherNameUrl);

                        dynastyTree.addPersonConnection(personPageData, motherNameUrl, DynastyTree.KinshipDegree.MOTHER, offsetX, offsetY);

                        PersonPageData motherPersonPageData = PersonPageData.getPersonPageData(webDriver, motherNameUrl, dynastyPageUrl);

                        this.mother = new Person(motherPersonPageData, graph);
                    } else {
                        this.mother = storedPersonObjects.getOrDefault(motherPageUrl, null);
                    }
                }
            }

            /*
             * SPOUSES
             */

            offsetX = 0;

            this.spouses = new ArrayList<>();

            ArrayList<PersonNameUrl> spouses = personPageData.getSpouses();

            if (spouses != null) {
                for (PersonNameUrl spouseNameUrl : spouses) {
                    String spousePageUrl = spouseNameUrl.getUrl();

                    if (spousePageUrl != null) {
                        dynastyTree.addPersonToGraph(spouseNameUrl);

                        added = dynastyTree.addPersonConnection(personPageData, spouseNameUrl, DynastyTree.KinshipDegree.SPOUSE, offsetX, offsetY);

                        /*
                         * Each spouse will be one graph unit
                         * higher than the previously added one
                         */

                        if (added) {
                            offsetY += 1;
                        }

                        /*
                         * [1]
                         */

                        if (!visitedUrls.contains(spousePageUrl)) {
                            this.spouses.add(new Person(webDriver, PersonPageData.getPersonPageData(webDriver, spouseNameUrl, dynastyPageUrl), dynastyTree));
                        } else {
                            this.spouses.add(storedPersonObjects.getOrDefault(spousePageUrl, null));
                        }
                    }
                }
            }

            /*
             * SUCCESSORS
             */

            offsetY = 0;

            ArrayList<PersonNameUrl> successors = personPageData.getSuccessors();

            if (successors != null) {
                ArrayList<PersonPageData> validSuccessors = new ArrayList<>();

                for (PersonNameUrl successorNameUrl : successors) {
                    if (!visitedUrls.contains(successorNameUrl.getUrl())) {
                        PersonPageData successorPageData = PersonPageData.getPersonPageData(webDriver, successorNameUrl, dynastyPageUrl);

                        /*
                         * We consider a successor to be part of the current dynasty if
                         * his/her dynasty Wikipedia page URL points to the same dynasty
                         * of his/her predecessor (this Person instance) or the dynasty
                         * Wikipedia page URL points to a Wikipedia page that has
                         * still to be created (identified with a /w/ prefix).
                         *
                         * An example of ths case can be observed on the Wikipedia
                         * page of the Roman Emperor "Macrino":
                         *      https://it.wikipedia.org/wiki/Macrino
                         *
                         * Yet he is Caracalla's successor and the missing node
                         * with Eliogabalo, both emperors of the Severan Dynasty.
                         */

                        if (successorPageData.getPersonDynastyPageUrl() != null &&
                                (Dynasty.dynastyPageUrlEquals(dynastyPageUrl, successorPageData.getPersonDynastyPageUrl()) ||
                                        successorPageData.getPersonDynastyPageUrl().startsWith("https://it.wikipedia.org/w/"))) {

                            validSuccessors.add(successorPageData);

                            dynastyTree.addPersonToGraph(successorNameUrl);

                            added = dynastyTree.addPersonConnection(personPageData, successorNameUrl, DynastyTree.KinshipDegree.SUCCESSOR, offsetX, offsetY);

                            visitedUrls.add(successorNameUrl.getUrl());

                            if (added) {
                                offsetX++;
                            }
                        }
                    }
                }

                for (PersonPageData validSuccessorPageData : validSuccessors) {
                    this.successors.add(new Person(webDriver, validSuccessorPageData, dynastyTree));
                }
            }

            /*
             * CHILDREN
             */

            this.children = new ArrayList<>();

            ArrayList<PersonNameUrl> children = personPageData.getChildren();

            if (children != null) {
                for (PersonNameUrl childNameUrl : children) {
                    String childPageUrl = childNameUrl.getUrl();

                    if (childPageUrl != null) {
                        if (personPageData.isEmperorOrDictator()) {
                            dynastyTree.addPersonToGraph(childNameUrl);

                            added = dynastyTree.addPersonConnection(personPageData, childNameUrl, DynastyTree.KinshipDegree.CHILDREN, offsetX, offsetY);

                            /*
                             * [2] Each child will be shifted 1 graph unit to the right of
                             * the previously added child and 1 graph unit lower or higher
                             * (this is done to alternately place a child above and a child
                             * below to prevent their names from overlapping)
                             */

                            if (added) {
                                offsetX += 1;

                                if (offsetY == 0) {
                                    offsetY = 1;
                                } else {
                                    offsetY = 0;
                                }
                            }
                        }

                        if (!visitedUrls.contains(childPageUrl)) {
                            PersonPageData childrenPageData = PersonPageData.getPersonPageData(webDriver, childNameUrl, dynastyPageUrl);

                            /*
                             * [3] We consider a child to be part of the current dynasty if
                             * his/her dynasty Wikipedia page URL points to the same dynasty
                             * of his/her father (this Person instance) or her/his dynasty
                             * Wikipedia page URL isn't specified at all
                             */

                            if (childrenPageData.getPersonDynastyPageUrl() == null || Dynasty.dynastyPageUrlEquals(dynastyPageUrl, childrenPageData.getPersonDynastyPageUrl())) {
                                this.children.add(new Person(webDriver, childrenPageData, dynastyTree));
                            } else {
                                dynastyTree.removeNode(childPageUrl);
                            }
                        }
                    }
                }
            }

            /*
             * ADOPTED CHILDREN
             */

            this.adoptedChildren = new ArrayList<>();

            ArrayList<PersonNameUrl> adoptedChildren = personPageData.getAdoptedChildren();

            if (adoptedChildren != null) {
                for (PersonNameUrl adoptedChildNameUrl : adoptedChildren) {
                    String adoptedChildPageUrl = adoptedChildNameUrl.getUrl();

                    if (adoptedChildPageUrl != null) {
                        if (personPageData.isEmperorOrDictator()) {
                            dynastyTree.addPersonToGraph(adoptedChildNameUrl);

                            added = dynastyTree.addPersonConnection(personPageData, adoptedChildNameUrl, DynastyTree.KinshipDegree.ADOPTEDCHILDREN, offsetX, offsetY);

                            /*
                             * [2]
                             */

                            if (added) {
                                offsetX += 1;

                                if (offsetY == 0) {
                                    offsetY = 1;
                                } else {
                                    offsetY = 0;
                                }
                            }
                        }

                        if (!visitedUrls.contains(adoptedChildPageUrl)) {
                            PersonPageData adoptedChildPageData = PersonPageData.getPersonPageData(webDriver, adoptedChildNameUrl, dynastyPageUrl);

                            /*
                             * [3]
                             */

                            if (adoptedChildPageData.getPersonDynastyPageUrl() == null || Dynasty.dynastyPageUrlEquals(dynastyPageUrl, adoptedChildPageData.getPersonDynastyPageUrl())) {
                                this.adoptedChildren.add(new Person(webDriver, adoptedChildPageData, dynastyTree));
                            } else {
                                dynastyTree.removeNode(adoptedChildPageUrl);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Empty the visitedUrls HashSet by instantiating it again,
     * so that all previously visited urls will no longer result
     * as such. Useful before scraping a new dynasty for example.
     */
    public static void resetVisitedUrls() {
        visitedUrls = new HashSet<>();
    }

    /**
     * @return the storedPersonObjects HashMap, containing the already
     * constructed Person objects by their Wikipedia page URL
     */
    public static HashMap<String, Person> getStoredPersonObjects() {
        return storedPersonObjects;
    }

    /**
     * @return the PersonPageData object related to this Person instance
     */
    public PersonPageData getPersonPageData() {
        return personPageData;
    }

    /**
     * @return an ArrayList of successors' Person objects
     */
    public ArrayList<Person> getSuccessors() {
        return successors;
    }
}
