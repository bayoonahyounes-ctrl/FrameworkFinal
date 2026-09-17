package tests;

import core.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import model.Product;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.CartPage;
import pages.ProductsPage;

import java.util.List;

@Epic("Swag Labs")
@Feature("Cart")
public class CartTest extends BaseTest {

    @Test(groups = "smoke")
    @Story("The cart shows the items that were added, with the right quantity")
    @Severity(SeverityLevel.BLOCKER)
    public void cartShowsTheItemsThatWereAdded() {
        ProductsPage products = signIn();
        products.addToCart("Sauce Labs Backpack");
        products.addToCart("Sauce Labs Bike Light");

        CartPage cart = products.goToCart();

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(cart.itemCount(), 2, "wrong number of rows in the cart");
        soft.assertTrue(cart.itemNames().contains("Sauce Labs Backpack"), "backpack missing from cart");
        soft.assertTrue(cart.itemNames().contains("Sauce Labs Bike Light"), "bike light missing from cart");
        soft.assertEquals(cart.quantityOf("Sauce Labs Backpack"), 1, "wrong quantity for the backpack");
        soft.assertAll();
    }

    @Test(groups = "regression")
    @Story("Cart prices match the catalogue prices")
    @Severity(SeverityLevel.CRITICAL)
    public void cartPricesMatchTheCatalogue() {
        ProductsPage products = signIn();
        Product catalogueEntry = products.products().stream()
                .filter(product -> product.name().equals("Sauce Labs Backpack"))
                .findFirst()
                .orElseThrow();

        products.addToCart("Sauce Labs Backpack");
        List<Product> cartItems = products.goToCart().items();

        Assert.assertEquals(cartItems.size(), 1, "wrong number of items in the cart");
        Assert.assertEquals(cartItems.get(0).price(), catalogueEntry.price(), "cart price did not match the catalogue");
    }

    @Test(groups = "regression")
    @Story("Removing an item updates both the cart rows and the badge")
    @Severity(SeverityLevel.CRITICAL)
    public void removingAnItemUpdatesTheCartAndTheBadge() {
        ProductsPage products = signIn();
        products.addToCart("Sauce Labs Backpack");
        products.addToCart("Sauce Labs Bike Light");
        CartPage cart = products.goToCart();

        cart.removeItem("Sauce Labs Backpack");

        Assert.assertEquals(cart.itemCount(), 1, "row was not removed");
        Assert.assertFalse(cart.itemNames().contains("Sauce Labs Backpack"), "backpack row is still present");
        Assert.assertEquals(cart.cartBadge(), 1, "badge did not drop after removal");
    }

    @Test(groups = "regression")
    @Story("Continue shopping returns to the products page without losing the cart")
    @Severity(SeverityLevel.NORMAL)
    public void continueShoppingKeepsTheCart() {
        ProductsPage products = signIn();
        products.addToCart("Sauce Labs Backpack");
        CartPage cart = products.goToCart();

        ProductsPage backOnProducts = cart.continueShopping();

        Assert.assertTrue(backOnProducts.isLoaded(), "did not return to the products page");
        Assert.assertEquals(backOnProducts.cartBadge(), 1, "cart was not kept after continuing shopping");
    }
}
