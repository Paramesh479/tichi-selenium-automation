package tichi.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * MyRequestsPage.java — Page Object for My Requests (/profile?tabid=requests)
 *
 * This page shows all jobs the user has applied for.
 * After applying, the job should appear here.
 */
public class MyRequestsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ── Element Locators ──────────────────────────────────────────────

    // Page heading
    private By myRequestsHeading = By.xpath("//*[contains(text(),'My Requests')]");

    // Search bar in My Requests
    private By searchBar = By.cssSelector("input[placeholder*='Job title'], input[placeholder*='Keyword']");

    // ── Constructor ───────────────────────────────────────────────────
    public MyRequestsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ── Actions ───────────────────────────────────────────────────────

    /**
     * Navigate directly to My Requests page
     */
    public void goToMyRequests() {
        driver.get("https://tichi-app-webapp-stage.web.app/profile?tabid=requests");
        System.out.println("Navigated to My Requests page");
        try { Thread.sleep(2000); } catch (Exception e) {}
    }

    /**
     * Check if My Requests heading is visible
     */
    public boolean isMyRequestsPageVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(myRequestsHeading));
            return true;
        } catch (Exception e) {
            return driver.getPageSource().contains("My Requests");
        }
    }

    /**
     * Check if a specific job title appears in My Requests
     */
    public boolean isJobInRequests(String jobTitle) {
        boolean found = driver.getPageSource().contains(jobTitle);
        System.out.println("Job '" + jobTitle + "' in My Requests: " + found);
        return found;
    }

    /**
     * Click on a job card in My Requests by title
     */
    public void clickJobByTitle(String jobTitle) {
        try {
            WebElement job = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(),'" + jobTitle + "')]")
                )
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", job);
            // Scroll back slightly so sticky navbar doesn't intercept the click
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, -100);");
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", job);
            System.out.println("Clicked job in My Requests: " + jobTitle);
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Could not click job in My Requests '" + jobTitle + "': " + e.getMessage());
        }
    }

    /**
     * Check if search bar is available
     */
    public boolean isSearchBarVisible() {
        try {
            return driver.findElement(searchBar).isDisplayed();
        } catch (Exception e) {
            return driver.getPageSource().contains("Job title") ||
                   driver.getPageSource().contains("Keyword");
        }
    }
}
