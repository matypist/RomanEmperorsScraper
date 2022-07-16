package romanemperorsscraper.ui.choice;

import romanemperorsscraper.run.RomanEmperorsScraper;
import romanemperorsscraper.ui.FontUtils;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

/**
 * JFrame organized in two panels:
 * - DynastyChoiceTitlePanel (containing the buttons use to import and export data)
 * - DynastyChoiceBodyPanel (containing the buttons used to start dynasties' scraping
 *   or access the genealogical tree graph of the already scraped ones)
 *
 * @see DynastyChoiceBodyPanel
 * @see DynastyChoiceTitlePanel
 * @see romanemperorsscraper.scraping.dynasties.Dynasty
 *
 * @author Sara Lazzaroni
 * @author Ivan Dalla Ragione
 */
public class DynastyChoiceFrame extends JFrame {
    private static DynastyChoiceFrame instance = null;

    private final DynastyChoiceTitlePanel TitlePanel;
    private final DynastyChoiceBodyPanel BodyPanel;

    /*
     * SINGLETON CONSTRUCTOR
     */

    /**
     * Construct an instance of DynastyChoiceFrame or return an already constructed one
     *
     * @return the instance of the DynastyChoiceFrame object
     */
    public static DynastyChoiceFrame getInstance() {
        if (instance == null) {
            instance = new DynastyChoiceFrame();
        }

        return instance;
    }

    /**
     * Construct the DynastyChoiceFrame with DynastyChoiceTitlePanel,
     * DynastyChoiceBodyPanel and a JLabel containing credits
     */
    private DynastyChoiceFrame() {
        super("Roman Emperors Scraper - Dynasty Choice");

        /*
         * MISC OPTIONS
         */

        setIconImage(RomanEmperorsScraper.getAppIconImage());
        setExtendedState(MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
         * COMPONENTS
         */

        /* Load the background image from a local file */

        try {
            final Image backgroundImage = javax.imageio.ImageIO.read(
                DynastyChoiceFrame.class.getResource(
                        "/resources/images/DynastyChoiceFrame_bgImage.jpeg")
            );

            setContentPane(
                new JPanel(new BorderLayout()) {
                    @Override
                    public void paintComponent(Graphics g) {
                        g.drawImage(backgroundImage.getScaledInstance(
                                this.getWidth(),
                                this.getHeight(),
                                Image.SCALE_DEFAULT
                        ), 0, 0, null);
                    }
                }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /* Create Title and Body panels and add them to the frame content */

        TitlePanel = new DynastyChoiceTitlePanel();
        BodyPanel  = new DynastyChoiceBodyPanel();

        /* Create a JLabel containing the developers' full name */

        JLabel credits = new JLabel("Radu Ionut Barbalata, Matteo Collica, Ivan Dalla Ragione, Sara Lazzaroni");
        credits.setFont(FontUtils.getFont("RomanFont7.ttf", Font.PLAIN, 20f));
        credits.setOpaque(false);
        credits.setHorizontalAlignment(SwingConstants.RIGHT);
        credits.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 10));

        /* Add the created elements to the content pane */

        getContentPane().add(TitlePanel, BorderLayout.NORTH);
        getContentPane().add(BodyPanel, BorderLayout.CENTER);
        getContentPane().add(credits, BorderLayout.SOUTH);

        /* Once the frame has been created we finally set it to be visible */

        setVisible(true);
    }

    /**
     * @return the DynastyChoiceBodyPanel instance related to the DynastyChoiceFrame
     */
    public DynastyChoiceBodyPanel getBodyPanel() {
        return BodyPanel;
    }
}
