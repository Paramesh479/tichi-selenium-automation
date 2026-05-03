# Tichi — Selenium Automation

Automated end-to-end tests for the **Job Application** and **Search Feature** modules of the Tichi app.

**App under test:** https://tichi-app-webapp-stage.web.app

---

## What is this project?

This project automatically opens a Chrome browser, logs into the Tichi app, and tests two modules:
- **Job Application** — browsing jobs, applying, and verifying the application
- **Search Feature** — search bar visibility, invalid keyword handling, and search relevance (includes a known bug)

It uses:
- **Selenium WebDriver** — controls the Chrome browser (clicks buttons, fills forms, reads text)
- **TestNG** — runs the tests in order and generates a report
- **Maven** — builds the project and runs everything with one command
- **WebDriverManager** — automatically downloads the correct ChromeDriver (no manual setup needed)

---

## Prerequisites

| Requirement | Version |
|-------------|---------|
| Java (JDK) | 11 or higher |
| Maven | 3.6 or higher |
| Google Chrome | Any recent version |

> ChromeDriver is downloaded **automatically** — you do not need to install it manually.

---

## How to Run

Open a terminal in the project root folder and run:

```bash
mvn test
```

That's it. Maven will:
1. Download all dependencies
2. Compile the test classes
3. Open Chrome and run all 13 tests
4. Print results in the console
5. Save an HTML report in `target/surefire-reports/`

---

## Test Cases

All 13 tests run in sequence. Each test depends on the previous one passing.

### Module 1 — Job Application (TC-JA-001 to TC-JA-010)

| Test ID | Test Name | What it checks |
|---------|-----------|----------------|
| TC-JA-001 | Login | Can log in with valid email and password |
| TC-JA-002 | Job Listings Page | The "All Posts" jobs page loads correctly |
| TC-JA-003 | Filter Panel | Sort By, Tichi Type, and Locations filters are visible |
| TC-JA-004 | Job Card Details | Job cards show salary and job type |
| TC-JA-005 | Job Detail Page | Clicking a job opens a page with full details (compensation, type, location, posted by) |
| TC-JA-006 | Request / Withdraw Button | The apply button is visible on the job detail page |
| TC-JA-007 | Apply for Job | Clicking Request changes the button to Withdraw |
| TC-JA-008 | Job in My Requests | The applied job appears in the My Requests page |
| TC-JA-009 | Application Timestamp | A "Requested at..." timestamp is shown after applying |
| TC-JA-010 | No Duplicate Application | The Request button is gone after applying — cannot apply twice |

### Module 2 — Search Feature (TC-JA-011 to TC-JA-013)

| Test ID | Test Name | What it checks | Expected Result |
|---------|-----------|----------------|-----------------|
| TC-JA-011 | Search Bar Visible | The search input is present on the All Posts page | PASS |
| TC-JA-012 | Invalid Search No Results | Searching a random keyword shows "No data found" | PASS |
| TC-JA-013 | Search Relevance | Searching "driver" must not return "Software Test Engineer" | **FAIL** (confirms BUG-012) |

> **BUG-012:** The **Home page** search autocomplete returns irrelevant suggestions. Typing `driver` in the
> home page search box shows `Software Test Engineer` (twice) in the suggestion dropdown — which has no
> connection to the keyword "driver". TC-JA-013 is intentionally written to **FAIL** — the failure is
> proof that the bug exists and has been caught by automation.

---

## Project Structure

```
tichi-selenium/
├── pom.xml                         ← Maven config (dependencies, plugins)
├── testng.xml                      ← TestNG suite config (which tests to run)
├── README.md                       ← This file
└── src/
    └── test/
        └── java/
            └── tichi/
                ├── tests/
                │   ├── BaseTest.java           ← Opens/closes the browser (shared setup)
                │   └── JobApplicationTest.java ← All 13 test cases (Job Application + Search)
                ├── pages/
                │   ├── LoginPage.java          ← Login page actions & locators
                │   ├── JobsPage.java           ← Jobs listing page actions & locators
                │   ├── JobDetailPage.java      ← Job detail page actions & locators
                │   └── MyRequestsPage.java     ← My Requests page actions & locators
                └── utils/
                    └── ScreenshotListener.java ← Auto-captures screenshots after every test
```

### Design Pattern: Page Object Model (POM)

Each page of the app has its own class under `pages/`. The page classes contain:
- **Locators** — how to find buttons, text, and inputs on that page
- **Actions** — methods like `login()`, `clickRequestButton()`, `isWithdrawButtonVisible()`

The test cases in `JobApplicationTest.java` only call these methods — they contain no raw selectors. This keeps tests clean and easy to maintain.

