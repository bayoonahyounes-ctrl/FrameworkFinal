package pages;

import exceptions.ProductNotFoundException;
import io.qameta.allure.Step;
import model.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.PriceUtils;

import java.util.ArrayList;
import java.util.List;

public class CartPage extends BasePage {

    private static final By CART_CONTENTS = By.id("cart_contents_container");
    private static final By ITEM = By.className("cart_item");
    private static final By ITEM_NAME = By.className("inventory_item_name");
    private static final By ITEM_PRICE = By.className("inventory_item_price");
    private static final By ITEM_QUANTITY = By.className("cart_quantity");
    private static final By ITEM_REMOVE_BUTTON = By.tagName("button");
    private static final By CHECKOUT_BUTTON = By.id("checkout");
    private static final By CONTINUE_SHOPPING_BUTTON = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By openIndicator() {
        return CART_CONTENTS;
    }

    public int itemCount() {
        return findAll(ITEM).size();
    }

    public List<String> itemNames() {
        return findAll(ITEM).stream().map(item -> item.findElement(ITEM_NAME).getText().trim()).toList();
    }

    public List<Product> items() {
        List<Product> products = new ArrayList<>();
        for (WebElement item : findAll(ITEM)) {
            String name = item.findElement(ITEM_NAME).getText().trim();
            double price = PriceUtils.parse(item.findElement(ITEM_PRICE).getText());
            products.add(new Product(name, price, ""));
        }
        return products;
    }

    public int quantityOf(String productName) {
        return Integer.parseInt(itemRow(productName).findElement(ITEM_QUANTITY).getText().trim());
    }

    @Step("Remove {0} from the cart")
    public CartPage removeItem(String productName) {
        itemRow(productName).findElement(ITEM_REMOVE_BUTTON).click();
        return this;
    }

    @Step("Continue shopping")
    public ProductsPage continueShopping() {
        click(CONTINUE_SHOPPING_BUTTON);
        return new ProductsPage(driver);
    }

    @Step("Go to checkout")
    public CheckoutInformationPage checkout() {
        click(CHECKOUT_BUTTON);
        return new CheckoutInformationPage(driver);
    }

    private WebElement itemRow(String productName) {
        for (WebElement item : findAll(ITEM)) {
            if (item.findElement(ITEM_NAME).getText().trim().equals(productName)) {
                return item;
            }
        }
        throw new ProductNotFoundException(productName, itemNames());
    }
}
