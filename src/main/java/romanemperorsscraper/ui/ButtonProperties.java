package romanemperorsscraper.ui;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Dimension;
import java.io.IOException;

/**
 * Class defining the main properties of a button
 * (borders, size, icon variants, [...]). Other
 * classes will inherit and recall its constructor.
 *
 * @author Sara Lazzaroni
 */
public class ButtonProperties extends JButton {
    private ImageIcon icon;
    private ImageIcon rollIcon;
    private final int buttonHeight = 64;
    private final int buttonWidth  = 64;

    /**
      * Construct a ButtonProperties object by eliminating the JButton's borders,
      * setting its size and icon variants
      *
      * Please note that the given rollover icon will also be used as button disabled icon
      *
      * @param buttonIconPath the button's icon path
      * @param buttonRolloverIconPath the button's rollover icon path
      */
    public ButtonProperties(String buttonIconPath, String buttonRolloverIconPath) {
        super();

        /*
         * MISC OPTIONS
         */

        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
        setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        /*
         * ICONS
         */

        try {
            icon = new ImageIcon(ImageIO.read(ButtonProperties.class.getResourceAsStream(buttonIconPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setIcon(icon);

        try {
            rollIcon = new ImageIcon(ImageIO.read(ButtonProperties.class.getResourceAsStream(buttonRolloverIconPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setRolloverIcon(rollIcon);
        setPressedIcon(rollIcon);
        setDisabledIcon(rollIcon);
    }
}
