package steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/** Implementacion real (Selenium) de los pasos de checkout.feature. */
public class CheckoutSteps {

    private static final String BASE_URL = "https://www.saucedemo.com/";

    // Localizadores por id (SauceDemo no le pone id al mensaje de error ni a los titulos; ahi se usa data-test).
    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By INVENTORY = By.id("inventory_container");
    private static final By CART_LINK = By.id("shopping_cart_container");
    private static final By CHECKOUT = By.id("checkout");
    private static final By FIRST_NAME = By.id("first-name");
    private static final By LAST_NAME = By.id("last-name");
    private static final By POSTAL_CODE = By.id("postal-code");
    private static final By CONTINUE = By.id("continue");
    private static final By SUMMARY = By.id("checkout_summary_container");
    private static final By FINISH = By.id("finish");
    private static final By COMPLETE_HEADER = By.cssSelector("[data-test='complete-header']");
    private static final By ERROR = By.cssSelector("[data-test='error']");

    /** Zip escrito en el formulario; Cucumber crea una instancia nueva de esta clase por escenario. */
    private String zipCapturado = "";

    private WebDriver driver() {
        return Hooks.driver();
    }

    private WebDriverWait waitFor() {
        return new WebDriverWait(driver(), Duration.ofSeconds(10));
    }

    private WebElement visible(By locator) {
        return waitFor().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void click(By locator) {
        waitFor().until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void type(By locator, String text) {
        WebElement field = visible(locator);
        field.clear();
        field.sendKeys(text);
    }

    /** Gherkin recorta los espacios de las celdas de Examples, asi que algunos valores llegan como marcador. */
    private static String decode(String value) {
        return switch (value) {
            case "<vacio>" -> "";
            case "<3 espacios>" -> "   ";
            default -> value;
        };
    }

    @Given("que estoy en la pagina de login de SauceDemo")
    public void abrirLogin() {
        driver().get(BASE_URL);
        visible(LOGIN_BUTTON);
    }

    @When("inicio sesion con usuario {string} y contrasena {string}")
    public void iniciarSesion(String usuario, String contrasena) {
        type(USERNAME, usuario);
        type(PASSWORD, contrasena);
        click(LOGIN_BUTTON);
    }

    @Then("veo la pagina de productos")
    public void veoProductos() {
        visible(INVENTORY);
        assertTrue(driver().getCurrentUrl().contains("inventory.html"), "No se redirigio al inventario");
    }

    @When("agrego el producto {string} al carrito")
    public void agregarProducto(String producto) {
        // "Sauce Labs Backpack" -> id "add-to-cart-sauce-labs-backpack"
        click(By.id("add-to-cart-" + producto.toLowerCase().replace(' ', '-')));
    }

    @When("abro el carrito")
    public void abrirCarrito() {
        click(CART_LINK);
        waitFor().until(ExpectedConditions.urlContains("cart.html"));
    }

    @When("inicio el checkout")
    public void iniciarCheckout() {
        click(CHECKOUT);
        visible(FIRST_NAME);
    }

    @When("capturo First Name {string}, Last Name {string} y Zip {string}")
    public void capturarDatos(String nombre, String apellido, String zip) {
        type(FIRST_NAME, nombre);
        type(LAST_NAME, apellido);
        zipCapturado = decode(zip);
        type(POSTAL_CODE, zipCapturado);
    }

    @When("presiono Continue")
    public void presionarContinue() {
        click(CONTINUE);
    }

    @Then("veo el resumen de la orden")
    public void veoResumen() {
        visible(SUMMARY);
    }

    @When("presiono Finish")
    public void presionarFinish() {
        click(FINISH);
    }

    @Then("veo el mensaje de confirmacion {string}")
    public void veoConfirmacion(String mensaje) {
        assertEquals(mensaje, visible(COMPLETE_HEADER).getText());
    }

    @Then("veo el mensaje de error {string}")
    public void veoError(String mensaje) {
        assertEquals(mensaje, visible(ERROR).getText());
    }

    @Then("el Zip es {string}")
    public void zipEs(String resultadoEsperado) {
        // Despues de Continue pasa una de dos cosas: aparece el error o se navega al resumen (step-two).
        waitFor().until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(ERROR),
                ExpectedConditions.urlContains("checkout-step-two")));
        boolean aceptado = driver().getCurrentUrl().contains("checkout-step-two");

        // fail() en lugar de assertTrue: asi el mensaje no termina en "expected: <true> but was: <false>".
        switch (resultadoEsperado) {
            case "aceptado" -> {
                if (!aceptado) {
                    fail("Se esperaba que el Zip '" + zipCapturado + "' fuera aceptado, pero SauceDemo mostro: "
                            + visible(ERROR).getText());
                }
            }
            case "rechazado" -> {
                if (aceptado) {
                    fail("DEFECTO: SauceDemo acepto el Zip invalido '" + zipCapturado
                            + "' y avanzo a Checkout: Overview");
                }
            }
            default -> fail("Resultado esperado desconocido: " + resultadoEsperado);
        }
    }
}
