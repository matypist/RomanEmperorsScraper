package romanemperorsscraper.ui.tree;

import org.graphstream.graph.Graph;
import org.openqa.selenium.WebDriver;
import romanemperorsscraper.run.NoSupportedBrowsersException;
import romanemperorsscraper.run.OsUtils;
import romanemperorsscraper.run.RomanEmperorsScraper;
import romanemperorsscraper.scraping.dynasties.Dynasty;
import romanemperorsscraper.ui.choice.DynastyChoiceButton;
import romanemperorsscraper.ui.choice.DynastyChoiceFrame;
import romanemperorsscraper.ui.people.PeopleListOpenButton;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

/**
 * Class used to create, represent and show a
 * frame containing the genealogical tree graph
 * of a specific Dynasty (the one the DynastyTree
 * of the DynastyTreeFrame itself is related to)
 *
 * @see romanemperorsscraper.scraping.dynasties.DynastyTree
 * @see Dynasty
 *
 * @author Matteo Collica
 * @author Sara Lazzaroni
 */
public class DynastyTreeFrame extends JFrame {
    private Dynasty dynasty;

    private DynastyTreeTitlePanel titlePanel;
    private DynastyTreeBodyPanel bodyPanel;

    private static DynastyTreeFrame legendFrameInstance;

    /**
     * Create the DynastyTreeFrame of the graph legend
     * or return it if it was already created
     *
     * @param graph the Dynasty's Graph instance
     * @param graphLegendDynasty the graph legend dynasty's Dynasty object
     *
     * @return the graph legend's DynastyTreeFrame object
     */
    public static DynastyTreeFrame createOrGetLegendFrameInstance(Graph graph, Dynasty graphLegendDynasty) {
        if(legendFrameInstance == null) {
            legendFrameInstance = new DynastyTreeFrame(graph, graphLegendDynasty);
        }

        return legendFrameInstance;
    }

    /**
     * @return the graph legend's DynastyTreeFrame
     */
    public static DynastyTreeFrame getLegendFrameInstance() {
        return legendFrameInstance;
    }

    /**
     * @param dynastyTreeFrame the graph legend's DynastyTreeFrame
     *                         to be stored for later retrieval
     */
    public static void setLegendFrameInstance(DynastyTreeFrame dynastyTreeFrame) {
        legendFrameInstance = dynastyTreeFrame;
    }

    /**
     * Given a dynasty Graph and Dynasty object,
     * create a frame containing the genealogical
     * tree graph
     *
     * @param graph the dynasty's Graph object
     * @param dynasty the dynasty's Dynasty object
     */
    public DynastyTreeFrame(Graph graph, Dynasty dynasty) {
        super("Roman Emperors Scraper - " + dynasty.getName());

        this.dynasty = dynasty;

        /*
         * FRAME OPTIONS
         */

        setIconImage(RomanEmperorsScraper.getAppIconImage());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setBackground(Color.WHITE);
        setBounds(100, 100, 1174, 658);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        /*
         * When the graph frame is closed, we remove it from the list of
         * already opened DynastyTreeFrame(s) and enable the corresponding
         * DynastyChoiceButton on the DynastyChoiceFrame so that a new
         * instance can therefore be opened.
         */

        final DynastyTreeFrame thisDynastyTreeFrame = this;

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if(legendFrameInstance == null || !legendFrameInstance.equals(thisDynastyTreeFrame)) {
                        String dynastyPageUrl = dynasty.getWikipediaPageUrl();

                        DynastyChoiceFrame.getInstance().getBodyPanel().enableButton(dynastyPageUrl);

                        DynastyChoiceButton.getButton(dynastyPageUrl).setIcon(new ImageIcon(ImageIO.read(DynastyTreeFrame.class.getResourceAsStream("/resources/images/icons/choice/DynastyChoiceButton_scraped.png"))));
                        DynastyChoiceButton.getButton(dynastyPageUrl).setRolloverIcon(new ImageIcon(ImageIO.read(DynastyTreeFrame.class.getResourceAsStream("/resources/images/icons/choice/DynastyChoiceButton_scraped_roll.png"))));
                    } else {
                        legendFrameInstance = null;
                    }
                } catch (IOException ignored) {}

                PeopleListOpenButton.removeFromOpenedList(getDynasty().getWikipediaPageUrl());
            }
        };
        this.addWindowListener(exitListener);

        /*
         * Create DynastyTreeTitlePanel, the DynastyTreeBodyPanel
         * and add them to the DynastyTreeFrame
         */

        titlePanel = new DynastyTreeTitlePanel(this);
        this.getContentPane().add(titlePanel, BorderLayout.NORTH);

        bodyPanel = new DynastyTreeBodyPanel(this, graph);
        this.getContentPane().add(bodyPanel, BorderLayout.CENTER);

        /* Once the frame has been created we finally set it to be visible */

        setVisible(true);
    }

    /**
     * Given a dynasty's Wikipedia page URL, get its corresponding
     * Dynasty object and then get (or construct) and show its
     * DynastyTreeFrame to the user
     *
     * @param dynastyPageUrl the dynasty's Wikipedia page URL
     */
    public static void showDynastyTreeFrameByUrl(String dynastyPageUrl, String dynastyName) {
        Dynasty dynasty = Dynasty.getStoredDynastyByUrl(dynastyPageUrl);

        if(dynasty == null) {
            /*
             * Start a new WebDriver session (it only fails
             * if there are no supported browsers installed)
             */

            WebDriver webDriver;

            try {
                webDriver = OsUtils.getWebDriver();
            } catch (NoSupportedBrowsersException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "There are no supported browsers" +
                                " (Firefox / Chrome / Safari / Edge /" +
                                " Internet Explorer) in your Operating" +
                                " System, please try installing one first" +
                                " or importing data instead of scraping" +
                                " them in place",
                        "No Supported Browsers found",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            dynasty = new Dynasty(dynastyPageUrl, dynastyName, webDriver, true);

            /*
             * Close the WebDriver session because it is no longer required
             */

            webDriver.close();
        }

        if(dynasty.getDynastyTree() == null) {
            dynasty.createDynastyTree(null);
        }

        new DynastyTreeFrame(dynasty.getDynastyTree().getGraph(), dynasty);
    }

    /**
     * @return the Dynasty object corresponding to this DynastyTreeFrame instance
     */
    public Dynasty getDynasty() {
        return dynasty;
    }
}
