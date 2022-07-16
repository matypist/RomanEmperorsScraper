package romanemperorsscraper.ui.people;

import romanemperorsscraper.scraping.people.Person;
import romanemperorsscraper.ui.ButtonProperties;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Class representing the button placed on
 * the PersonCard frame (only when the person
 * is an emperor) to go to its successor's
 * PersonCard.
 *
 * @see PersonCard
 * @see ButtonProperties
 *
 * @author Sara Lazzaroni
 * @author Matteo Collica
 */
public class GoToSuccessorButton extends ButtonProperties implements ActionListener {
    private final PersonCard personCard;

    /**
     * Construct a GoToSuccessorButton by setting its field values and its action listener
     *
     * @param personCard the PersonCard object this GoToSuccessorButton instance is placed in
     * @param buttonIconPath the icon path
     * @param buttonRolloverIconPath the rollover icon path
     */
    public GoToSuccessorButton(PersonCard personCard, String buttonIconPath, String buttonRolloverIconPath){
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
         * When switching to the successor's PersonCard,
         * remove the previous PersonCard instance from
         * the openedPersonCards so that a new instance
         * can be eventually later opened
         */

        PeopleList.removeFromOpenedPersonCards(
            personCard.getPerson().getPersonPageData().getPersonNameUrl().getUrl()
        );

        /*
         * On button click, if the represented person has more than one successor,
         * a JDialog is shown to let the user choose one of them. Instead, if the
         * represented person has only one successor, his/her personal information
         * directly replace the current ones.
         */

        ArrayList<Person> successors = personCard.getPerson().getSuccessors();

        if(successors.size() > 1) {
            new SuccessorListDialog(successors, personCard);
        } else {
            PersonCard.updateCard(personCard, successors.get(0));
        }

        /*
         * After creating the successor's PersonCard, add successor's PersonCard
         * instance to the openedPersonCards list to avoid opening twice the
         * same card meanwhile an instance is already opened
         */

        PeopleList.addToOpenedPersonCards(personCard.getPerson().getPersonPageData().getPersonNameUrl().getUrl(), personCard);
    }
}
