package romanemperorsscraper.ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class used to import True Type Fonts (.ttf ones) from files and
 * subsequently obtain them as Font objects ready to be used in text styling
 *
 * @author Matteo Collica
 */
public class FontUtils {
    private static HashMap<String, Font> importedFonts;

    /*
     * PREVENT INSTANTIATION WITH A PRIVATE CONSTRUCTOR
     */

    private FontUtils() {
        throw new java.lang.UnsupportedOperationException(
            "As a utility class, this class must not be instantiated"
        );
    }

    /*
     * CLASS UTILITY METHODS
     */

    /**
     * Given a True Type Font placed in the fonts/ directory's file name,
     * a font style and the font size, if there are no imported fonts
     * yet import them, then get the specified font's Font object already
     * derived to be in the given style and size.
     *
     * @param fontFileName the font's file name in the fonts/ directory
     * @param fontStyle the font's desired style (like Font.PLAIN or Font.BOLD)
     * @param fontSize the font's desired size
     */
    public static Font getFont(String fontFileName, int fontStyle, float fontSize) {
        if(importedFonts == null || importedFonts.isEmpty()) {
            importFonts();
        }

        return importedFonts.get(fontFileName).deriveFont(fontStyle, fontSize);
    }

    /**
     * Import some specific fonts located in the fonts/directory and update the
     * importedFonts HashMap adding the font file name as key and the file object
     * as value
     */
    public static void importFonts() {
        importedFonts = new HashMap<>();

        HashMap<String, InputStream> inputStreamHashMap = new HashMap<>();

        /*
         * Put the needed fonts in an HashMap so that we can iterate over them
         * (we'll use the file name as key and the File object as value)
         */

        inputStreamHashMap.put("RomanFont7.ttf", FontUtils.class.getResourceAsStream("/resources/fonts/RomanFont7.ttf"));
        inputStreamHashMap.put("Romanica.ttf", FontUtils.class.getResourceAsStream("/resources/fonts/Romanica.ttf"));

        /*
         * Iterate over the filled HashMap to register each
         * single font so that they're all ready for usage
         */

        Font font;

        for (Map.Entry<String, InputStream> inputStreamSet : inputStreamHashMap.entrySet()) {
            try {
                /*
                 * Get a local URL for the current font file
                 * and then use it to create its Font object
                 */

                font = Font.createFont(Font.TRUETYPE_FONT, inputStreamSet.getValue());

                /*
                 * Make the created Font object available
                 * to Font constructors and listed via
                 * Font enumeration APIs - in a few words,
                 * "install" it into the JVM so that it is
                 * available just as OS installed fonts are
                 */

                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(font);

                /*
                 * Store the created Font object in the imported fonts
                 * HashMap using the font file name as key
                 */

                importedFonts.put(inputStreamSet.getKey(), font);
            } catch (FontFormatException | IOException ignored) {}
        }
    }
}
