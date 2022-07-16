package romanemperorsscraper.ui.tree;

import romanemperorsscraper.ui.CustomTextArea;
import romanemperorsscraper.ui.FontUtils;
import romanemperorsscraper.ui.people.PeopleListOpenButton;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

/**
 * Class extending JPanel to represent a panel
 * containing the dynasty name (in form of a title),
 * the button to close the DynastyTreeFrame this
 * panel is placed into and the button to open
 * the PeopleList instance of the dynasty the
 * DynastyTreeFrame is related to.
 *
 * @see DynastyTreeFrame
 * @see CloseDynastyTreeFrameButton
 * @see PeopleListOpenButton
 *
 * @author Sara Lazzaroni
 * @author Ivan Dalla Ragione
 */
public class DynastyTreeTitlePanel extends JPanel {
    private DynastyTreeFrame dynastyTreeFrame;

    private CloseDynastyTreeFrameButton closeDynastyTreeFrameButton;

    private Container peopleListButtonContainer;

    private PeopleListOpenButton peopleListOpenButton;

    private CustomTextArea peopleListOpenButtonTextArea;

    private Container backToChoiceFrameButtonContainer;

    private CustomTextArea backToChoiceFrameButtonTextArea;

    /**
     * Create a panel containing the dynasty name, the container
     * with the button to close this DynastyTreeFrame instance and
     * the container with the button to open the list of dynasty people
     */
    public DynastyTreeTitlePanel(DynastyTreeFrame dynastyTreeFrame) {
        super();

        this.dynastyTreeFrame = dynastyTreeFrame;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        /*
         * TITLE (THE DYNASTY NAME)
         */

        JLabel titleLabel = new JLabel(dynastyTreeFrame.getDynasty().getName());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        titleLabel.setFont(FontUtils.getFont("RomanFont7.ttf", Font.BOLD, 55f));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        /*
         * CLOSE DYNASTYTREEFRAME BUTTON CONTAINER (BUTTON AND DESCRIPTIVE TEXT AREA)
         */

        this.createCloseDynastyTreeFrameButtonContainer();

        /*
         * OPEN PEOPLE LIST BUTTON CONTAINER (BUTTON AND DESCRIPTIVE TEXT AREA)
         */

        this.createPeopleListButtonContainer();

        add(backToChoiceFrameButtonContainer, BorderLayout.WEST);
        add(titleLabel, BorderLayout.CENTER);
        add(peopleListButtonContainer, BorderLayout.EAST);
    }

    /**
     * Create a container with a button used
     * to close this DynastyTreeFrame instance
     */
    public void createCloseDynastyTreeFrameButtonContainer() {
        /*
         * BUTTON
         */

        /* Create the button and set its icon variants */

        String buttonIconPath = "/resources/images/icons/tree/BackToChoiceFrameButton.png";
        String buttonRolloverIconPath = "/resources/images/icons/tree/BackToChoiceFrameButton_roll.png";
        closeDynastyTreeFrameButton = new CloseDynastyTreeFrameButton(dynastyTreeFrame, buttonIconPath, buttonRolloverIconPath);

        /*
         * DESCRIPTIVE TEXT AREA
         */

        backToChoiceFrameButtonTextArea = new CustomTextArea("Back to Dynasty Choice", Color.BLACK, "RomanFont7.ttf", Font.BOLD, 15f);
        backToChoiceFrameButtonTextArea.setPreferredSize(new Dimension(200, 30));

        /*
         * CONTAINER
         */

        backToChoiceFrameButtonContainer = new Container();
        backToChoiceFrameButtonContainer.setLayout(new FlowLayout());

        backToChoiceFrameButtonContainer.add(closeDynastyTreeFrameButton);
        backToChoiceFrameButtonContainer.add(backToChoiceFrameButtonTextArea);
    }

    /**
     * Create a container with a button used to open
     * a list with people of the dynasty to choose one
     * of them and see his/her personal information
     */
    public void createPeopleListButtonContainer() {
        /*
         * BUTTON
         */

        /* Create the button and set its icon variants */

        String buttonIconPath = "/resources/images/icons/tree/PeopleListOpenButton.png";
        String buttonRolloverIconPath = "/resources/images/icons/tree/PeopleListOpenButton_roll.png";
        peopleListOpenButton = new PeopleListOpenButton(dynastyTreeFrame.getDynasty().getWikipediaPageUrl(), buttonIconPath, buttonRolloverIconPath);

        /*
         * DESCRIPTIVE TEXT AREA
         */

        peopleListOpenButtonTextArea = new CustomTextArea("Open people list", Color.BLACK, "RomanFont7.ttf", Font.BOLD, 15f);
        peopleListOpenButtonTextArea.setPreferredSize(new Dimension(140, 30));

        /*
         * CONTAINER
         */

        peopleListButtonContainer = new Container();
        peopleListButtonContainer.setLayout(new FlowLayout());

        peopleListButtonContainer.add(peopleListOpenButtonTextArea);
        peopleListButtonContainer.add(peopleListOpenButton);
    }
}
