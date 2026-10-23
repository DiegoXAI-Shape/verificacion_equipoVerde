package steps;

import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/** Abre un Chrome nuevo por escenario y lo cierra al final (con captura si el escenario fallo). */
public class Hooks {

    private static WebDriver driver;

    public static WebDriver driver() {
        return driver;
    }

    @Before("not @generated")
    public void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.getBoolean("headless")) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1366,900", "--incognito");
        // Evita el aviso de "contrasena filtrada" de Chrome, que tapa la pagina despues del login.
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        // Selenium Manager descarga el chromedriver que corresponde a la version de Chrome instalada.
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @After("not @generated")
    public void closeBrowser(Scenario scenario) {
        if (driver == null) {
            return;
        }
        try {
            if (scenario.isFailed()) {
                byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(png, "image/png", "pantalla al fallar");
            }
        } finally {
            driver.quit();
            driver = null;
        }
    }
}
