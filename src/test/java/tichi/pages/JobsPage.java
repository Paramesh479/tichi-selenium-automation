package tichi.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

/**
 * JobsPage.java — Page Object for Job Listings (/jobs)
 *
 * Handles all interactions on the All Posts / Job Listings page.
 * This is where users browse and search for jobs.
 */
public class JobsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ── Element Locators ──────────────────────────────────────────────

    // Page heading
    private By allPostsHeading = By.xpath("//*[contains(text(),'All Posts')]");

    // Search bar at top right
    private By searchInput = By.cssSelector("input[placeholder*='Job title'], input[placeholder*='Keyword']");

    // Filter panel items
    private By sortByFilter    = By.xpath("//*[contains(text(),'Sort By')]");
    private By tichiTypeFilter = By.xpath("//*[contains(text(),'Tichi Type')]");
    private By locationsFilter = By.xpath("//*[contains(text(),'Locations')]");

    // Job cards in the results
    private By jobCards = By.cssSelector("[class*='job'], [class*='card'], [class*='post']");

    // ── Constructor ───────────────────────────────────────────────────
    public JobsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ── Actions ───────────────────────────────────────────────────────

    /**
     * Navigate directly to the jobs page
     */
    public void goToJobsPage() {
        driver.get("https://tichi-app-webapp-stage.web.app/jobs");
        System.out.println("Navigated to /jobs page");
        try { Thread.sleep(4000); } catch (Exception e) {}
    }

    /**
     * Check if "All Posts" heading is visible
     */
    public boolean isAllPostsHeadingVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(allPostsHeading));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if search bar is visible
     */
    public boolean isSearchBarVisible() {
        try {
            return driver.findElement(searchInput).isDisplayed();
        } catch (Exception e) {
            // Try alternative search — page text check
            return driver.getPageSource().contains("Search by Job") ||
                   driver.getPageSource().contains("Job title");
        }
    }

    /**
     * Check if filter panel has Sort By, Tichi Type, Locations
     */
    public boolean isFilterPanelVisible() {
        boolean hasSortBy    = driver.getPageSource().contains("Sort By");
        boolean hasTichiType = driver.getPageSource().contains("Tichi Type");
        boolean hasLocations = driver.getPageSource().contains("Locations");

        System.out.println("Sort By: " + hasSortBy +
                           " | Tichi Type: " + hasTichiType +
                           " | Locations: " + hasLocations);

        return hasSortBy && hasTichiType && hasLocations;
    }

    /**
     * Check if job cards are showing on the page
     */
    public boolean areJobCardsDisplayed() {
        // Check for salary info which appears on job cards
        return driver.getPageSource().contains("Per Hour") ||
               driver.getPageSource().contains("Per Month") ||
               driver.getPageSource().contains("FullTime") ||
               driver.getPageSource().contains("Full Time");
    }

    /**
     * Click on the first job that matches the given title
     */
    public void clickJobByTitle(String jobTitle) {
        try {
            WebElement job = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(),'" + jobTitle + "')]")
                )
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", job);
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, -100);");
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", job);
            System.out.println("Clicked job: " + jobTitle);
            Thread.sleep(2000);
        } catch (Exception e) {
            // Debug: check if text exists anywhere on the page
            boolean inSource = driver.getPageSource().contains(jobTitle);
            System.out.println("Job title in page source: " + inSource + " | URL: " + driver.getCurrentUrl());
            System.out.println("Could not click job '" + jobTitle + "': " + e.getMessage());
        }
    }

    /**
     * Types a keyword into the search bar and waits for results to update.
     */
    public void searchFor(String keyword) {
        try {
            WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(searchInput)
            );
            input.click();
            input.clear();
            input.sendKeys(keyword);
            input.sendKeys(Keys.ENTER);
            Thread.sleep(3000); // wait for results to load
            System.out.println("Searched for: '" + keyword + "'");
        } catch (Exception e) {
            System.out.println("Search failed: " + e.getMessage());
        }
    }

    /**
     * Returns true if the page shows no job results.
     * Checks for "no results" messages OR absence of job card salary info.
     */
    public boolean hasNoResults() {
        String pageText = driver.getPageSource();

        boolean noResultsMessage = pageText.contains("No results")   ||
                                   pageText.contains("No jobs")      ||
                                   pageText.contains("no results")   ||
                                   pageText.contains("No data")      ||
                                   pageText.contains("0 result");

        boolean noSalaryInfo = !pageText.contains("Per Hour")  &&
                               !pageText.contains("Per Month") &&
                               !pageText.contains("FullTime")  &&
                               !pageText.contains("Full Time");

        System.out.println("No-results message: " + noResultsMessage +
                           " | No salary info: " + noSalaryInfo);
        return noResultsMessage || noSalaryInfo;
    }

    /**
     * Returns the title of the first job card shown in search results.
     * Used to verify whether search results are relevant.
     */
    public String getFirstResultTitle() {
        try {
            Thread.sleep(2000);
            // Job card titles are typically in elements with line-clamp or font-semibold
            String[] selectors = {
                "[class*='line-clamp']",
                "[class*='font-semibold']",
                "h2", "h3"
            };
            // Labels to skip — these are UI chrome, not job titles
            String[] skipWords = { "All Posts", "Sort By", "Tichi Type",
                                   "Locations", "Sign", "Home", "Jobs" };

            for (String selector : selectors) {
                List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                for (WebElement el : elements) {
                    String text = el.getText().trim();
                    if (text.length() < 4) continue;
                    boolean isLabel = false;
                    for (String skip : skipWords) {
                        if (text.contains(skip)) { isLabel = true; break; }
                    }
                    if (!isLabel) {
                        System.out.println("First result title found: '" + text + "'");
                        return text;
                    }
                }
            }
            return "NO_RESULT_FOUND";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Get current page URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
