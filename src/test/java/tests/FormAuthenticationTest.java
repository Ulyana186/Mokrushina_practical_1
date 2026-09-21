package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * UI-автотесты для страницы https://the-internet.herokuapp.com/login
 * Позитивный и негативный сценарии авторизации.
 */
public class FormAuthenticationTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String LOGIN_URL = "https://the-internet.herokuapp.com/login";

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(description = "Позитивный сценарий: вход с верными логином и паролем")
    public void positiveLogin() {
        driver.get(LOGIN_URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")))
                .sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button.radius")).click();

        String flashMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("flash"))
        ).getText();

        Assert.assertTrue(
                flashMessage.contains("You logged into a secure area!"),
                "Ожидалось сообщение об успешном входе, получено: " + flashMessage
        );

        Assert.assertTrue(
                driver.findElement(By.cssSelector("a.button.secondary.radius")).isDisplayed(),
                "Кнопка Logout должна отображаться после успешного входа"
        );
    }

    @Test(description = "Негативный сценарий: вход с неверным паролем")
    public void negativeLogin_InvalidPassword() {
        driver.get(LOGIN_URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")))
                .sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        driver.findElement(By.cssSelector("button.radius")).click();

        String flashMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("flash"))
        ).getText();

        Assert.assertTrue(
                flashMessage.contains("Your password is invalid!"),
                "Ожидалось сообщение об ошибке аутентификации, получено: " + flashMessage
        );
    }
}