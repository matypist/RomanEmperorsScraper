package romanemperorsscraper.ui.people;

import romanemperorsscraper.run.RomanEmperorsScraper;
import romanemperorsscraper.scraping.dynasties.Dynasty;
import romanemperorsscraper.scraping.people.Person;
import romanemperorsscraper.scraping.people.PersonPageData;
import romanemperorsscraper.ui.FontUtils;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to create and represent a frame
 * containing a list of people from a Dynasty
 * to let the user choose one of them and see
 * his personal information (contained in their
 * PersonPageData object) through PersonCard.
 *
 * @see PersonCard
 * @see PersonPageData
 * @see PeopleListOpenButton
 * @see Dynasty
 *
 * @author Matteo Collica
 * @author Sara Lazzaroni
 */
public class PeopleList extends JFrame {
    private static HashMap<String, PersonCard> openedPersonCards = new HashMap<>();

    private JList peopleList;
    private JScrollPane scrollPane;

    private int maxElementNameLength = 0;
    private int numberOfPeople = 0;

    /**
     * Construct the PeopleList frame of a specific dynasty
     *
     * @param dynastyPageUrl the dynasty's Wikipedia page URL
     */
    public PeopleList(String dynastyPageUrl) {
        super(Dynasty.getStoredDynastyByUrl(dynastyPageUrl).getName() + " - People List");

        /*
         * OPTIONS
         */

        setIconImage(RomanEmperorsScraper.getAppIconImage());
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        /*
         * COMPONENTS
         */

        this.createPeopleList(dynastyPageUrl);

        this.createScrollPanePeopleList();

        add(scrollPane, BorderLayout.CENTER);

        /*
         * Set the width of the frame depending on the longest person name
         * in the list and the height depending on the number of people
         */

        setSize(maxElementNameLength * 15, (numberOfPeople + 4) * 30);

        /* Set the PeopleList distanced its width from the right side of the screen */

        setLocation(new Point((int) (GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth() - (int)(this.maxElementNameLength * 15)),0));

        setVisible(true);

        /*
         * On window close the PersonCard instance
         * gets removed from the openedPersonCards
         * so that a new instance can be eventually
         * be opened later on
         */

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                PeopleListOpenButton.removeFromOpenedList(dynastyPageUrl);
            }
        };
        this.addWindowListener(exitListener);
    }

    /**
     * Create the effective list component of the PeopleList frame
     * by putting all the dynasty people's names and birth dates
     * (to prevent cases of homonyms), styling them and making
     * possible to click on one of them to open the corresponding
     * PersonCard instance
     *
     * @param dynastyPageUrl the dynasty's Wikipedia page URL
     */
    public void createPeopleList(String dynastyPageUrl){
        DefaultListModel listModel = new DefaultListModel();
        peopleList = new JList(listModel);

        /* Create an HashMap containing all the dynasty people */

        HashMap<String, Person> namePersonMatches = this.getDynastyPeopleList(dynastyPageUrl);

        /* Sort the people names in alphabetical order */

        List<Map.Entry<String, Person>> list = new ArrayList<>(namePersonMatches.entrySet());
        list.sort(Map.Entry.comparingByKey());

        int elementNameLength;

        /*
         * Add each person of the dynasty to the list and
         * calculate the longest name while adding them
         */

        for(Map.Entry<String, Person> set : list){
            String personNameAndBirthDate = set.getKey();

            listModel.addElement(personNameAndBirthDate);

            elementNameLength = personNameAndBirthDate.length();

            if(elementNameLength > maxElementNameLength) {
                maxElementNameLength = elementNameLength;
            }
        }

        /* Get the number of people contained in the list */

        numberOfPeople = listModel.getSize();

        final PeopleList thisPeopleList = this;

        peopleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                try {
                    /* Get the name-birthdate key contained in the clicked button */

                    String key = (String) listModel.getElementAt(peopleList.locationToIndex(e.getPoint()));

                    /* Get the Person object corresponding to this name-birthdate key */

                    Person person = namePersonMatches.get(key);

                    /* Get the Wikipedia page URL of this person */

                    String personPageUrl = person.getPersonPageData().getPersonNameUrl().getUrl();

                    /*
                     * Create a PersonCard frame or bring it
                     * to the foreground if it already is
                     * opened for the given person
                     */

                    if (!openedPersonCards.containsKey(personPageUrl)) {
                        addToOpenedPersonCards(person.getPersonPageData().getPersonNameUrl().getUrl(), new PersonCard(dynastyPageUrl, person, thisPeopleList));
                    } else {
                        PersonCard personCard = openedPersonCards.get(personPageUrl);
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                personCard.toFront();
                            }
                        });
                    }
                } catch(ArrayIndexOutOfBoundsException ignored) {}
            }
        });

        /* Set the font and the background of the cells in the list */

        peopleList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                String text = value.toString();
                setText(text);
                setFont(FontUtils.getFont("Romanica.ttf", Font.PLAIN, 30f));

                setBackground(Color.WHITE);

                return this;
            }
        });
    }

    /**
     * Get all people from a specific dynasty from the dynastiesPeopleList HashMap
     *
     * @param dynastyPageUrl the person's Wikipedia page URL
     *
     * @return the HashMap containing a name-birthdate : Wikipedia page URL
     * entry for each dynasty member
     */
    public HashMap<String, Person> getDynastyPeopleList(String dynastyPageUrl) {
        HashMap<String, HashMap<String, String>> dynastiesPeopleList = PersonPageData.getDynastiesPeopleList();

        HashMap<String, Person> dynastyPeopleList = new HashMap<>();

        for (Map.Entry<String, HashMap<String, String>> dynasty : dynastiesPeopleList.entrySet()) {
            /*
             * Iterate over all the dynasties' Wikipedia page URLs and pick those
             * that are relative to the one with the given Wikipedia page URL
             */

            if (Dynasty.dynastyPageUrlEquals(dynastyPageUrl, dynasty.getKey()) || dynasty.getKey().equals(dynastyPageUrl)) {
                /*
                 * Collect all people of the dynasty into an HashMap
                 * containing a name-birthdate : Wikipedia page URL
                 * entry for each dynasty member
                 *
                 * e.g. key:   "Valeriano (200 d.C.)"
                 *      value: Valeriano's Person object
                 */

                for(Map.Entry<String, String> set : dynasty.getValue().entrySet()) {
                    Person person = Person.getStoredPersonObjects().get(set.getValue());

                    if(person != null) {
                        dynastyPeopleList.put(set.getKey(), person);
                    }
                }
            }
        }

        return dynastyPeopleList;
    }

    /**
     * Create a JScrollPane enclosing the list of people name-birthdate entries
     */
    public void createScrollPanePeopleList() {
        scrollPane = new JScrollPane(peopleList);

        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    /**
     * Remove a PersonCard instance from the openedPersonCards
     * list so that a new instance can be opened
     *
     * @param personPageUrl the person's Wikipedia page URL
     *                      (which serves as an ID for the
     *                      corresponding PersonCard instance)
     */
    public static void removeFromOpenedPersonCards(String personPageUrl) {
        openedPersonCards.remove(personPageUrl);
    }

    /**
     * Add a PersonCard instance to the openedPersonCards
     * so that a new instance can't be opened until the
     * actual one has been closed
     *
     * @param personPageUrl the person's Wikipedia page URL
     *                      (which serves as an ID for the
     *                      corresponding PersonCard instance)
     * @param personCard the PersonCard instance to be added
     */
    public static void addToOpenedPersonCards(String personPageUrl, PersonCard personCard){

        /* Add the PersonCard object relative to the person whose Url is passed as parameter to  the list of
         * opened PersonCards to avoid to create a lot of times the card of the same person meanwhile one
         * has already been opened */

        openedPersonCards.put(personPageUrl, personCard);
    }

    /**
     * Dispose this PeopleList frame instance
     */
    public void disposeList(){
        this.dispose();
    }
}


