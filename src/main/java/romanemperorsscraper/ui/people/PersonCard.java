package romanemperorsscraper.ui.people;

import romanemperorsscraper.run.RomanEmperorsScraper;
import romanemperorsscraper.scraping.dynasties.Dynasty;
import romanemperorsscraper.scraping.people.Person;
import romanemperorsscraper.scraping.people.PersonNameUrl;
import romanemperorsscraper.scraping.people.PersonPageData;
import romanemperorsscraper.ui.choice.DynastyChoiceFrame;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class used to graphically visualize
 * information contained in a PersonPageData
 * object, which are the information relative
 * to a member of a Roman Emperors Dynasty.
 *
 * It can be opened by the user by clicking on
 * a PeopleListOpenButton in a DynastyTreeFrame.
 *
 * @see PersonPageData
 * @see PeopleListOpenButton
 * @see romanemperorsscraper.ui.tree.DynastyTreeFrame
 *
 * @author Sara Lazzaroni
 * @author Matteo Collica
 * @author Ivan Dalla Ragione
 */
public class PersonCard extends JFrame {
    private final String dynastyPageUrl;

    private Person person;

    private PersonPageData personPageData;

    private static HashMap<String, Image> imagesHashMap = new HashMap<>();

    private static Image defaultImage;

    private JLabel image;
    private JTextArea information;

    private BackToGraphButton backToGraphButton;
    private JLabel backToGraphLabel;

    private GoToSuccessorButton goToSuccessorButton;
    private JLabel goToSuccessorLabel;

    private Container imageButtonContainer;
    private Container buttonContainer;

    private JScrollPane informationScrollPane;

