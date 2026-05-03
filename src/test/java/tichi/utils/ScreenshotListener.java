package tichi.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;
import tichi.tests.BaseTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotListener.java
 *
 * A TestNG Listener that automatically takes a screenshot after every test.
 * - PASS tests → screenshot saved with PASS_ prefix
 * - FAIL tests → screenshot saved with FAIL_ prefix
 *
 * All screenshots are saved to: target/screenshots/
 *
 * Registered in testng.xml — no changes needed in test classes.
 */
public class ScreenshotListener implements ITestListener {

    private static final String SCREENSHOT_FOLDER = "target/screenshots";

    // Called automatically by TestNG when a test PASSES
    @Override
    public void onTestSuccess(ITestResult result) {
        saveScreenshot(result, "PASS");
    }

    // Called automatically by TestNG when a test FAILS
    @Override
    public void onTestFailure(ITestResult result) {
        saveScreenshot(result, "FAIL");
    }

    // Called automatically by TestNG when a test is SKIPPED
    @Override
    public void onTestSkipped(ITestResult result) {
        saveScreenshot(result, "SKIP");
    }

    /**
     * Takes a screenshot and saves it to target/screenshots/
     *
     * Filename format: STATUS_MethodName_YYYYMMDD_HHmmss.png
     * Example: FAIL_TC_JA_013_SearchRelevance_20260503_142500.png
     */
    private void saveScreenshot(ITestResult result, String status) {
        // Get the WebDriver from the test instance
        Object testInstance = result.getInstance();
        if (!(testInstance instanceof BaseTest)) return;

        WebDriver driver = ((BaseTest) testInstance).getDriver();
        if (driver == null) return;

        try {
            // Create the screenshots folder if it does not exist
            new File(SCREENSHOT_FOLDER).mkdirs();

            // Build the filename
            String methodName = result.getMethod().getMethodName();
            String timestamp  = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName   = status + "_" + methodName + "_" + timestamp + ".png";
            String filePath   = SCREENSHOT_FOLDER + "/" + fileName;

            // Take screenshot and write to file
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(Paths.get(filePath), screenshot);

            System.out.println("Screenshot saved → " + filePath);

        } catch (IOException e) {
            System.out.println("Could not save screenshot: " + e.getMessage());
        }
    }
}
