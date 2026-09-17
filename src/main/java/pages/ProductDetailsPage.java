package pages;

import io.qameta.allure.Step;
import model.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.PriceUtils;

public class ProductDetailsPage extends BasePage {

    private static final By NAME = By.className("inventory_details_name");
    private static final By DESC = By.className("inventory_details_desc");
    private static final By PRICE = By.className("inventory_details_price");
    private static final By CART_BUTTON = By.className("btn_inventory");
    private static final By BACK_BUTTON = By.id("back-to-products");

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By openIndicator() {
        return NAME;
    }

    public Product product() {
        String name = textOf(NAME);
        double price = PriceUtils.parse(textOf(PRICE));
        String description = textOf(DESC);
        return new Product(name, price, description);
    }

    public boolean isInCart() {
        return "Remove".equalsIgnoreCase(textOf(CART_BUTTON));
    }

    @Step("Add this product to the cart")
    public ProductDetailsPage addToCart() {
        click(CART_BUTTON);
        return this;
    }

    @Step("Remove this product from the cart")
    public ProductDetailsPage removeFromCart() {
        click(CART_BUTTON);
        return this;
    }

    @Step("Go back to the products page")
    public ProductsPage backToProducts() {
        click(BACK_BUTTON);
        return new ProductsPage(driver);
    }
}
