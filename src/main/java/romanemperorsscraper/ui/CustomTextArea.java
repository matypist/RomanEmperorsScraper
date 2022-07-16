package romanemperorsscraper.ui;

import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Class defining the main properties of a textArea
 * (text color, size, font, style, [...]). Other
 * classes will inherit and recall its constructor.
 *
 * @author Matteo Collica
 * @author Ivan Dalla Ragione
 */
public class CustomTextArea extends JTextArea {
    /**
     * Construct a CustomTextArea object by setting its size and the color,
     * font and style of the text. The text can not be changed later on.
     *
     * @param text the text of the TextArea
     * @param textColor the text color
     * @param fontFileName the text font (e.g. 'Romanica.ttf',  'RomanFont7.ttf')
     * @param fontStyle the font style (e.g. PLAIN, BOLD, [...])
     * @param fontSize the font size
     */
    public CustomTextArea(String text, Color textColor, String fontFileName, int fontStyle, float fontSize) {
        super(text);

        setOpaque(false);
        setEditable(false);

        setForeground(textColor);
        setFont(FontUtils.getFont(fontFileName, fontStyle, fontSize));

        setPreferredSize(new Dimension(230, 78));
    }
}