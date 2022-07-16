package romanemperorsscraper.scraping.dynasties;

import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class providing methods
 * related to GraphStream
 *
 * @see DynastyTree
 *
 * @author Matteo Collica
 */
public class GraphStreamUtils {
    /*
     * PREVENT INSTANTIATION WITH A PRIVATE CONSTRUCTOR
     */

    private GraphStreamUtils() {
        throw new java.lang.UnsupportedOperationException(
                "As a utility class, this class must not be instantiated"
        );
    }

    /*
     * CLASS UTILITY METHODS
     */

    /**
     * Given a GraphStream Element (a node or an edge) and a
     * style class's name, add it to the element's 'ui.class'
     * attribute (or set it as the only class if empty)
     *
     * @param element a GraphStream element (a node or an edge)
     * @param customClass the class to be added to the element's 'ui.class' attribute
     */
    public static void addClassToElement(Element element, String customClass) {
        if(element.getAttribute("ui.class") == null) {
            element.setAttribute("ui.class", customClass);
        } else {
            String previousClasses = (String) element.getAttribute("ui.class");

            element.setAttribute("ui.class", previousClasses + ", " + customClass);
        }
    }

    /**
     * Create a node and add it to an existing GraphStream's graph
     *
     * @param id the node's ID
     * @param label the node's label text
     * @param graph the graph the node as to be added to
     *
     * @return the created and styled node
     */
    public static Node addNodeToGraph(String id, String label, Graph graph) {
        Node node = graph.addNode(id);

        node.setAttribute("x", 0);
        node.setAttribute("y", 0);
        node.setAttribute("ui.label", label);
        node.setAttribute("ui.weight", 1);

        return node;
    }

    /**
     * Given two GraphStream nodes, check whether there is a connection between them
     *
     * @param firstNode the first node
     * @param secondNode the second node
     *
     * @return whether there is a connection between the two nodes
     */
    public static boolean areConnected(Node firstNode, Node secondNode) {
        Stream<Node> neighbourNodesStream = firstNode.neighborNodes();

        List<Node> neighbourNodes = neighbourNodesStream.collect(Collectors.toList());

        for(Node neighbourNode : neighbourNodes) {
            if(neighbourNode == secondNode) {
                return true;
            }
        }

        return false;
    }
}
