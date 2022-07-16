package romanemperorsscraper.ui.people;

import romanemperorsscraper.scraping.people.Person;
import romanemperorsscraper.ui.FontUtils;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * When a person related to a Roman Emperor Dynasty
 * has more than one successor, on click on the
 * GoToSuccessorButton the dialog represented by
 * this class gets shown instead to let the user
 * choose one of them.
 *
 * @see PersonCard
 * @see GoToSuccessorButton
 *
 * @author Matteo Collica
 * @author Sara Lazzaroni
 */
public class SuccessorListDialog extends JDialog {
    private ArrayList<Person> successors;
    private PersonCard personCard;

    /**
     * Given a PersonCard instance and an ArrayList containing the
     * represented person's successors, create a dialog to let
     * the user choose one of them
     *
     * @param successors the ArrayList containing the successors
     * @param personCard the predecessor's PersonCard instance
     */
    public SuccessorListDialog(ArrayList<Person> successors, PersonCard personCard) {
        super();

        setTitle(personCard.getPerson().getPersonPageData().getPersonNameUrl().getFullName() + " - Successors");

        this.successors = successors;
        this.personCard = personCard;

        /* Set the successors list location relative to the PersonCard image's position */
        setLocationRelativeTo(personCard.getImage());

        /* Set the Layout to GridLayout to create as many cells as the person's successors */
        setLayout(new GridLayout(successors.size() + 1, 1));

        /*
         * Create a stylish JLabel containing the prompt text
         */

        JLabel successorDialogTitle = new JLabel("Choose a successor");
        successorDialogTitle.setHorizontalAlignment(JLabel.CENTER);
        successorDialogTitle.setFont(FontUtils.getFont("Romanica.ttf", Font.BOLD, 20f));
        add(successorDialogTitle);

        int maxElementNameLength = 0;

        /*
         * Iterate over successors to put them
         * in the dialog as possible choices
         */

        for(Person successor : successors) {
            /* Obtain the current successor's name */
            String successorName = successor.getPersonPageData().getPersonNameUrl().getFullName()+" "+successor.getPersonPageData().getBirthDate();

            /* Update the longest successor's name if the current one is longer */
            if(successorName.length() > maxElementNameLength) {
                maxElementNameLength = successorName.length();
            }

            /* Create a button for each successor */
            JButton successorButton = new JButton(successorName);
            successorButton.setFont(FontUtils.getFont("Romanica.ttf", Font.PLAIN, 20f));

            /*
             * Add an actionListener to the current button
             * so that on click the PersonCard information
             * gets updated to the corresponding successor
             * and the dialog gets disposed
             */
            successorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PersonCard.updateCard(personCard, successor);

                    PeopleList.addToOpenedPersonCards(personCard.getPerson().getPersonPageData().getPersonNameUrl().getUrl(), personCard);

                    dispose();
                }
            });

            /* Add the successor button to the dialog */
            add(successorButton);
        }

        /*
         * Set the width of the dialog depending on the size of the longest
         * successor's name and the height of the dialog depending on the
         * number of successors
         */

        setSize(maxElementNameLength * 15,(successors.size() + 4) * 30);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setVisible(true);
    }
}
