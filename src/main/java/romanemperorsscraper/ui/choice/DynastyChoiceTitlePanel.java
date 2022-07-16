package romanemperorsscraper.ui.choice;

import romanemperorsscraper.ui.ButtonProperties;
import romanemperorsscraper.ui.CustomTextArea;
import romanemperorsscraper.ui.FontUtils;

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
import java.awt.GraphicsEnvironment;

/**
 * Class extending JPanel to represent a
 * panel used to contain the title, the
 * ImportButton and the ExportButton
 * placed inside the DynastyChoiceFrame.
 *
 * @see DynastyChoiceFrame
 * @see DynastyChoiceBodyPanel
 * @see ImportButton
 * @see ExportButton
 *
 * @author Sara Lazzaroni
 * @author Ivan Dalla Ragione
 */
public class DynastyChoiceTitlePanel extends JPanel {
    private final JLabel titleLabel;

    private ImportButton importButton;
    private CustomTextArea importButtonTextArea;

    private ExportButton exportButton;
    private CustomTextArea exportButtonTextArea;

    /**
     * Create a panel containing the DynastyChoiceFrame's title,
     * the import button and the export button
     */
    public DynastyChoiceTitlePanel() {
        super();

        /*
         * OPTIONS
         */

        setLayout(new BorderLayout());
        setOpaque(false);

        /* Create the title label */

        String titleText = "CHOOSE A DYNASTY";
        titleLabel = new JLabel(titleText);
        titleLabel.setFont(FontUtils.getFont("RomanFont7.ttf", Font.BOLD, 70f));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0,  0));

        /* Create a container with the ImportButton and its descriptive label */

        String importButtonIconPath         = "/resources/images/icons/choice/ImportButton.png";
        String importButtonRolloverIconPath = "/resources/images/icons/choice/ImportButton_roll.png";

        importButton = new ImportButton(this, importButtonIconPath, importButtonRolloverIconPath);
        importButtonTextArea = new CustomTextArea("Import data", Color.BLACK, "RomanFont7.ttf", Font.PLAIN, 20f);

        Container importContainer = this.createImportExportContainer(importButton, importButtonTextArea);

        /* Create a container with the ExportButton and its descriptive label */

        String exportButtonIconPath         = "/resources/images/icons/choice/ExportButton.png";
        String exportButtonRolloverIconPath = "/resources/images/icons/choice/ExportButton_roll.png";

        exportButton = new ExportButton(this, exportButtonIconPath, exportButtonRolloverIconPath);
        exportButtonTextArea = new CustomTextArea("Export data", Color.BLACK, "RomanFont7.ttf", Font.PLAIN, 20f);

        Container exportContainer = this.createImportExportContainer(exportButton, exportButtonTextArea);

        /* Add the created elements to the panel */

        add(titleLabel,      BorderLayout.CENTER);
        add(importContainer, BorderLayout.WEST);
        add(exportContainer, BorderLayout.EAST);

        /* Set the panel's preferred size based on the screen width */

        setPreferredSize(new Dimension((int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth(),150));
    }

    /**
     * Given a ButtonProperties button and its descriptive CustomTextArea,
     * create a container which groups them
     *
     * @param button the ButtonProperties object
     * @param textArea the CustomTextArea object
     *
     * @return the created Container
     */
    public Container createImportExportContainer(ButtonProperties button, CustomTextArea textArea){
        Container container = new Container();
        FlowLayout flowLayout = new FlowLayout();
        container.setLayout(flowLayout);

        /* Distance the container from the top of the frame */
        flowLayout.setVgap(10);

        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(125, 50));

        /* Align textArea to the button */
        textArea.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        /* Add the button and the descriptive textArea to the container */
        container.add(button);
        container.add(textArea);

        container.setPreferredSize(new Dimension(250, 200));

        return container;
    }
}