    static {
        try {
            defaultImage = adjustImageSize(ImageIO.read(
                PersonCard.class.getResourceAsStream(
                        "/resources/images/PersonCard_defaultImage.png")
            ));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Given a dynasty's Wikipedia page URL, a person to be represented and the
     * PeopleList this person is included into, create a frame containing image,
     * personal information, a button to go to his/her successors (if there are
     * available) and a button to go back to the graph
     *
     * @param dynastyPageUrl the Wikipedia page URL of the person's dynasty
     * @param person the Person object of the person to be represented
     * @param peopleList the PeopleList instance this PersonCard is related to
     */
    public PersonCard(String dynastyPageUrl, Person person, PeopleList peopleList) {
        /* Set the frame title to "<dynasty_name> - <person_name>" */
        super(Dynasty.getStoredDynastyByUrl(dynastyPageUrl).getName() + " - " + person.getPersonPageData().getPersonNameUrl().getFullName());

        this.dynastyPageUrl = dynastyPageUrl;

        this.person = person;

        this.personPageData = person.getPersonPageData();

        /* Set the image of the frame when it is reduced to an icon */

        setIconImage(RomanEmperorsScraper.getAppIconImage());

        /*
         * OPTIONS
         */

        setLayout(new FlowLayout());
        setSize(1500, 700);
        setLocationRelativeTo(peopleList);
        setResizable(false);

        /*
         * COMPONENTS
         */

        /* Load and set the frame's background image */

        try {
            final Image backgroundImage = javax.imageio.ImageIO.read(
                DynastyChoiceFrame.class.getResourceAsStream(
                        "/resources/images/DynastyChoiceFrame_bgImage.jpeg")
            );

            setContentPane(
                new JPanel(new FlowLayout()) {
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

        this.createButtonContainer();
        this.createImageButtonContainer();
        this.createInformationTextArea();
        this.createInformationScrollPane();

        add(imageButtonContainer);
        add(informationScrollPane);

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        /*
         * On window close, the PersonCard instance gets removed from the
         * openedPersonCards list so that a new instance can be opened
         */

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                PeopleList.removeFromOpenedPersonCards(person.getPersonPageData().getPersonNameUrl().getUrl());
            }
        };
        this.addWindowListener(exitListener);
    }

    /**
     * Create a container with a button used to go back to the graph
     * and a button used to go to the successor if there is at least one.
     * Add a descriptive label too for each button.
     */
    public void createButtonContainer() {
        /*
         * BACK TO GRAPH BUTTON
         */

        /* Create the back to graph button and set its icon variants */

        String buttonIconPath = "/resources/images/icons/people/BackToGraphButton.png";
        String buttonRolloverIconPath = "/resources/images/icons/people/BackToGraphButton_roll.png";
        backToGraphButton = new BackToGraphButton(this, buttonIconPath, buttonRolloverIconPath);

        backToGraphLabel = new JLabel("Go back to the graph");

        /*
         * CONTAINER
         */

        buttonContainer = new Container();

        /*
         * Dynamically set layout rows to fit one or two buttons depending
         * on the presence or absence of the "go to successor button"
         */

        if(person.getSuccessors().size() > 0) {
            buttonContainer.setLayout(new GridLayout(2, 2));
        } else {
            buttonContainer.setLayout(new GridLayout(1, 2));
        }

        /* Add the back to graph button and label to the button container */

        buttonContainer.add(backToGraphButton);
        buttonContainer.add(backToGraphLabel);

        if(person.getSuccessors().size() > 0) {
            /*
             * GO TO SUCCESSOR BUTTON
             */

            /* Create the "go to successor button" and set its icon variants */

            buttonIconPath = "/resources/images/icons/people/GoToSuccessorButton.png";
            buttonRolloverIconPath = "/resources/images/icons/people/GoToSuccessorButton_roll.png";
            goToSuccessorButton = new GoToSuccessorButton(this, buttonIconPath, buttonRolloverIconPath);

            goToSuccessorLabel = new JLabel("Go to the next successor");

            /* Add the back to graph button and label to the button container */

            buttonContainer.add(goToSuccessorButton);
            buttonContainer.add(goToSuccessorLabel);
        }
    }

    /**
     * Create a container with the person's image, if available, or a default image,
     * the button to go back to the graph and, in case, the button to go to the successor
     */
    public void createImageButtonContainer() {
        /* Download (or get the already downloaded) image */

        Image img = this.getPersonImage(personPageData);
        ImageIcon imageIcon = new ImageIcon(img);

        image = new JLabel(imageIcon);
        image.setBounds(0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight());

        int maxWidth = imageIcon.getIconWidth();

        int buttonContainerWidth = (int) buttonContainer.getPreferredSize().getWidth();
        int buttonContainerHeight = (int) buttonContainer.getPreferredSize().getHeight();

        if(buttonContainerWidth > maxWidth) {
            maxWidth = buttonContainerWidth;
        }

        /*
         * If the image is bigger than the button container, the button container
         * will be located horizontally where the image starts. Instead, if the button
         * container is bigger, it will be located horizontally before the image.
         *
         * Vertically, the button container will be located under the image.
         */

        buttonContainer.setBounds(
            (imageIcon.getIconWidth() - maxWidth),
            imageIcon.getIconHeight(),
            maxWidth,
            buttonContainerHeight
        );

        /*
         * The ImageButtonContainer groups the person's
         * image and the buttons to put them int the right
         * part of the frame
         */

        imageButtonContainer = new Container();
        imageButtonContainer.setLayout(null);

        imageButtonContainer.add(image);
        imageButtonContainer.add(buttonContainer);

        /*
         * While the width is the largest one between the image and the button
         * container, the height is given by the sum of the image height and the
         * button container height.
         */

        imageButtonContainer.setPreferredSize(
            new Dimension(maxWidth, (int) (imageIcon.getIconHeight() + buttonContainer.getPreferredSize().getHeight()))
        );
    }

    /**
     * Create the text area containing the represented person's personal information
     */
    private void createInformationTextArea() {
        StringBuilder string = this.createInformation(personPageData);

        information = new JTextArea(string.toString());
        information.setFont(new Font("Serif", Font.BOLD, 20));
        information.setEditable(false);
        information.setOpaque(false);
    }

    /**
     * Create a scrollPane containing the represented person's personal information
     */
    private void createInformationScrollPane() {
        /*
         * Create a ScrollPane to allow to see all personal
         * information even if the string is too long
         */

        Color color = new Color(220, 220, 220);

        informationScrollPane = new JScrollPane(information);
        informationScrollPane.getViewport().setOpaque(false);
        informationScrollPane.setOpaque(false);

        informationScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        informationScrollPane.getHorizontalScrollBar().setBackground(color);

        informationScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        informationScrollPane.getVerticalScrollBar().setBackground(color);

        /*
         * The information ScrollPane is spaced from the bottom
         * of the frame by the button container height.
         */

        informationScrollPane.setBorder(BorderFactory.createEmptyBorder(
                0,
                10,
                (int) buttonContainer.getPreferredSize().getHeight(),
                0)
        );

        informationScrollPane.setPreferredSize(new Dimension(
                600,
                (int) imageButtonContainer.getPreferredSize().getHeight()
        ));
    }

    /**
     * Scale / enlarge an image by a proportional constant so
     * that its proportions are as much as possible maintained.
     *
     * Useful to normalize all the images (especially the small
     * ones) around a specific width and height without losing
     * too much quality.
     *
     * @param image the image to be scaled
     *
     * @return the image's scaled instance
     */
    private static Image adjustImageSize(BufferedImage image) {
        /*
         * To enlarge the image maintaining the right proportions,
         * get the height and the width of the original image and
         * multiply them by the minimum between height factor and
         * width factor. This two factors are obtained dividing a
         * constant (400 in this case), respectively, by the height
         * and by the width.
         */
        final int SCALING_CONSTANT = 400;

        int height = image.getHeight();
        int width  = image.getWidth();

        float rapHeight = (float) SCALING_CONSTANT / height;
        float rapWidth  = (float) SCALING_CONSTANT / width;

        height = (int) (height * Math.min(rapHeight, rapWidth));
        width  = (int) (width  * Math.min(rapWidth, rapHeight));

        return image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
    }

    /**
     * Create a string representing all the information of a given PersonPageData instance
     *
     * @param personPageData the PersonPageData instance of the person to represent
     *
     * @return the created string of personal information
     */
    public StringBuilder createInformation(PersonPageData personPageData){
        /*
         * Space the field from the value with two tabs
         * e.g. Name      Marco Aurelio
         */

        StringBuilder information = new StringBuilder();
        information.append("Name").append("\t\t").append(personPageData.getPersonNameUrl().getFullName());

        /*
         * Space one row from another one with two blank lines
         * e.g. Mother        Domizia Lucila
         *
         *
         *      Father        Marco Annio Vero
         */

        /*
         * Each of the following instructions appends single information if it is not null
         */

        information = appendStringToInformation(information, "Birth Date", personPageData.getBirthDate());
        information = appendStringToInformation(information, "Death Date", personPageData.getDeathDate());
        information = appendStringToInformation(information, "Reign Start", personPageData.getReignBeginningDate());
        information = appendStringToInformation(information, "Reign End", personPageData.getReignEndDate());

        information.append("\n").append("\n");
        information.append("Role").append("\t\t").append(personPageData.getRole());

        /*
         * Each of the following instructions appends a single relative's information if his/her PersonNameUrl exists
         */

        information = appendStringToInformation(information, "Mother", personPageData.getMotherNameUrl());
        information = appendStringToInformation(information, "Father", personPageData.getFatherNameUrl());
        information = appendStringToInformation(information, "Adoptive Father", personPageData.getAdoptiveFatherNameUrl());

        /*
         * Each of the following instructions appends an ArrayList of relatives of
         * a specific kinship degree if it exists and contains at least one entry
         */

        information = appendStringToInformation(information, "Successors", personPageData.getSuccessors());
        information = appendStringToInformation(information, "Spouses", personPageData.getSpouses());
        information = appendStringToInformation(information, "Children", personPageData.getChildren());
        information = appendStringToInformation(information, "Adoptive children", personPageData.getAdoptedChildren());

        return information;
    }

    /**
     * Append single information if it is not null
     *
     * @param information the string of personal information
     * @param fieldName the field name (e.g. 'Birth Date')
     * @param fieldValue the field value
     *
     * @return the updated information
     */
    private static StringBuilder appendStringToInformation(StringBuilder information, String fieldName, String fieldValue) {
        if(fieldValue != null) {
            information.append("\n").append("\n");
            information.append(fieldName).append("\t\t").append(fieldValue);
        }

        return information;
    }

    /**
     * Append a single relative's information if his/her PersonNameUrl exists
     *
     * @param information the string of personal information
     * @param fieldName the single information's field name (e.g. 'Mother')
     * @param personNameUrl the relative's PersonNameUrl instance
     *
     * @return the updated information
     */
    private static StringBuilder appendStringToInformation(StringBuilder information, String fieldName, PersonNameUrl personNameUrl) {
        if(personNameUrl != null) {
            String personName = personNameUrl.getFullName();

            information.append("\n").append("\n");

            if (!fieldName.contains("Adop")){
                information.append(fieldName).append("\t\t").append(personName);
            }
            else{
                information.append(fieldName).append("\t").append(personName);
            }
        }

        return information;
    }

    /**
     * Appends an ArrayList of relatives of a specific kinship
     * degree if it exists and contains at least one entry
     *
     * @param information the string of personal information
     * @param fieldName the kinship degree type name (e.g. 'Children')
     * @param arrayOfPersonNameUrls the ArrayList of PersonNameUrl(s) of
     *                              that specific kinship degree's relatives
     *
     * @return the updated information
     */

    private static StringBuilder appendStringToInformation(StringBuilder information, String fieldName, ArrayList<PersonNameUrl> arrayOfPersonNameUrls) {
        if(arrayOfPersonNameUrls != null && arrayOfPersonNameUrls.size() != 0) {
            information.append("\n").append("\n");

            if (!fieldName.contains("Adop")) {
                information.append(fieldName).append("\t\t");
            }
            else{
                information.append(fieldName).append("\t");
            }

            information = createMultipleInformation(arrayOfPersonNameUrls, information);
        }

        return information;
    }

    /**
     * Given the ArrayList of PersonNameUrl(s) of a specific kinship degree's
     * relatives, add the list of their names to the string of personal information
     *
     * @param arrayOfPersonNameUrl the ArrayList of PersonNameUrl(s)
     * @param information the string of personal information
     *
     * @return the updated information
     */
    public static StringBuilder createMultipleInformation(ArrayList<PersonNameUrl> arrayOfPersonNameUrl, StringBuilder information) {
        /* Create a piece of string containing some relatives */
        int numberOfArrayElements = arrayOfPersonNameUrl.size(), i = 0;

        for(PersonNameUrl personNameUrl : arrayOfPersonNameUrl) {
            /*
             * If the person name to add isn't the first one,
             * put a newline and two tabs before
             */

            if(i != 0) {
                information.append("\n");
                information.append("\t").append("\t");
            }

            /*
             * If the person name isn't the last one,
             * add ',' at the end of the name
             */

            if(i != numberOfArrayElements - 1) {
                information.append(personNameUrl.getFullName()).append(", ");

                i++;
            }  else {
                information.append(personNameUrl.getFullName());
            }

            /*
             * e.g Children      Tito Aurelio Antonino,
             *                   Tito Elio Aurelio,
             *                   Marco Annio Verio Cesare
             */
        }

        return information;
    }

    /**
     * Given a person's PersonPageData object, download his/her image
     * from the imageUrl, if available, or get the default image otherwise.
     *
     * @param personPageData the given PersonPageData object
     *
     * @return the image of the person represented by the PersonPageData object
     */
    public Image getPersonImage(PersonPageData personPageData) {
        String imageUrl = personPageData.getImageUrl();

        Image scaledImage;

        if(imageUrl != null) {
            /* If the image isn't in the HashMap, it hasn't already been loaded */

            if(!imagesHashMap.containsKey(imageUrl)) {
                BufferedImage image;

                /* So, download the image from the imageUrl */

                try {
                    image = ImageIO.read(new URL(personPageData.getImageUrl()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                /* Enlarge the image */

                scaledImage = adjustImageSize(image);

                /* Put the downloaded image in the HashMap */

                imagesHashMap.put(imageUrl, scaledImage);
            } else {
                /* Get the already downloaded image otherwise */

                scaledImage = imagesHashMap.get(imageUrl);
            }
        } else {
            /*
             * If there is no imageUrl (it happens when there isn't
             * an image on the Wikipedia page) load a default image
             */

            scaledImage = defaultImage;
        }

        return scaledImage;
    }

    /**
     * Replace the represented person's personal
     * information and image with her/his successor's
     *
     * @param personCard the PersonCard instance to be updated
     * @param successor the Person object relative to the new person to represent
     */
    public static void updateCard(PersonCard personCard, Person successor) {
        /*
         * Replace the PersonCard information relative to the current represented person
         * with the information relative to the new person to represent
         */

        Image img = personCard.getPersonImage(successor.getPersonPageData());
        personCard.setImage(new ImageIcon(img));

        personCard.setPerson(successor);

        String successorDynastyPageUrl = successor.getPersonPageData().getPersonDynastyPageUrl();
        String successorFullName = successor.getPersonPageData().getPersonNameUrl().getFullName();
        personCard.setTitle(Dynasty.getStoredDynastyByUrl(successorDynastyPageUrl).getName() + " - " + successorFullName);

        PersonPageData successorPersonPageData = successor.getPersonPageData();
        StringBuilder newInformation = personCard.createInformation(successorPersonPageData);
        personCard.setInformation(newInformation);

        /* If the successor has no successors, hide the
         * button used to go to the successor and its label
         */

        if(personCard.getPerson().getSuccessors().size() == 0) {
            personCard.getGoToSuccessorButton().setVisible(false);
            personCard.getGoToSuccessorLabel().setVisible(false);
        }
    }

    /**
     * @param image the image to be set
     */
    public void setImage(ImageIcon image){
        this.image.setIcon(image);
    }

    /**
     * @param person the Person object represented by this PersonCard instance to be set
     */
    public void setPerson(Person person){
        this.person= person;
    }

    /**
     * @param information the string to update personal information to
     */
    public void setInformation(StringBuilder information){
        this.information.setText(information.toString());
    }

    /**
     * @return the button used to go to the represented person's successors
     */
    public GoToSuccessorButton getGoToSuccessorButton(){
        return goToSuccessorButton;
    }

    /**
     * @return the represented person's Person object
     */
    public Person getPerson(){
        return person;
    }

    /**
     * @return the label of the GoToSuccessorButton
     */
    public JLabel getGoToSuccessorLabel() {
        return goToSuccessorLabel;
    }

    /**
     * @return the label containing the represented person's image
     */
    public JLabel getImage() {
        return image;
    }
}