```
Test Case  →  calls  →  Page Object Method  →  uses  →  Selenium WebDriver
```

---

## How the Browser is Shared Across Tests

`BaseTest.java` uses `@BeforeClass` and `@AfterClass` (TestNG annotations):

- `@BeforeClass` — opens Chrome **once** before all 13 tests start
- `@AfterClass` — closes Chrome **once** after all 13 tests finish

This means all tests run in the **same browser session**, so the login from TC-JA-001 is still active when TC-JA-002, TC-JA-003, etc. run.

---

## Test Report

After running `mvn test`, open this file in a browser to see the HTML report:

```
target/surefire-reports/index.html
```

---

## Screenshots

Screenshots are taken **automatically** after every test and saved to:

```
target/screenshots/
```

**Naming format:** `STATUS_TestMethodName_YYYYMMDD_HHmmss.png`

| Prefix | Meaning |
|--------|---------|
| `PASS_` | Test passed — screenshot of the passing state |
| `FAIL_` | Test failed — screenshot of the failure state |
| `SKIP_` | Test was skipped |

**Example files after a run:**
```
target/screenshots/
├── PASS_TC_JA_001_Login_20260503_024212.png
├── PASS_TC_JA_002_JobListingsPage_20260503_024217.png
├── PASS_TC_JA_003_FilterPanel_20260503_024217.png
├── PASS_TC_JA_004_JobCardDetails_20260503_024217.png
├── PASS_TC_JA_005_JobDetailPage_20260503_024248.png
├── PASS_TC_JA_006_RequestButton_20260503_024309.png
├── PASS_TC_JA_007_ApplyForJob_20260503_024330.png
├── PASS_TC_JA_008_JobInMyRequests_20260503_024332.png
├── PASS_TC_JA_009_ApplicationTimestamp_20260503_024403.png
├── PASS_TC_JA_010_NoDuplicateApplication_20260503_024423.png
├── PASS_TC_JA_011_SearchBarVisible_20260503_024428.png
├── PASS_TC_JA_012_InvalidSearchNoResults_20260503_024431.png
└── FAIL_TC_JA_013_SearchRelevance_20260503_024433.png   ← BUG-012 evidence
```

The `FAIL_TC_JA_013_SearchRelevance_*.png` screenshot captures the browser at the exact moment BUG-012 is confirmed — showing "Software Test Engineer" appearing in the search results for "driver".

Screenshots are handled by [ScreenshotListener.java](src/test/java/tichi/utils/ScreenshotListener.java) and registered in `testng.xml`. No changes to test classes are needed.

---

## Configuration

To run against a different account or URL, update these values in [BaseTest.java](src/test/java/tichi/tests/BaseTest.java):

```java
protected static final String BASE_URL = "https://tichi-app-webapp-stage.web.app";
protected static final String EMAIL    = "sparamesh479@gmail.com";
protected static final String PASSWORD = "Paramesh@0404";
```

---

## Sample Console Output

```
[TC-JA-001] Login Test
PASS - Logged in. URL: https://tichi-app-webapp-stage.web.app/home

[TC-JA-002] Job Listings Page
PASS - All Posts heading visible

[TC-JA-003] Filter Panel
PASS - Filter panel: Sort By, Tichi Type, Locations visible

[TC-JA-004] Job Card Details
PASS - Job cards show salary and type

[TC-JA-005] Job Detail Page
PASS - Job detail page loaded with all info

[TC-JA-006] Request/Withdraw Button
PASS - Request button visible

[TC-JA-007] Apply for Job
PASS - Application submitted. Button is now Withdraw

[TC-JA-008] Job in My Requests
PASS - 'Looking for Automation Tester' visible in My Requests

[TC-JA-009] Application Timestamp
PASS - Timestamp visible: Requested at [time] on [date]

[TC-JA-010] No Duplicate Application
PASS - Duplicate prevented. Only Withdraw button shown.

[TC-JA-011] Search Bar Visible
PASS - Search bar is visible on the jobs page

[TC-JA-012] Invalid Search — No Data Found
PASS - 'No data found' shown for invalid search term

[TC-JA-013] Search Relevance [BUG-012 — Expected FAIL]
'Software Test Engineer' in results: true
FAIL - BUG-012 CONFIRMED: 'Software Test Engineer' appears in search
       results for 'driver' — irrelevant result returned

Tests run: 13, Failures: 1 (TC-JA-013 — expected), Errors: 0, Skipped: 0
BUILD FAILURE
```

> The BUILD FAILURE is expected because TC-JA-013 is designed to fail.
> It is a **bug-detection test**, not a broken test.
