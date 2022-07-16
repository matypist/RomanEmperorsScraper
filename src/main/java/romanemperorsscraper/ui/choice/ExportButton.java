package romanemperorsscraper.ui.choice;

import org.json.simple.JSONObject;
import romanemperorsscraper.run.RomanEmperorsScraper;
import romanemperorsscraper.scraping.dynasties.Dynasty;
import romanemperorsscraper.scraping.dynasties.DynastySerializer;
import romanemperorsscraper.scraping.people.PersonPageData;
import romanemperorsscraper.scraping.people.PersonPageDataSerializer;
import romanemperorsscraper.ui.ButtonProperties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Class representing the button placed on DynastyChoiceFrame
 * (inside the DynastyChoiceTitlePanel) used to export dynasties
 * and people's data.
 *
 * @see DynastyChoiceFrame
 * @see DynastyChoiceTitlePanel
 * @see ExportButton
 * @see ButtonProperties
 *
 * @author Matteo Collica
 */
public class ExportButton extends ButtonProperties implements ActionListener {
    private DynastyChoiceTitlePanel dynastyChoiceTitlePanel;

    /**
     * Given the Dynasty Choice Title Panel the button is contained in, the icon
     * path and the rollover icon path, construct an ExportButton object by
     * setting its properties (width, height, icon) and its action listener
     *
     * @param dynastyChoiceTitlePanel the instance of Dynasty Choice Title Panel this button is contained in
     * @param exportButtonIconPath the path of the icon to be set
     * @param exportButtonRolloverIconName the path of the rollover icon to be set
     */
    public ExportButton(DynastyChoiceTitlePanel dynastyChoiceTitlePanel, String exportButtonIconPath, String exportButtonRolloverIconName) {
        super(exportButtonIconPath, exportButtonRolloverIconName);

        this.dynastyChoiceTitlePanel = dynastyChoiceTitlePanel;
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Window rootWindow = SwingUtilities.getWindowAncestor(dynastyChoiceTitlePanel);

        HashMap<String, PersonPageData> unserializedPeople = PersonPageData.getUrlPersonPageDataMatches();
        HashMap<String, Dynasty> unserializedDynasties     = Dynasty.getStoredDynasties();

        /*
         * Prevent the user from exporting data if there aren't at
         * least one dynasty's data in the stored dynasties HashMap
         */

        if(unserializedDynasties.isEmpty()) {
            JOptionPane.showMessageDialog(
                rootWindow,
                "There are no data ready for export, please scrape a dynasty first",
                "No export data",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        /*
         * After the export button is clicked, we start a JFileChooser instance to
         * permit the user to choose where he/she wants to save the .json export file,
         * for convenience in the view the only files shown are the other .json files
         */

        JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        FileNameExtensionFilter jsonExtensionFilter = new FileNameExtensionFilter(".json", "json");
        jFileChooser.addChoosableFileFilter(jsonExtensionFilter);
        jFileChooser.setAcceptAllFileFilterUsed(false);

        jFileChooser.setDialogTitle("Choose the export file's name and location");
        jFileChooser.setApproveButtonToolTipText("Save the export file here");

        /*
         * We give the user a suggested name for the export file based on the
         * current date and time, e.g. 'export_2022-05-22_21-15-17.json'
         */

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        jFileChooser.setSelectedFile(new File("export_" + formatter.format(date) + ".json"));

        /*
         * Once the JFileChooser is finally set up, we show the save dialog
         * to the user and wait for the approval ('Save') button to be pressed
         */

        int returnValue = jFileChooser.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jFileChooser.getSelectedFile();

            String selectedFileAbsolutePath = selectedFile.getAbsolutePath();

            JSONObject serializedData = new JSONObject();

            /*
             * We serialize the dynasties and the people' PersonPageData objects
             * inside two different children JSON Objects (which keys are "dynasties"
             * and "people")
             */

            JSONObject serializedDynasties = DynastySerializer.serializeDynastiesHashMap(unserializedDynasties);

            serializedData.put("dynasties", serializedDynasties);

            JSONObject serializedPeople = PersonPageDataSerializer.serializePersonPageDataHashMap(unserializedPeople);

            serializedData.put("people", serializedPeople);

            /*
             * Once serialization is completed, we write the serialized
             * data into the export file chosen by the user
             */

            try {
                BufferedWriter bufferedFileWriter = new BufferedWriter(
                    new FileWriter(selectedFileAbsolutePath)
                );
                bufferedFileWriter.write(serializedData.toJSONString());

                bufferedFileWriter.flush();
                bufferedFileWriter.close();

                /*
                 * For every possible outcome of the export process we'll
                 * show the user an explanatory Message Dialog
                 */

                JOptionPane.showMessageDialog(
                        rootWindow,
                        "Data of " + unserializedPeople.size()
                                + " people from " + unserializedDynasties.size()
                                + " dynasties:\n" + Dynasty.getStoredDynastiesNames()
                                + "\nSuccesfully exported to:\n'" + selectedFileAbsolutePath + "'",
                        "Successful export",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        rootWindow,
                        "There was an I/O error while trying to write the export file, " +
                                "run this jar in console with '--debug' parameter to read the " +
                                "debug messages and please report it to the developer should " +
                                "the problem persist:\n\n" + e.getMessage(),
                        "I/O error while writing export file",
                        JOptionPane.ERROR_MESSAGE
                );

                if(RomanEmperorsScraper.isDebugEnabled()) {
                    e.printStackTrace();
                }
            }
        }
    }
}