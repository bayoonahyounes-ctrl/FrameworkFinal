package pages;

import exceptions.ProductNotFoundException;
import io.qameta.allure.Step;
import model.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import utils.PriceUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductsPage extends BasePage {

    private static final By INVENTORY_CONTAINER = By.id("inventory_container");
    private static final By ITEM = By.className("inventory_item");
    private static final By ITEM_NAME = By.className("inventory_item_name");
    private static final By ITEM_DESC = By.className("inventory_item_desc");
    private static final By ITEM_PRICE = By.className("inventory_item_price");
    private static final By ITEM_BUTTON = By.tagName("button");
    private static final By SORT = By.className("product_sort_container");
    private static final By CART_LINK = By.className("shopping_cart_link");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By openIndicator() {
        return INVENTORY_CONTAINER;
    }

    public int productCount() {
        return findAll(ITEM).size();
    }

    public List<String> productNames() {
        return findAll(ITEM).stream().map(item -> item.findElement(ITEM_NAME).getText().trim()).toList();
    }

    public List<Double> productPrices() {
        return findAll(ITEM).stream()
                .map(item -> PriceUtils.parse(item.findElement(ITEM_PRICE).getText()))
                .toList();
    }

    /** Every product on the page, as immutable {@link Product} values built in Java. */
    public List<Product> products() {
        List<Product> products = new ArrayList<>();
        for (WebElement item : findAll(ITEM)) {
            String name = item.findElement(ITEM_NAME).getText().trim();
            double price = PriceUtils.parse(item.findElement(ITEM_PRICE).getText());
            String description = item.findElement(ITEM_DESC).getText().trim();
            products.add(new Product(name, price, description));
        }
        return products;
    }

    @Step("Add {0} to the cart")
    public ProductsPage addToCart(String productName) {
        WebElement item = productContainer(productName);
        scrollIntoView(item);
        item.findElement(ITEM_BUTTON).click();
        return this;
    }

    @Step("Remove {0} from the cart")
    public ProductsPage removeFromCart(String productName) {
        WebElement item = productContainer(productName);
        scrollIntoView(item);
        item.findElement(ITEM_BUTTON).click();
        return this;
    }

    public boolean isInCart(String productName) {
        String label = productContainer(productName).findElement(ITEM_BUTTON).getText().trim();
        return "Remove".equalsIgnoreCase(label);
    }

    @Step("Open the product page for {0}")
    public ProductDetailsPage openProduct(String productName) {
        WebElement nameElement = productContainer(productName).findElement(ITEM_NAME);
        wait.until(ExpectedConditions.elementToBeClickable(nameElement)).click();
        return new ProductDetailsPage(driver);
    }

    @Step("Sort the products by {0}")
    public ProductsPage sortBy(String optionValue) {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(SORT));
        new Select(dropdown).selectByValue(optionValue);
        return this;
    }

    @Step("Go to the cart")
    public CartPage goToCart() {
        click(CART_LINK);
        return new CartPage(driver);
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    private WebElement productContainer(String productName) {
        for (WebElement item : findAll(ITEM)) {
            if (item.findElement(ITEM_NAME).getText().trim().equals(productName)) {
                return item;
            }
        }
        throw new ProductNotFoundException(productName, productNames());
    }
}
