package romanemperorsscraper.run;

import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class providing methods that should exhibit
 * a different behavior depending on the Operating System
 * the program is running in, like setting and getting a
 * WebDriver session for scraping or the OS detection itself.
 *
 * @see UnsupportedOsException
 * @see NoSupportedBrowsersException
 *
 * @author Matteo Collica
 */
public class OsUtils {
    /*
     * ATTRIBUTES
     */

    private static final String TEMP_FILES_PATH = System.getProperty("java.io.tmpdir");

    private static final String DRIVERS_BASE_PATH = "/resources/drivers/";

    private static final String OS = System.getProperty("os.name").toLowerCase();

    private static SupportedOs OSTYPE;

    public enum SupportedOs {
        WINDOWS,
        LINUX,
        MACOS
    }

    private static SupportedBrowser BROWSER;

    public enum SupportedBrowser {
        FIREFOX,
        CHROME,
        SAFARI,
        EDGE,
        IE
    }

    /*
     * PREVENT INSTANTIATION WITH A PRIVATE CONSTRUCTOR
     */

    private OsUtils() {
        throw new java.lang.UnsupportedOperationException(
            "As a utility class, this class must not be instantiated"
        );
    }

    /*
     * CLASS UTILITY METHODS
     */

    /**
     * Create and get a new WebDriver instance based on the user's first supported
     * browser found, with a Firefox {@literal >} Chrome {@literal >} Edge {@literal >} (Safari / IE) priority;<br>
     * <br>
     * Set the drivers' path if they're needed and were not already set.
     *
     * @return the newly created WebDriver instance.
     * @throws NoSupportedBrowsersException if no supported browser is installed
     * or functioning with Selenium in the user's operating system.
     */
    public static WebDriver getWebDriver() throws NoSupportedBrowsersException {
        WebDriver webDriver;

        String messageInitialPart = "Detected ";
        String messageEndingPart = ", scraping will be performed on it during this execution";

        /*
         * Whether if a supported browser was already found in a previous call
         * of this method or not, we'll search one or create a session with
         * the one already tested working.
         */

        if(BROWSER == null) {
            /*
             * We'll try creating a new session for each browser supported by Selenium
             * and the Operating System the program is running in:
             *
             * - Firefox, Chrome & Edge on Windows, macOS and Unix-based OS;
             * - Safari only on macOS;
             * - IE only on Windows.
             *
             * If it doesn't work out a NoSupportedBrowsersException will be thrown.
             */

            try {
                setWebDriverSystemPath(SupportedBrowser.FIREFOX);

                webDriver = new FirefoxDriver();

                BROWSER = SupportedBrowser.FIREFOX;

                System.out.println(messageInitialPart + "Firefox Browser" + messageEndingPart);
            } catch (SessionNotCreatedException sessionNotCreatedException1) {
                try {
                    setWebDriverSystemPath(SupportedBrowser.CHROME);

                    webDriver = new ChromeDriver();

                    BROWSER = SupportedBrowser.CHROME;

                    System.out.println(messageInitialPart + "Chrome Browser" + messageEndingPart);
                } catch (SessionNotCreatedException sessionNotCreatedException2) {
                    try {
                        setWebDriverSystemPath(SupportedBrowser.EDGE);

                        webDriver = new EdgeDriver();

                        BROWSER = SupportedBrowser.EDGE;

                        System.out.println(messageInitialPart + "Edge Browser" + messageEndingPart);
                    } catch (SessionNotCreatedException sessionNotCreatedException3) {
                        if (OSTYPE == SupportedOs.WINDOWS) {
                            try {
                                setWebDriverSystemPath(SupportedBrowser.IE);

                                webDriver = new InternetExplorerDriver();

                                BROWSER = SupportedBrowser.IE;

                                System.out.println(messageInitialPart + "Internet Explorer Browser" + messageEndingPart);
                            } catch (SessionNotCreatedException sessionNotCreatedException4) {
                                throw new NoSupportedBrowsersException();
                            }
                        } else if (OSTYPE == SupportedOs.MACOS) {
                            try {
                                setWebDriverSystemPath(SupportedBrowser.SAFARI);

                                webDriver = new SafariDriver();

                                BROWSER = SupportedBrowser.SAFARI;

                                System.out.println(messageInitialPart + "Safari Browser" + messageEndingPart);
                            } catch (SessionNotCreatedException sessionNotCreatedException4) {
                                throw new NoSupportedBrowsersException();
                            }
                        } else {
                            throw new NoSupportedBrowsersException();
                        }
                    }
                }
            }
        } else {
            try {
                switch(BROWSER) {
                    case FIREFOX:
                        webDriver = new FirefoxDriver();

                        break;

                    case CHROME:
                        webDriver = new ChromeDriver();
                        break;

                    case SAFARI:
                        webDriver = new SafariDriver();
                        break;

                    case EDGE:
                        webDriver = new EdgeDriver();
                        break;

                    case IE:
                        webDriver = new InternetExplorerDriver();

                        break;

                    default:
                        throw new NoSupportedBrowsersException();
                }
            } catch(SessionNotCreatedException ex) {
                BROWSER = null;

                return getWebDriver();
            }
        }

        return webDriver;
    }

