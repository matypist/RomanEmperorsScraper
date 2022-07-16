package romanemperorsscraper.ui.people;

import romanemperorsscraper.ui.ButtonProperties;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * Class used to create and represent a button
 * used to open a PeopleList instance related
 * to a specific Dynasty. This button is placed
 * into the DynastyTreeFrame (inside the Dynasty
 * TreeTitlePanel).
 *
 * @see PeopleList
 * @see romanemperorsscraper.scraping.dynasties.Dynasty
 * @see romanemperorsscraper.ui.tree.DynastyTreeFrame
 * @see romanemperorsscraper.ui.tree.DynastyTreeTitlePanel
 * @see ButtonProperties
 *
 * @author Matteo Collica
 * @author Sara Lazzaroni
 */
public class PeopleListOpenButton extends ButtonProperties implements ActionListener {
    private final String dynastyPageUrl;
    private static HashMap<String, PeopleList> openedLists = new HashMap<>();

    /**
     * Create a PeopleListOpenButton by setting its field values
     *
     * @param dynastyPageUrl the Wikipedia page URL of the dynasty the button is related to
     * @param buttonIconPath the icon path
     * @param buttonRolloverIconPath the rollover icon path
     */
    public PeopleListOpenButton(String dynastyPageUrl, String buttonIconPath, String buttonRolloverIconPath) {
        /* Recall to the super class constructor method to eliminate borders, set the size, set the icon
         * shown when the mouse isn't on the button and the one shown when the mouse is over it */

        super(buttonIconPath, buttonRolloverIconPath);

        this.dynastyPageUrl = dynastyPageUrl;

        this.addActionListener(this);
    }

    /**
     * Add a PeopleList instance to the openedLists HashMap used to
     * later prevent user from opening the same PeopleList twice
     *
     * @param peopleList the PeopleList instance to be added
     */
    public  void addToOpenedList(PeopleList peopleList) {
        openedLists.put(dynastyPageUrl, peopleList);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(!openedLists.containsKey(dynastyPageUrl)) {
            /*
             * On button click, if the PeopleList isn't already opened,
             * it gets created and added to the list containing the
             * already opened PeopleList(s)
             */

            addToOpenedList(new PeopleList(dynastyPageUrl));
        } else {
            /*
             * Instead, if the peopleList was already opened,
             * the opened instance is brought to the foreground
             */

            JFrame peopleListFrame = (JFrame) openedLists.get(dynastyPageUrl);

            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    peopleListFrame.toFront();
                }
            });
        }
    }

    /**
     * Dispose the PeopleList frame relative to the dynasty Wikipedia page URL
     * given as parameter and remove its instance from the list of already opened
     * PeopleList(s) so that a new instance can eventually be opened later
     *
     * @param dynastyPageUrl the dynasty's Wikipedia page URL
     */
    public static void removeFromOpenedList(String dynastyPageUrl) {
        if(openedLists.containsKey(dynastyPageUrl)){
            openedLists.get(dynastyPageUrl).disposeList();

            openedLists.remove(dynastyPageUrl);
        }
    }
}
