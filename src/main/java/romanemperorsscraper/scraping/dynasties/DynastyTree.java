package romanemperorsscraper.scraping.dynasties;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.openqa.selenium.WebDriver;
import romanemperorsscraper.run.RomanEmperorsScraper;
import romanemperorsscraper.scraping.people.Person;
import romanemperorsscraper.scraping.people.PersonNameUrl;
import romanemperorsscraper.scraping.people.PersonPageData;

import java.util.HashSet;

/**
 * Class used to create and represent
 * a Dynasty's genealogical tree graph.
 *
 * @see Dynasty
 * @see romanemperorsscraper.ui.tree.DynastyTreeFrame
 *
 * @author Matteo Collica
 * @author Radu Ionut Barbalata
 * @author Sara Lazzaroni
 */
public class DynastyTree {
    private final Graph graph;

    private HashSet<String> graphNodeIds;

    private final Dynasty dynasty;

    private final Person founderNode;

    public enum KinshipDegree {
        MOTHER,
        FATHER,
        ADOPTIVEFATHER,
        SPOUSE,
        CHILDREN,
        ADOPTEDCHILDREN,
        SUCCESSOR
    }

    /**
     * Construct the graph of the dynasty starting from the Founder
     *
     * @param webDriver the Web Driver instance to be used to scrape data
     * @param founderPageData PersonPageData object which represent the founder of the dynasty
     * @param dynasty Dynasty object contain the principal information about the dynasty than we create
     */
    public DynastyTree(WebDriver webDriver, PersonPageData founderPageData, Dynasty dynasty) {
        this.dynasty = dynasty;

        this.graphNodeIds = new HashSet<>();

        /*
         * We use a SingleGraph because a maximum of one
         * edge between two nodes it's enough
         */

        this.graph = new SingleGraph(dynasty.getWikipediaPageUrl());

        /*
         * Here we set the location of the CSS stylesheet
         * which will be used to style nodes and edges
         * in our graph
         */

        graph.setAttribute("ui.stylesheet", "url("+ DynastyTree.class.getResource("/resources/graphstream/gs-stylesheet.css") + ")");

        addPersonToGraph(founderPageData.getPersonNameUrl());
        this.founderNode = new Person(webDriver, founderPageData, this);

        /*
         * VisitedUrls is an HashMap we use to keep track of already visited URLs and not visit them more than once
         * during the scraping, but since some people of the Dynasty we are actually scraping could've been previously
         * visited too (while exploring the successors) we empty it to be able to visit them again
         */

        Person.resetVisitedUrls();

        if(RomanEmperorsScraper.isDebugEnabled()) {
            for(String nodeId : graphNodeIds) {
                Node node = graph.getNode(nodeId);
                System.out.println(nodeId + " : " + node.getAttribute("x") + " : " + node.getAttribute("y"));
            }
        }
    }

    /**
     * Crate and add the Node of the person to the Graph
     *
     * @param personNameUrl PersonNameUrl object whose url will represent the NodeID
     */
    public void addPersonToGraph(PersonNameUrl personNameUrl) {
        String label = personNameUrl.getFullName();
        String id    = personNameUrl.getUrl();

        /*
         * Here we add the node but not the connection
         * with other nodes
         */
        if (!graphNodeIds.contains(id)) {
            GraphStreamUtils.addNodeToGraph(id, label, graph);

            graphNodeIds.add(id);
        }
    }

