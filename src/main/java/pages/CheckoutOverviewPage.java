package pages;

import io.qameta.allure.Step;
import model.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.PriceUtils;

import java.util.ArrayList;
import java.util.List;

public class CheckoutOverviewPage extends BasePage {

    private static final By CONTAINER = By.id("checkout_summary_container");
    private static final By ITEM = By.className("cart_item");
    private static final By ITEM_NAME = By.className("inventory_item_name");
    private static final By ITEM_PRICE = By.className("inventory_item_price");
    private static final By SUBTOTAL_LABEL = By.className("summary_subtotal_label");
    private static final By TAX_LABEL = By.className("summary_tax_label");
    private static final By TOTAL_LABEL = By.className("summary_total_label");
    private static final By FINISH_BUTTON = By.id("finish");
    private static final By CANCEL_BUTTON = By.id("cancel");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By openIndicator() {
        return CONTAINER;
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

    /** Item total as displayed on the page, e.g. "Item total: $29.99" -> 29.99. */
    public double displayedItemTotal() {
        return PriceUtils.parse(afterColon(textOf(SUBTOTAL_LABEL)));
    }

    public double displayedTax() {
        return PriceUtils.parse(afterColon(textOf(TAX_LABEL)));
    }

    public double displayedTotal() {
        return PriceUtils.parse(afterColon(textOf(TOTAL_LABEL)));
    }

    @Step("Finish the order")
    public CheckoutCompletePage finish() {
        click(FINISH_BUTTON);
        return new CheckoutCompletePage(driver);
    }

    @Step("Cancel checkout and return to the products page")
    public ProductsPage cancel() {
        click(CANCEL_BUTTON);
        return new ProductsPage(driver);
    }

    private String afterColon(String label) {
        int colon = label.indexOf(':');
        return colon == -1 ? label : label.substring(colon + 1);
    }
}
