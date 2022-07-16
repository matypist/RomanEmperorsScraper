package romanemperorsscraper.run;

/**
 * This class encapsulates an error condition that occurred while trying to
 * start a WebDriver session with all the supported browsers (Firefox / Chrome
 * / Safari / Edge / Internet Explorer) because none of them worked.
 *
 * @see OsUtils
 *
 * @author Matteo Collica
 */
public class NoSupportedBrowsersException extends Exception {
    public NoSupportedBrowsersException() {
        super("There are no supported browsers (Firefox / Chrome / Edge / " +
                "Safari / Internet Explorer) in your Operating System, please " +
                "try installing one first");
    }
}
