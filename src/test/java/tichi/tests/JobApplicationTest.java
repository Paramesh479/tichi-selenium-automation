package tichi.tests;

import tichi.pages.LoginPage;
import tichi.pages.JobsPage;
import tichi.pages.JobDetailPage;
import tichi.pages.MyRequestsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

/**
 * ============================================================
 * JobApplicationTest.java
 *
 * Module  : Job Application
 * Tool    : Selenium WebDriver + Java
 * Reports : TestNG (target/surefire-reports/)
 * App URL : https://tichi-app-webapp-stage.web.app
 *
 * HOW TO RUN IN ANTIGRAVITY IDE:
 *   1. Open Antigravity IDE
 *   2. File → Open → select this project folder
 *   3. Open terminal in project root
 *   4. Run: mvn test
 *   5. See results in console
 * ============================================================
 *
 * TEST CASES:
 *   TC-JA-001 : Login with valid credentials
 *   TC-JA-002 : Job listings page loads
 *   TC-JA-003 : Filter panel visible
 *   TC-JA-004 : Job cards show salary and type
 *   TC-JA-005 : Job detail page with full info
 *   TC-JA-006 : Request button visible
 *   TC-JA-007 : Apply for job (Request → Withdraw)
 *   TC-JA-008 : Applied job appears in My Requests
 *   TC-JA-009 : Application timestamp visible
 *   TC-JA-010 : Duplicate application prevented
 *   TC-JA-011 : Search bar visible on All Posts page
 *   TC-JA-012 : Invalid search shows "No data found"
 *   TC-JA-013 : Search relevance check (BUG-012 — expected FAIL)
 * ============================================================
 */
public class JobApplicationTest extends BaseTest {

    // Job we will apply to during tests
    private static final String JOB_TITLE = "Looking for Automation Tester";