    /**
     * This method creates the connection between two nodes, represented by
     * a PersonPageData (the first) and a PersonNameUrl (the second one)
     * object, taking in account the relationship between them
     *
     * @param firstPersonPageData PersonPageData object of the person that defines the relationship
     * @param secondPersonNameUrl PersonPageData object of the second person
     * @param kinshipDegree KinshipDegree object which represent the relationship between the firstPersonPageData and
     *                      the secondPersonNameUrl
     * @param offsetX the horizontal distance between the firstNode and the second one
     * @param offsetY the vertical distance between the firstNode and the second one
     * @return true if the second node isn't in the graph, false otherwise
     */
    public boolean addPersonConnection(PersonPageData firstPersonPageData, PersonNameUrl secondPersonNameUrl, KinshipDegree kinshipDegree, int offsetX, int offsetY) {
        PersonNameUrl firstPersonNameUrl = firstPersonPageData.getPersonNameUrl();

        /*
         * We can only add a connection between two nodes if they both have a URL to be used as an ID
         */
        if(firstPersonNameUrl.getUrl() != null && secondPersonNameUrl.getUrl() != null) {
            Node firstNode  = graph.getNode(firstPersonNameUrl.getUrl());
            Node secondNode = graph.getNode(secondPersonNameUrl.getUrl());

            /*
             * If the second person is already connected to the first one, we don't add a new connection
             */

            if(GraphStreamUtils.areConnected(firstNode, secondNode)) {
                return false;
            }

            int firstNodeX = (int) firstNode.getAttribute("x");
            int firstNodeY = (int) firstNode.getAttribute("y");

            int secondNodeX = (int) firstNode.getAttribute("x");
            int secondNodeY = (int) firstNode.getAttribute("y");

            String customStyle = "";
            String customClass = "";

            /*
             * Since we place the nodes on x = 0 and y = 0 on creation we then modify their position
             * based on the relationship which occurs between them
             */
            switch(kinshipDegree) {
                case MOTHER:
                    if(RomanEmperorsScraper.isDebugEnabled()) {
                        System.out.println(secondNode.getAttribute("ui.label") + " -(madre di)-> " + firstNode.getAttribute("ui.label"));
                    }

                    secondNode.setAttribute("x", firstNodeX + (offsetX * 2));

                    /*
                     * We place the son node under the mother's node by one graph unit
                     */
                    if(firstNode.getAttribute("x").equals(0) && firstNode.getAttribute("y").equals(0)) {
                        firstNode.setAttribute("y", secondNodeY - 1);

                        if(RomanEmperorsScraper.isDebugEnabled()) {
                            System.out.println(firstNode.getAttribute("ui.label") + " -> x : " + firstNode.getAttribute("x") + " y : " + firstNode.getAttribute("y"));
                        }
                    } else {
                        secondNode.setAttribute("y", firstNodeY + 1);
                    }

                    if(RomanEmperorsScraper.isDebugEnabled()) {
                        System.out.println(secondNode.getAttribute("ui.label") + " -> x : " + secondNode.getAttribute("x") + " y : " + secondNode.getAttribute("y"));
                    }

                    customClass = "mother";

                    break;

                case FATHER:
                    if(RomanEmperorsScraper.isDebugEnabled()) {
                        System.out.println(secondNode.getAttribute("ui.label") + " -(padre di)-> " + firstNode.getAttribute("ui.label"));
                    }

                    if (secondNode.getAttribute("x").equals(0) && secondNode.getAttribute("y").equals(0)) {
                        /*
                         * If the first person has a mother, we place the father node
                         * on the right side of the mother node
                         */
                        if (firstPersonPageData.getMotherNameUrl() != null && firstPersonPageData.getMotherNameUrl().getUrl() != null) {
                            secondNode.setAttribute("x", firstNodeX + 2 + (offsetX * 2));
                        } else {
                            secondNode.setAttribute("x", firstNodeX + (offsetX * 2));
                        }

                        /*
                         * We place the son node under the father's node by one graph unit
                         */
                        if (firstNode.getAttribute("x").equals(0) && firstNode.getAttribute("y").equals(0)) {
                            firstNode.setAttribute("y", secondNodeY - 1);

                            if(RomanEmperorsScraper.isDebugEnabled()) {
                                System.out.println(firstNode.getAttribute("ui.label") + " -> x : " + firstNode.getAttribute("x") + " y : " + firstNode.getAttribute("y"));
                            }
                        } else {
                            secondNode.setAttribute("y", firstNodeY + 1);
                        }

                        if(RomanEmperorsScraper.isDebugEnabled()) {
                            System.out.println(secondNode.getAttribute("ui.label") + " -> x : " + secondNode.getAttribute("x") + " y : " + secondNode.getAttribute("y"));
                        }
                    }

                    customClass = "father";

                    break;

                case ADOPTIVEFATHER:
                    if(RomanEmperorsScraper.isDebugEnabled()) {
                        System.out.println(secondNode.getAttribute("ui.label") + " -(padre adottivo di)-> " + firstNode.getAttribute("ui.label"));
                    }

                    if(secondNode.getAttribute("x").equals(0) && secondNode.getAttribute("y").equals(0)) {
                        /*
                         * The distance between parents is determined by the x-coordinate, the first in order from
                         * left to right will be the father or the mother, the second can also be the adoptive father,
                         * the third will be the adoptive father if the first node has both a father and a mother
                         */
                        if (firstPersonPageData.getFatherNameUrl() != null && firstPersonPageData.getMotherNameUrl() != null && firstPersonPageData.getMotherNameUrl().getUrl() != null) {
                            secondNode.setAttribute("x", firstNodeX + 5 + (offsetX * 2));
                        } else {
                            secondNode.setAttribute("x", firstNodeX + 2 + (offsetX * 2));
                        }

                        /*
                         * We place the son node under the adoptive father's node by one graph unit
                         */
                        if(firstNode.getAttribute("x").equals(0) && firstNode.getAttribute("y").equals(0)) {
                            firstNode.setAttribute("y", secondNodeY - 1);

                            if(RomanEmperorsScraper.isDebugEnabled()) {
                                System.out.println(firstNode.getAttribute("ui.label") + " -> x : " + firstNode.getAttribute("x") + " y : " + firstNode.getAttribute("y"));
                            }
                        } else {
                            secondNode.setAttribute("y", firstNodeY + 1);
                        }

                        if(RomanEmperorsScraper.isDebugEnabled()) {
                            System.out.println(secondNode.getAttribute("ui.label") + " -> x : " + secondNode.getAttribute("x") + " y : " + secondNode.getAttribute("y"));
                        }
                    }

                    customClass = "adoptivefather";

                    break;

                case SUCCESSOR:
                    if(RomanEmperorsScraper.isDebugEnabled()) {
                        System.out.println(secondNode.getAttribute("ui.label") + " -(successore di)-> " + firstNode.getAttribute("ui.label"));
                    }

                    /*
                     * We place the successor node under the predecessor node by four graph unit
                     */
                    secondNode.setAttribute("x", firstNodeX + (offsetX * 5));
                    secondNode.setAttribute("y", firstNodeY - 4);

                    if(RomanEmperorsScraper.isDebugEnabled()) {
                        System.out.println(secondNode.getAttribute("ui.label") + " -> x : " + secondNode.getAttribute("x") + " y : " + secondNode.getAttribute("y"));
                    }

                    customClass = "successor";

                    break;

                case SPOUSE:
                    if(RomanEmperorsScraper.isDebugEnabled()) {
                        System.out.println(secondNode.getAttribute("ui.label") + " -(coniuge di)-> " + firstNode.getAttribute("ui.label"));
                    }

                    /*
                     * Each spouse is placed offsetY higher than the previous one
                     * and shifted 2 graph units to the left of the first node
                     */
                    secondNode.setAttribute("x", firstNodeX - 2);
                    secondNode.setAttribute("y", firstNodeY + offsetY);

                    if(RomanEmperorsScraper.isDebugEnabled()) {
                        System.out.println(secondNode.getAttribute("ui.label") + " -> x : " + secondNode.getAttribute("x") + " y : " + secondNode.getAttribute("y"));
                    }

                    customClass = "spouse";

                    break;

                case CHILDREN:
                    if(RomanEmperorsScraper.isDebugEnabled()) {
                        System.out.println(secondNode.getAttribute("ui.label") + " -(figlio/a di)-> " + firstNode.getAttribute("ui.label"));
                    }

                    /*
                     * Each child is placed at least 1 graph unit lower than the father
                     * and shifted twice offsetX graph units to the right of the first node
                     */
                    secondNode.setAttribute("x", firstNodeX + (offsetX * 2));
                    secondNode.setAttribute("y", firstNodeY - (offsetY + 1));

                    if(RomanEmperorsScraper.isDebugEnabled()) {
                        System.out.println(secondNode.getAttribute("ui.label") + " -> x : " + secondNode.getAttribute("x") + " y : " + secondNode.getAttribute("y"));
                    }

                    customClass = "children";

                    break;

                case ADOPTEDCHILDREN:
                    if(RomanEmperorsScraper.isDebugEnabled()) {
                        System.out.println(secondNode.getAttribute("ui.label") + " -(figlio/a adottivo/a di)-> " + firstNode.getAttribute("ui.label"));
                    }

                    /*
                     * Each adoptive child is placed offsetY+1 lower than the previous one
                     * and shifted twice offsetX graph units to the right of the first node
                     */
                    secondNode.setAttribute("x", firstNodeX + (offsetX * 2));
                    secondNode.setAttribute("y", firstNodeY - (offsetY + 1));

                    if(RomanEmperorsScraper.isDebugEnabled()) {
                        System.out.println(secondNode.getAttribute("ui.label") + " -> x : " + secondNode.getAttribute("x") + " y : " + secondNode.getAttribute("y"));
                    }

                    customClass = "adoptedchildren";

                    break;
            }

            /*
             * Create the edge between the two nodes and set its style properties
             */
            Edge edge = graph.addEdge(firstPersonNameUrl.getUrl() + "-" + secondPersonNameUrl.getUrl(), firstPersonNameUrl.getUrl(), secondPersonNameUrl.getUrl());

            if(!customStyle.equals("")) {
                edge.setAttribute("ui.style", customStyle);
            } else if(!customClass.equals("")) {
                GraphStreamUtils.addClassToElement(edge, customClass);
            }

            return true;
        }

        return false;
    }

    /**
     * Remove the specific node
     *
     * @param nodeId the ID of the node to be removed
     */
    public void removeNode(String nodeId) {
        if(graphNodeIds.contains(nodeId)) {
            graph.removeNode(nodeId);
            graphNodeIds.remove(nodeId);
        }
    }

    /**
     * @return the Dynasty object related to this DynastyTree instance
     */
    public Dynasty getDynasty() {
        return dynasty;
    }

    /**
     * @return the Graph object related to this DynastyTree instance
     */
    public Graph getGraph() {
        return graph;
    }
}
