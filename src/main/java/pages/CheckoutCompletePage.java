package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutCompletePage extends BasePage {

    private static final By CONTAINER = By.className("checkout_complete_container");
    private static final By HEADER = By.className("complete-header");
    private static final By BACK_HOME_BUTTON = By.id("back-to-products");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By openIndicator() {
        return CONTAINER;
    }

    @Step("Read the confirmation message")
    public String confirmationMessage() {
        return textOf(HEADER);
    }

    @Step("Return to the products page")
    public ProductsPage backToProducts() {
        click(BACK_HOME_BUTTON);
        return new ProductsPage(driver);
    }
}
