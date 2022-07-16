package romanemperorsscraper.ui.choice;

import org.openqa.selenium.WebDriver;
import romanemperorsscraper.run.NoSupportedBrowsersException;
import romanemperorsscraper.run.OsUtils;
import romanemperorsscraper.scraping.dynasties.Dynasty;
import romanemperorsscraper.ui.CustomTextArea;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * Class extending JPanel to represent a panel
 * containing DynastyChoiceButton(s) placed
 * inside the DynastyChoiceFrame.
 *
 * @see DynastyChoiceFrame
 * @see DynastyChoiceButton
 * @see DynastyChoiceTitlePanel
 *
 * @author Sara Lazzaroni
 * @author Ivan Dalla Ragione
 * @author Matteo Collica
 */
public class DynastyChoiceBodyPanel extends JPanel {
    private HashMap<String, JButton> disabledButtons = new HashMap<>();

    /**
     * Create a panel containing a button used
     * to start the scraping for each dynasty
     */
    public DynastyChoiceBodyPanel() {
        super();

        /*
         * OPTIONS
         */

        setLayout(new GridLayout(2, 6));
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        setOpaque(false);

        /* Get a WebDriver from a supported browser */

        WebDriver webDriver;
        try {
            webDriver = OsUtils.getWebDriver();
        } catch (NoSupportedBrowsersException e) {
            e.printStackTrace();

            return;
        }

        /* Obtain an HashMap containing:
         * - The dynasty's Wikipedia page URL (as key)
         * - The dynasty name (as value)
         *
         * e.g. key:   https://it.wikipedia.org/wiki/Dinastia_giulio-claudia
         *      value: Dinastia giulio-claudia
         */

        HashMap<String, String> dynasties = Dynasty.getDynasties(webDriver);

        webDriver.quit();

        /*
         * COMPONENTS
         */

        /* Prepare the parameter values for the DynastyChoiceButton(s) */

        String buttonIconPath = "/resources/images/icons/choice/DynastyChoiceButton.png";
        String buttonRolloverIconPath = "/resources/images/icons/choice/DynastyChoiceButton_roll.png";
        String buttonDisabledIconPath = "/resources/images/icons/choice/DynastyChoiceButton_roll.png";

        /*
         * Prepare the parameter values for the CustomTextArea(s)
         */

        String fontFileName = "RomanFont7.ttf";
        int fontStyle = Font.BOLD;
        int fontSize;

        /* The textArea font size is determined dynamically (depending on the screen size) */

        fontSize = (int) (GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth() * 0.0155125);

        int count = 0;

        DynastyChoiceButton button;
        CustomTextArea customTextArea;
        Container buttonLabelContainer;

        for(Map.Entry<String, String> set : dynasties.entrySet()) {
            String dynastyPageUrl = set.getKey();
            String dynastyName    = set.getValue();

            /* Create a button and a textArea relative to the specific dynasty */

            button = new DynastyChoiceButton(
                    this,
                    dynastyPageUrl,
                    dynastyName,
                    buttonIconPath,
                    buttonRolloverIconPath,
                    buttonDisabledIconPath
            );

            button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            String dynastyNameLabelText = " " + dynastyName.replace(" Dynasty", "\n Dynasty");
            customTextArea = new CustomTextArea(dynastyNameLabelText, Color.BLACK, fontFileName, fontStyle, fontSize);

            /*
             * Group button and textArea in a container and add it
             * to the Panel followed by an empty label to add a
             * space between the two containers
             */

            buttonLabelContainer = this.createButtonLabelDynastyContainer(button, customTextArea);
            add(buttonLabelContainer);

            /*
             * Due to the layout we place DynastyChoiceButton(s) into
             * being the following one:
             *
             * X X X
             *  X X X
             *
             * We add a single empty JLabel to create a spacing between
             * each button and the next one (so except for the last button
             * of the last row, the one where count == 5) and we create
             * two single empty JLabel(s) to "jump" on the next row after
             * the last button of the first row
             */

            if (count == 2) {
                add(new JLabel());
                add(new JLabel());
            } else if (count != 5) {
                add(new JLabel());
            }

            count++;
        }
    }

    /**
     * Given a button and a textArea, create a container to group them
     *
     * @param dynastyChoiceButton the DynastyChoiceButton object relative to a specific dynasty
     * @param dynastyChoiceTextArea the CustomTextArea object relative to a specific dynasty
     *
     * @return the created Container object
     */
    public Container createButtonLabelDynastyContainer(DynastyChoiceButton dynastyChoiceButton, CustomTextArea dynastyChoiceTextArea){
        Container buttonLabelDynastyContainer = new Container();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        buttonLabelDynastyContainer.setLayout(new GridBagLayout());

        /*
         * We put the DynastyChoiceButton on the top left side of the container
         */

        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        buttonLabelDynastyContainer.add(dynastyChoiceButton, gridBagConstraints);

        /*
         * We put the DynastyChoiceTextArea vertically
         * centered and on the right side of the container
         */

        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        buttonLabelDynastyContainer.add(dynastyChoiceTextArea, gridBagConstraints);

        return buttonLabelDynastyContainer;
    }

    /**
     * Given the Wikipedia page URL of a Dynasty and the corresponding scraping
     * button, disable it. This method is used for example when the scraping is
     * still running or the graph frame is already opened.
     *
     * Add the DynastyChoiceButton to an HashMap by using the dynastyPageUrl
     * as key so that the button can be later retrieved to be re-enabled
     *
     * @param dynastyPageUrl the dynasty's Wikipedia page URL
     * @param button the dynasty's corresponding DynastyChoiceButton
     */
    public void disableButton(String dynastyPageUrl, DynastyChoiceButton button) {
        button.setEnabled(false);

        disabledButtons.put(dynastyPageUrl, button);
    }

    /**
     * Given the Wikipedia page URL of a dynasty, get the corresponding button from
     * the HashMap to re-enable it. Remove it from the disabledButtons HashMap.
     *
     * @param dynastyPageUrl Wikipedia Url of the dynasty
     */
    public void enableButton(String dynastyPageUrl) {
        if (disabledButtons.containsKey(dynastyPageUrl)) {
            disabledButtons.get(dynastyPageUrl).setEnabled(true);

            disabledButtons.remove(dynastyPageUrl);
        }
    }
}