    /**
     * Navigates to the job detail page whether or not it appears in All Posts.
     * If the job has already been applied and is hidden from the listing,
     * finds it via My Requests instead.
     */
    private void navigateToJobDetail() {
        JobsPage jobsPage = new JobsPage(driver);
        jobsPage.goToJobsPage();
        jobsPage.clickJobByTitle(JOB_TITLE);

        // Detail page URL has "/job" but NOT "/jobs"; listing page ends with "/jobs"
        String curUrl = driver.getCurrentUrl();
        boolean onJobDetail = curUrl.contains("/job") && !curUrl.contains("/jobs");
        if (!onJobDetail) {
            System.out.println("Job not in All Posts — trying via My Requests");
            MyRequestsPage myRequests = new MyRequestsPage(driver);
            myRequests.goToMyRequests();
            myRequests.clickJobByTitle(JOB_TITLE);
        }
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-001 : Login
    // ────────────────────────────────────────────────────────────
    @Test(priority = 1, description = "TC-JA-001: Login with valid email and password")
    public void TC_JA_001_Login() {
        System.out.println("\n[TC-JA-001] Login Test");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(EMAIL, PASSWORD);

        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("tichi"),
            "Login failed. URL: " + url);

        System.out.println("PASS - Logged in. URL: " + url);
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-002 : Job Listings Page
    // ────────────────────────────────────────────────────────────
    @Test(priority = 2, description = "TC-JA-002: All Posts page loads with job cards",
          dependsOnMethods = "TC_JA_001_Login")
    public void TC_JA_002_JobListingsPage() {
        System.out.println("\n[TC-JA-002] Job Listings Page");

        JobsPage jobsPage = new JobsPage(driver);
        jobsPage.goToJobsPage();

        Assert.assertTrue(jobsPage.isAllPostsHeadingVisible(),
            "All Posts heading not visible on /jobs page");

        System.out.println("PASS - All Posts heading visible");
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-003 : Filter Panel
    // ────────────────────────────────────────────────────────────
    @Test(priority = 3, description = "TC-JA-003: Filter panel has Sort By, Tichi Type, Locations",
          dependsOnMethods = "TC_JA_002_JobListingsPage")
    public void TC_JA_003_FilterPanel() {
        System.out.println("\n[TC-JA-003] Filter Panel");

        JobsPage jobsPage = new JobsPage(driver);
        Assert.assertTrue(jobsPage.isFilterPanelVisible(),
            "Filter panel missing options");

        System.out.println("PASS - Filter panel: Sort By, Tichi Type, Locations visible");
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-004 : Job Card Details
    // ────────────────────────────────────────────────────────────
    @Test(priority = 4, description = "TC-JA-004: Job cards show salary and job type",
          dependsOnMethods = "TC_JA_002_JobListingsPage")
    public void TC_JA_004_JobCardDetails() {
        System.out.println("\n[TC-JA-004] Job Card Details");

        JobsPage jobsPage = new JobsPage(driver);
        Assert.assertTrue(jobsPage.areJobCardsDisplayed(),
            "Job cards not showing salary or job type");

        System.out.println("PASS - Job cards show salary and type");
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-005 : Job Detail Page
    // ────────────────────────────────────────────────────────────
    @Test(priority = 5, description = "TC-JA-005: Job detail page shows full info",
          dependsOnMethods = "TC_JA_002_JobListingsPage")
    public void TC_JA_005_JobDetailPage() {
        System.out.println("\n[TC-JA-005] Job Detail Page");

        navigateToJobDetail();

        // Verify URL is on detail page
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/job"), "Not on job detail page. URL: " + url);

        // Verify all job info is present
        JobDetailPage detail = new JobDetailPage(driver);
        Assert.assertTrue(detail.hasJobDetails(),
            "Missing compensation, type, location or posted by");

        System.out.println("PASS - Job detail page loaded with all info");
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-006 : Request Button
    // ────────────────────────────────────────────────────────────
    @Test(priority = 6, description = "TC-JA-006: Request or Withdraw button visible",
          dependsOnMethods = "TC_JA_005_JobDetailPage")
    public void TC_JA_006_RequestButton() {
        System.out.println("\n[TC-JA-006] Request/Withdraw Button");

        JobDetailPage detail = new JobDetailPage(driver);
        boolean request  = detail.isRequestButtonVisible();
        boolean withdraw = detail.isWithdrawButtonVisible();

        Assert.assertTrue(request || withdraw,
            "Neither Request nor Withdraw button found");

        System.out.println("PASS - " + (request ? "Request" : "Withdraw") + " button visible");
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-007 : Apply for Job
    // ────────────────────────────────────────────────────────────
    @Test(priority = 7, description = "TC-JA-007: Click Request → button changes to Withdraw",
          dependsOnMethods = "TC_JA_006_RequestButton")
    public void TC_JA_007_ApplyForJob() {
        System.out.println("\n[TC-JA-007] Apply for Job");

        JobDetailPage detail = new JobDetailPage(driver);

        // Apply only if not already applied
        if (detail.isRequestButtonVisible()) {
            detail.clickRequestButton();
        } else {
            System.out.println("Already applied — Withdraw already showing");
        }

        // Withdraw button must be visible after applying
        Assert.assertTrue(detail.isWithdrawButtonVisible(),
            "Withdraw button not shown after applying");

        System.out.println("PASS - Application submitted. Button is now Withdraw");
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-008 : Applied Job in My Requests
    // ────────────────────────────────────────────────────────────
    @Test(priority = 8, description = "TC-JA-008: Applied job appears in My Requests",
          dependsOnMethods = "TC_JA_007_ApplyForJob")
    public void TC_JA_008_JobInMyRequests() {
        System.out.println("\n[TC-JA-008] Job in My Requests");

        MyRequestsPage myRequests = new MyRequestsPage(driver);
        myRequests.goToMyRequests();

        Assert.assertTrue(myRequests.isMyRequestsPageVisible(),
            "My Requests page did not load");

        Assert.assertTrue(myRequests.isJobInRequests(JOB_TITLE),
            "'" + JOB_TITLE + "' not found in My Requests");

        System.out.println("PASS - '" + JOB_TITLE + "' visible in My Requests");
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-009 : Application Timestamp
    // ────────────────────────────────────────────────────────────
    @Test(priority = 9, description = "TC-JA-009: Timestamp shown after applying",
          dependsOnMethods = "TC_JA_007_ApplyForJob")
    public void TC_JA_009_ApplicationTimestamp() {
        System.out.println("\n[TC-JA-009] Application Timestamp");

        navigateToJobDetail();

        JobDetailPage detail = new JobDetailPage(driver);
        Assert.assertTrue(detail.isTimestampVisible(),
            "Timestamp 'Requested at...' not visible");

        System.out.println("PASS - Timestamp visible: Requested at [time] on [date]");
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-010 : No Duplicate Application
    // ────────────────────────────────────────────────────────────
    @Test(priority = 10, description = "TC-JA-010: Cannot apply twice — Request button gone",
          dependsOnMethods = "TC_JA_007_ApplyForJob")
    public void TC_JA_010_NoDuplicateApplication() {
        System.out.println("\n[TC-JA-010] No Duplicate Application");

        // Navigate fresh to the job detail page to guarantee correct page state
        navigateToJobDetail();

        JobDetailPage detail = new JobDetailPage(driver);

        boolean requestVisible  = detail.isRequestButtonVisible();
        boolean withdrawVisible = detail.isWithdrawButtonVisible();

        // Request must be gone, Withdraw must be there
        Assert.assertFalse(requestVisible,
            "Request button still visible — duplicate apply possible");
        Assert.assertTrue(withdrawVisible,
            "Withdraw button not visible for applied job");

        System.out.println("PASS - Duplicate prevented. Only Withdraw button shown.");
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-011 : Search Bar Visible
    // ────────────────────────────────────────────────────────────
    @Test(priority = 11, description = "TC-JA-011: Search bar is visible on the All Posts page",
          dependsOnMethods = "TC_JA_002_JobListingsPage")
    public void TC_JA_011_SearchBarVisible() {
        System.out.println("\n[TC-JA-011] Search Bar Visible");

        JobsPage jobsPage = new JobsPage(driver);
        jobsPage.goToJobsPage();

        Assert.assertTrue(jobsPage.isSearchBarVisible(),
            "Search bar not found on /jobs page");

        System.out.println("PASS - Search bar is visible on the jobs page");
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-012 : Invalid Search Shows No Data Found
    // ────────────────────────────────────────────────────────────
    @Test(priority = 12, description = "TC-JA-012: Invalid keyword shows 'No data found'",
          dependsOnMethods = "TC_JA_011_SearchBarVisible")
    public void TC_JA_012_InvalidSearchNoResults() {
        System.out.println("\n[TC-JA-012] Invalid Search — No Data Found");

        // Navigate directly to the jobs page with an invalid search keyword
        driver.get(BASE_URL + "/jobs?search=xyzabc123invalidkeyword");
        try { Thread.sleep(2000); } catch (Exception e) {}

        boolean noDataFound = driver.getPageSource().contains("No data found");

        Assert.assertTrue(noDataFound,
            "Expected 'No data found' message for invalid search, but it was not shown");

        System.out.println("PASS - 'No data found' shown for invalid search term");
    }


    // ────────────────────────────────────────────────────────────
    // TC-JA-013 : Search Relevance — BUG-012 (Expected FAIL)
    // ────────────────────────────────────────────────────────────
    @Test(priority = 13, description = "TC-JA-013: Home page search 'driver' must not show irrelevant suggestions [BUG-012]",
          dependsOnMethods = "TC_JA_011_SearchBarVisible")
    public void TC_JA_013_SearchRelevance() {
        System.out.println("\n[TC-JA-013] Search Relevance — Home Page [BUG-012 — Expected FAIL]");

        // Navigate to the Home page where the search autocomplete is
        driver.get(BASE_URL + "/home");
        try { Thread.sleep(2000); } catch (Exception e) {}

        // Find the home page search input and type "driver"
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("input[type='text'], input[type='search'], input[placeholder]")
        ));
        searchInput.click();
        searchInput.clear();
        searchInput.sendKeys("driver");
        System.out.println("Typed 'driver' in home page search box");

        // Wait for autocomplete suggestions to appear
        try { Thread.sleep(2500); } catch (Exception e) {}

        // Scroll down so the full autocomplete dropdown is visible in the screenshot
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 250);");
        try { Thread.sleep(500); } catch (Exception e) {}

        boolean irrelevantResultFound = driver.getPageSource().contains("Software Test Engineer");

        System.out.println("'Software Test Engineer' in suggestions: " + irrelevantResultFound);

        // This assertion FAILS intentionally — it confirms BUG-012
        // The autocomplete should NOT suggest "Software Test Engineer" for a "driver" search
        Assert.assertFalse(irrelevantResultFound,
            "BUG-012 CONFIRMED: 'Software Test Engineer' appears in home page search suggestions for 'driver' — irrelevant result returned");
    }
}
