package romanemperorsscraper.ui.choice;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import romanemperorsscraper.run.RomanEmperorsScraper;
import romanemperorsscraper.scraping.dynasties.Dynasty;
import romanemperorsscraper.scraping.dynasties.DynastySerializer;
import romanemperorsscraper.scraping.people.PersonPageData;
import romanemperorsscraper.scraping.people.PersonPageDataSerializer;
import romanemperorsscraper.ui.ButtonProperties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing the button placed on DynastyChoiceFrame
 * (inside the DynastyChoiceTitlePanel) used to import dynasties
 * and people's data.
 *
 * @see DynastyChoiceFrame
 * @see DynastyChoiceTitlePanel
 * @see ImportButton
 * @see ButtonProperties
 *
 * @author Matteo Collica
 */
public class ImportButton extends ButtonProperties implements ActionListener {
    private DynastyChoiceTitlePanel dynastyChoiceTitlePanel;

    /**
     * Given the Dynasty Choice Title Panel the button is contained in, the icon
     * path and the rollover icon path, construct an ImportButton object by
     * setting its properties (width, height, icon) and its action listener
     *
     * @param dynastyChoiceTitlePanel the DynastyChoiceTitlePanel this button is contained in
     * @param importButtonIconPath the path of the icon to be set
     * @param importButtonRolloverIconName the path of the Rollover icon to be set
     */
    public ImportButton(DynastyChoiceTitlePanel dynastyChoiceTitlePanel, String importButtonIconPath, String importButtonRolloverIconName) {
        super(importButtonIconPath, importButtonRolloverIconName);

        this.dynastyChoiceTitlePanel = dynastyChoiceTitlePanel;

        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        /*
         * After the import button was clicked, we start a JFileChooser instance to
         * permit the user to choose which .json file he/she wants to import data from
         */

        //JFileChooser jFileChooser = new JFileChooser(System.getProperty("user.dir") + ImportButton.class.getResource(".").getPath().split("classes")[0] + "classes/");
        JFileChooser jFileChooser = new JFileChooser(System.getProperty("user.dir"));

        FileNameExtensionFilter jsonExtensionFilter = new FileNameExtensionFilter(".json", "json");
        jFileChooser.addChoosableFileFilter(jsonExtensionFilter);
        jFileChooser.setAcceptAllFileFilterUsed(false);

        jFileChooser.setDialogTitle("Choose a json file to import data from");
        jFileChooser.setApproveButtonText("Import");
        jFileChooser.setApproveButtonToolTipText("Import data from the selected file");

        /*
         * Once the JFileChooser is finally set up, we show the open dialog to
         * the user and wait for the approval ('Import') button to be pressed
         */

        int returnValue = jFileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            Window rootWindow = SwingUtilities.getWindowAncestor(dynastyChoiceTitlePanel);

            /*
             * First we get and read through the dedicated parser
             * the root JSONObject of the selected file
             */

            File selectedFile = jFileChooser.getSelectedFile();

            String selectedFileAbsolutePath = selectedFile.getAbsolutePath();

            JSONParser parser = new JSONParser();
            Reader reader;

            try {
                reader = new FileReader(selectedFileAbsolutePath);

                Object object = parser.parse(reader);

                JSONObject jsonFileObject = (JSONObject) object;

                /*
                 * Once we've got the root JSONObject we can get its two children
                 * objects (containing the serialized people and dynasties data)
                 */

                JSONObject serializedPeople = (JSONObject) jsonFileObject.get("people");

                JSONObject serializedDynasties = (JSONObject) jsonFileObject.get("dynasties");

                if(serializedPeople != null && serializedDynasties != null) {
                    HashMap<String, PersonPageData> unserializedPeople =
                            PersonPageDataSerializer.deserializePersonPageDataHashMap(serializedPeople);

                    /*
                     * If there were no serialized people data in the selected file
                     * it is also useless to search for dynasties data there, so we
                     * only do it otherwise
                     */

                    if(!unserializedPeople.isEmpty()) {

                        /*
                         * We wish to overwrite current stored PersonPageData objects
                         * only if there are deserialized ones to overwrite with
                         * (that's why the following set is put inside this if)
                         */

                        PersonPageData.setUrlPersonPageDataMatches(unserializedPeople);

                        /*
                         * After having deserialized and successfully imported the
                         * people' PersonPageData objects we can now deserialize
                         * and import the dynasties' Dynasty objects
                         */

                        HashMap<String, Dynasty> deserializedDynasties = DynastySerializer.deserializeDynastiesHashMap(serializedDynasties);

                        String dynastyPageUrl;

                        for(Map.Entry <String, Dynasty> set : deserializedDynasties.entrySet()) {
                            dynastyPageUrl = set.getKey();

                            DynastyChoiceButton.getButton(dynastyPageUrl).setIcon(new ImageIcon(ImageIO.read(ImportButton.class.getResourceAsStream("/resources/images/icons/choice/DynastyChoiceButton_scraped.png"))));
                            DynastyChoiceButton.getButton(dynastyPageUrl).setRolloverIcon(new ImageIcon(ImageIO.read(ImportButton.class.getResourceAsStream("/resources/images/icons/choice/DynastyChoiceButton_scraped_roll.png"))));
                        }

                        Dynasty.setStoredDynasties(deserializedDynasties);

                        /*
                         * For every possible outcome of the import process we'll
                         * show the user an explanatory Message Dialog
                         */

                        JOptionPane.showMessageDialog(
                                rootWindow,
                                "Data of " + unserializedPeople.size()
                                        + " people from " + deserializedDynasties.size()
                                        + " dynasties:\n" + Dynasty.getStoredDynastiesNames()
                                        + "\nSuccesfully imported from:\n'" + selectedFile.getName() + "'",
                                "Successful import",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                rootWindow,
                                "The selected file does not contain any people",
                                "Empty file selected",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            rootWindow,
                            "The selected file does not contain any data or isn't a RomanEmperorsScraper valid export file",
                            "Wrong file selected",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        rootWindow,
                        "There was an I/O error while trying to read the import file, " +
                                "run this jar in console with '--debug' parameter to read the " +
                                "debug messages and please report it to the developer should" +
                                "the problem persist:\n\n" + e.getMessage(),
                        "I/O error while reading import file",
                        JOptionPane.ERROR_MESSAGE
                );

                if(RomanEmperorsScraper.isDebugEnabled()) {
                    e.printStackTrace();
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(
                        rootWindow,
                        "The selected file is not a valid JSON file",
                        "Wrong file selected",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
