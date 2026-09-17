package pages;

import io.qameta.allure.Step;
import model.Customer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutInformationPage extends BasePage {

    private static final By CONTAINER = By.id("checkout_info_container");
    private static final By FIRST_NAME = By.id("first-name");
    private static final By LAST_NAME = By.id("last-name");
    private static final By POSTAL_CODE = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By CANCEL_BUTTON = By.id("cancel");
    private static final By ERROR = By.cssSelector("h3[data-test='error']");

    public CheckoutInformationPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By openIndicator() {
        return CONTAINER;
    }

    @Step("Fill in checkout information for {0}")
    public CheckoutOverviewPage continueWith(Customer customer) {
        fill(customer.firstName(), customer.lastName(), customer.postalCode());
        click(CONTINUE_BUTTON);
        return new CheckoutOverviewPage(driver);
    }

    @Step("Try to continue checkout with first={0}, last={1}, postal={2}")
    public CheckoutInformationPage continueExpectingFailure(String firstName, String lastName, String postalCode) {
        fill(firstName, lastName, postalCode);
        click(CONTINUE_BUTTON);
        return this;
    }

    @Step("Read the checkout error message")
    public String errorMessage() {
        return textOf(ERROR);
    }

    @Step("Cancel checkout and return to the cart")
    public CartPage cancel() {
        click(CANCEL_BUTTON);
        return new CartPage(driver);
    }

    private void fill(String firstName, String lastName, String postalCode) {
        type(FIRST_NAME, firstName);
        type(LAST_NAME, lastName);
        type(POSTAL_CODE, postalCode);
    }
}