    /**
     * Given a browser, set the corresponding driver path System Property
     * (required by Selenium to get the WebDriver's instances working)
     * differently based on the user's Operating System
     *
     * @param TARGET_BROWSER the browser the driver path should be set for
     */
    public static void setWebDriverSystemPath(SupportedBrowser TARGET_BROWSER) {
        String webDriverFileNameSuffix;

        /*
         * We'll dynamically build some local paths like the following ones:
         *
         * - src/main/resources/drivers/gecko/geckodriver.linux
         * - src/main/resources/drivers/chrome/chromedriver-macos
         * - src/main/resources/drivers/edge/edgedriver.exe
         *
         * Depending on the user's operating system and tested working supported browser.
         */

        switch(OSTYPE) {
            case WINDOWS:
                webDriverFileNameSuffix = ".exe";

                break;

            case MACOS:
                webDriverFileNameSuffix = "-macos";

                break;

            case LINUX:
                webDriverFileNameSuffix = "-linux";

                break;

            default:
                return;
        }

        String webDriverName;

        switch(TARGET_BROWSER) {
            case FIREFOX:
                webDriverName = "gecko";

                break;

            case CHROME:
                webDriverName = "chrome";

                break;

            case EDGE:
                webDriverName = "edge";

                break;

            case IE:
                webDriverName = "ie";

                break;

            /*
             * For Safari on macOS the WebDriver is built-in, so
             * a System Property with its path should not be set
             */

            case SAFARI:
            default:
                return;
        }

        /*
         * Extract the driver binary file (eventually contained inside the JAR file)
         * in the system temporary folder so that the corresponding system property
         * can be correctly set
         */

        String driverFileName = webDriverName + "driver" + webDriverFileNameSuffix;

        String sourcePath = DRIVERS_BASE_PATH + webDriverName + "/"
                + webDriverName + "driver" + webDriverFileNameSuffix;

        InputStream driverFileStream = OsUtils.class.getResourceAsStream(sourcePath);

        String destinationPath = TEMP_FILES_PATH + "/" + driverFileName;

        if(RomanEmperorsScraper.isDebugEnabled()) {
            System.out.println("Extracting the '" + driverFileName + "' binary file to " + destinationPath);
        }

        copyFile(driverFileStream, destinationPath);

        System.setProperty("webdriver." + webDriverName + ".driver", destinationPath);
    }

    /**
     * Write input data to a specific destination file and set it as executable
     *
     * @param source the input data
     * @param destination the destination path of the file the input data should be written into
     */
    public static void copyFile(InputStream source, String destination) {
        try {
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);

            new File(destination).setExecutable(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Detect and store the user's Operating System
     * type needed for the OsUtils class' methods
     *
     * @throws UnsupportedOsException if the Operating System the user is running
     * this program in isn't Windows, macOS or Unix-based.
     */
    public static void initializeOs() throws UnsupportedOsException {
        if (isWindows()) {
            OSTYPE = SupportedOs.WINDOWS;
        } else if (isMac()) {
            OSTYPE = SupportedOs.MACOS;
        } else if (isUnix()) {
            OSTYPE = SupportedOs.LINUX;
        } else {
            throw new UnsupportedOsException();
        }
    }

    /**
     * @return true if the Operating System name stored in the OS
     * class field implies a Windows-based one, false otherwise
     */
    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    /**
     * @return true if the Operating System name stored in the OS
     * class field implies a macOS-based one, false otherwise
     */
    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    /**
     * @return true if the Operating System name stored in the OS
     * class field implies a Unix-based one, false otherwise
     */
    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }
}
