package tichi.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * JobDetailPage.java — Page Object for Job Detail Page (/job?jobId=...)
 *
 * This page shows full job information and has the Request/Withdraw button.
 * Users apply for jobs by clicking the Request button here.
 */
public class JobDetailPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ── Element Locators ──────────────────────────────────────────────

    // Request button (purple) — visible BEFORE applying
    private By requestBtn  = By.xpath("//button[contains(text(),'Request')]");

    // Withdraw button (red) — visible AFTER applying
    private By withdrawBtn = By.xpath("//button[contains(text(),'Withdraw')]");

    // Job info fields
    private By jobTitle        = By.xpath("//h1 | //*[contains(@class,'title')]");
    private By compensationInfo = By.xpath("//*[contains(text(),'Per Hour')] | //*[contains(text(),'Per Month')]");
    private By jobTypeInfo     = By.xpath("//*[contains(text(),'Full Time')] | //*[contains(text(),'FullTime')]");
    private By locationInfo    = By.xpath("//*[contains(text(),'Tamil Nadu')] | //*[contains(text(),'India')]");
    private By postedBy        = By.xpath("//*[contains(text(),'Posted By')]");

    // ── Constructor ───────────────────────────────────────────────────
    public JobDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ── Actions ───────────────────────────────────────────────────────

    /**
     * Check if Request button is visible (not yet applied)
     */
    public boolean isRequestButtonVisible() {
        try {
            WebElement btn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(requestBtn)
            );
            return btn.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if Withdraw button is visible (already applied)
     */
    public boolean isWithdrawButtonVisible() {
        try {
            WebElement btn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(withdrawBtn)
            );
            return btn.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click the Request button to apply for the job
     */
    public void clickRequestButton() {
        try {
            WebElement btn = wait.until(
                ExpectedConditions.elementToBeClickable(requestBtn)
            );
            btn.click();
            System.out.println("Clicked Request button — Application submitted!");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Could not click Request button: " + e.getMessage());
        }
    }

    /**
     * Check if job detail page has all required information
     * - Compensation, Job Type, Location, Posted By
     */
    public boolean hasJobDetails() {
        String pageText = driver.getPageSource();

        boolean hasCompensation = pageText.contains("Per Hour") || pageText.contains("Per Month");
        boolean hasJobType      = pageText.contains("Full Time") || pageText.contains("FullTime");
        boolean hasLocation     = pageText.contains("Tamil Nadu") || pageText.contains("India");
        boolean hasPostedBy     = pageText.contains("Posted By");

        System.out.println("Compensation: " + hasCompensation +
                           " | Job Type: " + hasJobType +
                           " | Location: " + hasLocation +
                           " | Posted By: " + hasPostedBy);

        return hasCompensation && hasJobType && hasLocation && hasPostedBy;
    }

    /**
     * Check if the application timestamp is shown
     * (Appears below Withdraw button after applying: "Requested at X:XX on DD MMM YYYY")
     */
    public boolean isTimestampVisible() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(),'Requested at') or contains(text(),'Requested At')]")
            ));
            return true;
        } catch (Exception e) {
            return driver.getPageSource().contains("Requested at") ||
                   driver.getPageSource().contains("Requested At");
        }
    }

    /**
     * Get current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
