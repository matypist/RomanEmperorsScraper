package romanemperorsscraper.run;

import com.formdev.flatlaf.FlatLightLaf;
import romanemperorsscraper.tester.Tester;
import romanemperorsscraper.ui.choice.DynastyChoiceFrame;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.awt.Image;
import java.io.IOException;

/**
 * RomanEmperorsScraper's goal is the creation of family trees relating to
 * each dynasty of Roman Emperors, with the aim of visualizing the different
 * parental relationships that exist. This is done by scraping their data or
 * importing them from pre-generated data files. This class represents where
 * the execution begins and contains, in fact, the project's main method.
 *
 * @see DynastyChoiceFrame
 * @see romanemperorsscraper.ui.tree.DynastyTreeFrame
 *
 * @author Matteo Collica
 */
public class RomanEmperorsScraper {
    private static Image appIconImage;

    private static boolean debugEnabled = false;

    /**
     * The main method begins the project's execution by detecting the
     * Operating System, setting up a custom Look and Feel for Swing and
     * starting the Dynasty Choice Frame
     *
     * @param args eventual command line arguments
     */
    public static void main(String[] args) {
        /*
         * Get and interpret any command line arguments,
         * the following is a list of supported ones:
         *
         * > --debug || -d || --verbose || -v
         *   To be used to get some additional debug
         *   information during the program's execution
         *
         * > -t
         *   To start just the tests instead of the program
         */

        if(args.length > 0) {
            for(String cliArg : args) {
                if(cliArg.equals("--debug") || cliArg.equals("-d") || cliArg.equals("--verbose") || cliArg.equals("-v")) {
                    System.out.println("Debug/verbose mode enabled");

                    debugEnabled = true;
                } else if (cliArg.equals("--test") || cliArg.equals("-t")) {
                    try {
                        Tester.main(null);
                    } catch (UnsupportedOsException | NoSupportedBrowsersException e) {
                        e.printStackTrace();
                    }

                    return;
                }
            }
        }

        /*
         * Our program was tested working on the following Operating Systems:
         * - Windows (10 / 11)
         * - macOS (Catalina 10.15.5)
         * - Unix (Pop!_OS, a GNU/Linux distribution Ubuntu-based)
         *
         * It should also work on other Windows, macOS and Unix-based Operating
         * Systems, but we (developers) don't plan to support other Operating Systems
         * like Solaris, TempleOS, Chrome OS, [...] so in these cases it just generates
         * an alert Message Dialog (if possible) and terminates the execution
         */

        try {
            OsUtils.initializeOs();
        } catch(UnsupportedOsException unsupportedOsException) {
            try {
                JOptionPane.showMessageDialog(
                        null,
                        "We're very sorry but your Operating System isn't one\n"
                                + "we plan to support at the moment, please try again\n"
                                + "with a Windows, macOS or Unix-based OS.\n\n"
                                + "If you think this may be an error or you wish your\n"
                                + "Operating System to be supported in the future\n"
                                + "please contact us at romanemperorscraper@gmail.com",
                        "Unsupported Operating System",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch(Exception ignored) {}

            System.out.println(unsupportedOsException.getMessage());

            return;
        }

        /*
         * FlatLightLaf is "a modern open-source cross-platform
         * Look and Feel for Java Swing desktop applications",
         * we use it to make our Swing components look better
         * and its setup method has to be called before creating
         * any Swing component, that's why we'll do it here:
         */

        FlatLightLaf.setup();

        /*
         * The program then starts with the Dynasty Choice Frame to
         * let the user choice one by one the dynasties he/she wants
         * to scrape or import dynasties' and emperors' data from a
         * previously generated export file
         */

        DynastyChoiceFrame.getInstance();
    }

    /**
     * Construct an AppIcon object or get the already constructed one
     *
     * @return the App Icon as an Image object
     */
    public static Image getAppIconImage() {
        if(appIconImage == null) {
            try {
                appIconImage = ImageIO.read(
                    RomanEmperorsScraper.class.getResourceAsStream("/resources/images/icons/AppIcon.png")
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return appIconImage;
    }

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }
}