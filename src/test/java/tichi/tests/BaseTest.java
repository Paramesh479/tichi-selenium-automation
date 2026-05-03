package tichi.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * BaseTest.java
 *
 * This class sets up and tears down the browser for every test.
 * All test classes extend this class to reuse the setup.
 *
 * What it does:
 * - @BeforeClass: Opens Chrome browser ONCE before all tests in the class
 * - @AfterClass:  Closes browser ONCE after all tests are done
 */
public class BaseTest {

    // WebDriver controls the Chrome browser
    protected WebDriver driver;

    // App URL - change this if URL changes
    protected static final String BASE_URL = "https://tichi-app-webapp-stage.web.app";

    // Test credentials
    protected static final String EMAIL    = "sparamesh479@gmail.com";
    protected static final String PASSWORD = "Paramesh@0404";

    @BeforeClass
    public void openBrowser() {
        // WebDriverManager automatically downloads the correct ChromeDriver
        // No need to download manually!
        WebDriverManager.chromedriver().setup();

        // Chrome browser settings
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");           // Open full screen
        options.addArguments("--disable-notifications");     // Block popups

        // Create Chrome browser
        driver = new ChromeDriver(options);

        // Wait up to 10 seconds for elements to appear
        driver.manage().timeouts()
              .implicitlyWait(java.time.Duration.ofSeconds(10));

        // Open the Tichi app
        driver.get(BASE_URL);

        System.out.println("Browser opened: " + BASE_URL);
    }

    public WebDriver getDriver() {
        return driver;
    }

    @AfterClass
    public void closeBrowser() {
        // Close browser after every test
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed.");
        }
    }
}
