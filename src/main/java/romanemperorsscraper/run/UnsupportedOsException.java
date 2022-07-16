package romanemperorsscraper.run;

/**
 * Class encapsulating an error/exit condition that occurred while detecting
 * the Operating System the program is running in as it is an unsupported one
 * (due to not being Windows, macOS or Unix-based).
 *
 * @see OsUtils
 *
 * @author Matteo Collica
 */
public class UnsupportedOsException extends Exception {
    public UnsupportedOsException() {
        super("Unable to proceed due to an unsupported Operating System:"
                + "\n'" + System.getProperty("os.name") + "'"
                + "\nPlease try again with a Windows, macOS or Unix-based OS."
                + "\n\nIf you think this may be an error or you wish your Operating "
                + "System to be supported in the future please contact us at "
                + "romanemperorscraper@gmail.com");
    }
}
