package tichi.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * LoginPage.java — Page Object for Login
 *
 * Page Object Model (POM) keeps all element locators in one place.
 * If the app changes a button, we update only this file — not every test.
 *
 * Login flow in Tichi:
 * Step 1: Enter email → Click Continue
 * Step 2: Enter password → Click Login
 */
public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ── Element Locators ──────────────────────────────────────────────
    // These find elements on the page

    // Step 1 - Email page
    private By emailInput    = By.cssSelector("input[type='email'], input[placeholder*='email']");
    private By continueBtn   = By.cssSelector("button[type='submit']");

    // Step 2 - Password page
    private By passwordInput = By.cssSelector("input[type='password']");
    private By loginBtn      = By.xpath("//button[contains(text(),'Login')]");

    // Sign In button on landing page
    private By signInBtn     = By.xpath("//button[contains(text(),'Sign In')] | //a[contains(text(),'Sign In')]");

    // ── Constructor ───────────────────────────────────────────────────
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ── Actions ───────────────────────────────────────────────────────

    /**
     * Complete login with email and password
     * Tichi has 2-step login: email first, then password
     */
    public void login(String email, String password) {

        // Click Sign In if on landing page
        try {
            WebElement signIn = wait.until(
                ExpectedConditions.elementToBeClickable(signInBtn)
            );
            signIn.click();
            System.out.println("Clicked Sign In button");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Already on login page or Sign In not needed");
        }

        // Step 1: Enter email and click Continue
        try {
            WebElement emailField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(emailInput)
            );
            emailField.clear();
            emailField.sendKeys(email);
            System.out.println("Entered email: " + email);

            driver.findElement(continueBtn).click();
            System.out.println("Clicked Continue");
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("Email step: " + e.getMessage());
        }

        // Step 2: Enter password and click Login
        try {
            WebElement passField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(passwordInput)
            );
            passField.clear();
            passField.sendKeys(password);
            System.out.println("Entered password");

            driver.findElement(loginBtn).click();
            System.out.println("Clicked Login");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Password step: " + e.getMessage());
        }
    }

    /**
     * Check if user is logged in by looking at current URL
     */
    public boolean isLoggedIn() {
        String url = driver.getCurrentUrl();
        return url.contains("/home") || url.contains("/jobs") || url.contains("/profile");
    }
}
