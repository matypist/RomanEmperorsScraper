package romanemperorsscraper.ui.people;

import romanemperorsscraper.ui.ButtonProperties;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class representing the button placed on
 * the PersonCard frame used to go back to
 * the genealogical tree graph of the dynasty
 * the person is related to.
 *
 * @see PersonCard
 * @see ButtonProperties
 *
 * @author Sara Lazzaroni
 */
public class BackToGraphButton extends ButtonProperties implements ActionListener {
    private final PersonCard personCard;

    /**
     * Construct a BackToGraphButton by setting its field values and its action listener
     *
     * @param personCard the PersonCard object this BackToGraphButton instance is placed in
     * @param buttonIconPath the icon path
     * @param buttonRolloverIconPath the rollover icon path
     */
    public BackToGraphButton(PersonCard personCard, String buttonIconPath, String buttonRolloverIconPath){
        /*
         * Recall to the super class constructor method to eliminate borders, set the size, set the icon
         * shown when the mouse isn't on the button and the one shown when the mouse is over it
         */

        super(buttonIconPath, buttonRolloverIconPath);

        this.personCard = personCard;

        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*
         * On button click the PersonCard instance gets
         * disposed and removed from the openedPersonCards
         * list so that a new instance can be opened
         */

        PeopleList.removeFromOpenedPersonCards(personCard.getPerson().getPersonPageData().getPersonNameUrl().getUrl());

        personCard.dispose();
    }
}
