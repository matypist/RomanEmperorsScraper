package romanemperorsscraper.ui.choice;

import romanemperorsscraper.ui.ButtonProperties;
import romanemperorsscraper.ui.tree.DynastyTreeFrame;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class representing the buttons placed on DynastyChoiceFrame
 * (inside the DynastyChoiceBodyPanel) which are used to choose
 * the Dynasty whose genealogical tree graph the user wants to
 * be drawn.
 *
 * @see DynastyChoiceFrame
 * @see DynastyChoiceBodyPanel
 * @see romanemperorsscraper.scraping.dynasties.Dynasty
 *
 * @author Sara Lazzaroni
 * @author Ivan Dalla Ragione
 */
public class DynastyChoiceButton extends ButtonProperties implements ActionListener {
    private final DynastyChoiceBodyPanel dynastyChoiceBodyPanel;

    private final String dynastyPageUrl;

    private final String dynastyName;

    private static HashMap<String, DynastyChoiceButton> buttons = new HashMap<>();

    /**
     * Construct a DynastyChoiceButton by setting its field values
     *
     * @param dynastyChoiceBodyPanel the DynastyChoiceBodyPanel the button is placed in
     * @param dynastyPageUrl the Wikipedia page URL of the Dynasty related to this DynastyChoiceButton
     * @param buttonIconPath the button's icon path
     * @param buttonRolloverIconPath the button's rollover icon path
     * @param buttonDisabledIconPath the button's disabled icon path
     */
    public DynastyChoiceButton(DynastyChoiceBodyPanel dynastyChoiceBodyPanel, String dynastyPageUrl, String dynastyName, String buttonIconPath, String buttonRolloverIconPath, String buttonDisabledIconPath) {
        /*
         * Recall to the super class constructor method to eliminate borders, set the size, set the icon
         * shown when the mouse isn't on the button and the one shown when the mouse is over it
         */

        super(buttonIconPath, buttonRolloverIconPath);

        this.dynastyChoiceBodyPanel = dynastyChoiceBodyPanel;

        this.dynastyPageUrl = dynastyPageUrl;

        this.dynastyName = dynastyName;

        this.addActionListener(this);

        /* Associate every button to the relative dynasty */

        buttons.put(dynastyPageUrl, this);

        /* Set the button icon during the scraping and the viewing
        of the graph, during which the button can't be clicked */

        ImageIcon rolloverIcon = null;
        
        try {
            rolloverIcon = new ImageIcon(ImageIO.read(
                ButtonProperties.class.getResourceAsStream(buttonRolloverIconPath)
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        setDisabledIcon(rolloverIcon);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        /* When the button is clicked, the scraping starts
           and the button gets disabled until its done */

        dynastyChoiceBodyPanel.disableButton(dynastyPageUrl, this);

        DynastyTreeFrame.showDynastyTreeFrameByUrl(this.dynastyPageUrl, this.dynastyName);
    }

    /**
     * Get the DynastyChoiceButton relative to a specific dynasty
     *
     * @param dynastyPageUrl the dynasty's Wikipedia page URL
     * @return the DynastyChoiceButton object corresponding to the dynasty
     */
    public static DynastyChoiceButton getButton(String dynastyPageUrl) {
        return buttons.get(dynastyPageUrl);
    }
}
