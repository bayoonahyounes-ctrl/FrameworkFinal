package tests;

import core.BaseTest;
import exceptions.ProductNotFoundException;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import model.Product;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductDetailsPage;
import pages.ProductsPage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Epic("Swag Labs")
@Feature("Products")
public class ProductsTest extends BaseTest {

    @Test(groups = "smoke")
    @Story("The catalogue lists every product")
    @Severity(SeverityLevel.CRITICAL)
    public void allProductsAreListed() {
        ProductsPage products = signIn();

        Assert.assertTrue(products.isLoaded(), "products page did not load");
        List<Product> catalogue = products.products();
        Assert.assertEquals(catalogue.size(), 6, "wrong number of products");
        catalogue.forEach(product ->
                Assert.assertTrue(product.price() > 0, "price was not positive: " + product));
    }

    @Test(groups = "smoke")
    @Story("Adding and removing an item updates the cart badge and the button label")
    @Severity(SeverityLevel.BLOCKER)
    public void addingAndRemovingUpdatesTheCartBadge() {
        ProductsPage products = signIn();
        Assert.assertEquals(products.cartBadge(), 0, "cart should start empty");

        products.addToCart("Sauce Labs Backpack");
        Assert.assertEquals(products.cartBadge(), 1, "badge did not show one item");
        Assert.assertTrue(products.isInCart("Sauce Labs Backpack"), "button did not turn into Remove");

        products.addToCart("Sauce Labs Bike Light");
        Assert.assertEquals(products.cartBadge(), 2, "badge did not show two items");

        products.removeFromCart("Sauce Labs Backpack");
        Assert.assertEquals(products.cartBadge(), 1, "badge did not drop back to one item");
        Assert.assertFalse(products.isInCart("Sauce Labs Backpack"), "button did not turn back into Add to cart");
    }

    @Test(groups = "regression")
    @Story("Sorting by price puts the cheapest first")
    @Severity(SeverityLevel.NORMAL)
    public void sortingByPriceLowToHigh() {
        List<Double> prices = signIn().sortBy("lohi").productPrices();

        List<Double> sorted = new ArrayList<>(prices);
        sorted.sort(Comparator.naturalOrder());
        Assert.assertEquals(prices, sorted, "products are not sorted cheapest first");
    }

    @Test(groups = "regression")
    @Story("Sorting by name Z to A reverses the catalogue")
    @Severity(SeverityLevel.MINOR)
    public void sortingByNameZtoA() {
        List<String> names = signIn().sortBy("za").productNames();

        List<String> sorted = new ArrayList<>(names);
        sorted.sort(Comparator.reverseOrder());
        Assert.assertEquals(names, sorted, "products are not in reverse alphabetical order");
    }

    @Test(groups = "regression")
    @Story("The product details page shows the same product as the catalogue")
    @Severity(SeverityLevel.CRITICAL)
    public void productDetailsMatchTheCatalogue() {
        ProductsPage products = signIn();
        Product catalogueEntry = products.products().stream()
                .filter(product -> product.name().equals("Sauce Labs Backpack"))
                .findFirst()
                .orElseThrow();

        ProductDetailsPage details = products.openProduct("Sauce Labs Backpack");

        Assert.assertEquals(details.product().name(), catalogueEntry.name(), "name did not match");
        Assert.assertEquals(details.product().price(), catalogueEntry.price(), "price did not match");
    }

    @Test(groups = "regression", expectedExceptions = ProductNotFoundException.class)
    @Story("Acting on a product that does not exist raises a clear error")
    @Severity(SeverityLevel.MINOR)
    public void unknownProductRaisesAClearError() {
        signIn().addToCart("Sauce Labs Time Machine");
    }
}
