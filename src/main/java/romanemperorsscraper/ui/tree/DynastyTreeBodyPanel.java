package romanemperorsscraper.ui.tree;

import org.graphstream.graph.Graph;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import romanemperorsscraper.scraping.dynasties.Dynasty;
import romanemperorsscraper.scraping.dynasties.DynastySerializer;
import romanemperorsscraper.scraping.people.PersonPageData;
import romanemperorsscraper.scraping.people.PersonPageDataSerializer;
import romanemperorsscraper.ui.CustomTextArea;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Class extending JPanel to represent a panel
 * containing the genealogical tree graph of
 * a specific Dynasty (the one the DynastyTree
 * and, therefore, the DynastyTreeFrame is
 * related to) and the link-like button used
 * to open the graph legend.
 *
 * @see DynastyTreeFrame
 * @see romanemperorsscraper.scraping.dynasties.DynastyTree
 * @see Dynasty
 *
 * @author Matteo Collica
 * @author Sara Lazzaroni
 * @author Ivan Dalla Ragione
 */
public class DynastyTreeBodyPanel extends JPanel {
    private DynastyTreeFrame dynastyTreeFrame;

    /**
     * Create a panel containing the graph of the dynasty's genealogical tree
     *
     * @param dynastyTreeFrame the DynastyTreeFrame instance the panel is placed in
     * @param graph the Graph to be contained in this DynastyTreeBodyPanel instance
     */
    public DynastyTreeBodyPanel(DynastyTreeFrame dynastyTreeFrame, Graph graph) {
        super();

        this.dynastyTreeFrame = dynastyTreeFrame;

        /*
         * GRAPH VIEW COMPONENT INITIALIZATION
         */

        /*
         * Initialize a SwingViewer needed to later draw the graph inside
         * an already created Swing component (the DynastyTreeBodyPanel)
         */

        SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.disableAutoLayout();

        final int GRAPH_VIEW_WIDTH  = 800;
        final int GRAPH_VIEW_HEIGHT = 735;

        DefaultView view = (DefaultView) viewer.addDefaultView(false);
        view.setPreferredSize(new Dimension(GRAPH_VIEW_WIDTH, GRAPH_VIEW_HEIGHT));

        /* Set the panel options and add the graph view component to the panel */

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(GRAPH_VIEW_WIDTH, GRAPH_VIEW_HEIGHT));
        setBackground(Color.WHITE);

        add(view, BorderLayout.CENTER);

        /* If the DynastyTreeFrame isn't the Graph Legend Dynasty's one,
           create a label which can be clicked to show the graph legend frame */

        if(!dynastyTreeFrame.getDynasty().getWikipediaPageUrl().startsWith("fakeurl://")) {
            CustomTextArea legendTextArea = new CustomTextArea("Legend", Color.BLACK, "RomanFont7.ttf", Font.BOLD, 15f);

            legendTextArea.setBackground(Color.WHITE);
            legendTextArea.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            legendTextArea.setPreferredSize(new Dimension(100, 20));

            /*
             * Set a MouseListener so that when the mouse passes on the label
             * the text changes its color and when the mouse clicks on it
             * the graph legend frame is shown
             */

            legendTextArea.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);

                    legendTextArea.setForeground(new Color(99, 156, 188));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);

                    legendTextArea.setForeground(Color.BLACK);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);

                    JSONParser parser = new JSONParser();
                    InputStreamReader reader;

                    try {
                        Dynasty graphLegendDynasty = Dynasty.getStoredDynastyByUrl("fakeurl://graphLegendDynasty");

                        if (graphLegendDynasty == null) {
                            reader = new InputStreamReader(DynastyTreeFrame.class.getResourceAsStream("/resources/graphstream/graphLegendDynasty.json"));

                            JSONObject jsonFileObject = (JSONObject) parser.parse(reader);

                            /* Deserialize the dynasty people */

                            JSONObject serializedPeople = (JSONObject) jsonFileObject.get("people");
                            HashMap<String, PersonPageData> unserializedPeople = PersonPageDataSerializer.deserializePersonPageDataHashMap(serializedPeople);
                            PersonPageData.getUrlPersonPageDataMatches().putAll(unserializedPeople);

                            /* Deserialize the dynasties */

                            JSONObject serializedDynasties = (JSONObject) jsonFileObject.get("dynasties");
                            HashMap<String, Dynasty> deserializedDynasties = DynastySerializer.deserializeDynastiesHashMap(serializedDynasties);
                            Dynasty.getStoredDynasties().putAll(deserializedDynasties);

                            /* Create the DynastyTreeFrame object of the graph legend dynasty */

                            graphLegendDynasty = deserializedDynasties.get("fakeurl://graphLegendDynasty");
                            graphLegendDynasty.createDynastyTree(null);

                            DynastyTreeFrame legendFrameInstance = new DynastyTreeFrame(graphLegendDynasty.getDynastyTree().getGraph(), graphLegendDynasty);
                            DynastyTreeFrame.setLegendFrameInstance(legendFrameInstance);

                        } else {
                            DynastyTreeFrame dynastyTreeFrame = DynastyTreeFrame.createOrGetLegendFrameInstance(graphLegendDynasty.getDynastyTree().getGraph(), graphLegendDynasty);

                            /* If the legend graph is already opened, bring it to the foreground */

                            java.awt.EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    dynastyTreeFrame.toFront();
                                }
                            });
                        }
                    } catch (Exception ex) {}
                }
            });

            add(legendTextArea, BorderLayout.SOUTH);
        }
    }
}