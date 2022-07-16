package romanemperorsscraper.ui.tree;

import romanemperorsscraper.ui.ButtonProperties;
import romanemperorsscraper.ui.choice.DynastyChoiceButton;
import romanemperorsscraper.ui.choice.DynastyChoiceFrame;
import romanemperorsscraper.ui.people.PeopleListOpenButton;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Class representing the button placed on
 * the DynastyTreeFrame used to close it
 * and go back to the DynastyChoiceFrame
 *
 * @see romanemperorsscraper.ui.tree.DynastyTreeFrame
 * @see romanemperorsscraper.ui.choice.DynastyChoiceFrame
 * @see ButtonProperties
 *
 * @author Sara Lazzaroni
 */
public class CloseDynastyTreeFrameButton extends ButtonProperties implements ActionListener {
    private DynastyTreeFrame dynastyTreeFrame;

    /**
     * Construct a DynastyTreeFrame object by setting its field values
     *
     * @param dynastyTreeFrame the DynastyTreeFrame instance the button is placed in
     * @param buttonIconPath the icon path
     * @param buttonRolloverIconPath the rollover icon path
     */

    public CloseDynastyTreeFrameButton(DynastyTreeFrame dynastyTreeFrame, String buttonIconPath, String buttonRolloverIconPath){
        /*
         * Recall to the super class constructor method to eliminate borders, set the size, set the icon
         * shown when the mouse isn't on the button and the one shown when the mouse is over it
         */

        super(buttonIconPath, buttonRolloverIconPath);

        this.dynastyTreeFrame = dynastyTreeFrame;

        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*
         * When the button is clicked, the DynastyTreeFrame object relative
         * to a particular dynasty is disposed. When the frame is closed,
         * the DynastyChoiceButton object relative to this dynasty is enabled
         * and a new instance of its DynastyTreeFrame object can be created
         */

        DynastyTreeFrame graphLegendInstance = DynastyTreeFrame.getLegendFrameInstance();

        /*
         * If the closed graph is the legend graph, the button on the choice
         * frame doesn't exist and the button icons aren't set.
         */

        if(graphLegendInstance == null || !graphLegendInstance.equals(dynastyTreeFrame)) {
            String dynastyPageUrl = dynastyTreeFrame.getDynasty().getWikipediaPageUrl();

            try {
                DynastyChoiceFrame.getInstance().getBodyPanel().enableButton(dynastyPageUrl);

                /*
                 * Change the DynastyChoiceButton with the icon variant for when
                 * the corresponding dynasty has already been scraped but its
                 * corresponding DynastyTreeFrame is not actually opened
                 */

                DynastyChoiceButton.getButton(dynastyPageUrl).setIcon(new ImageIcon(ImageIO.read(CloseDynastyTreeFrameButton.class.getResourceAsStream("/resources/images/icons/choice/DynastyChoiceButton_scraped.png"))));
                DynastyChoiceButton.getButton(dynastyPageUrl).setRolloverIcon(new ImageIcon(ImageIO.read(CloseDynastyTreeFrameButton.class.getResourceAsStream("/resources/images/icons/choice/DynastyChoiceButton_scraped_roll.png"))));
            } catch (IOException ignored) {}
        } else {
            DynastyTreeFrame.setLegendFrameInstance(null);
        }

        /*
         * Since we're getting rid of the DynastyTreeFrame
         * an eventual PeopleList opened related to the same
         * Dynasty and DynastyTreeFrame gets closed too
         */

        PeopleListOpenButton.removeFromOpenedList(dynastyTreeFrame.getDynasty().getWikipediaPageUrl());

        /*
         * Dispose the frame
         */

        dynastyTreeFrame.dispose();
    }
}
