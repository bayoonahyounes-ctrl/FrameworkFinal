package tests;

import config.Config;
import core.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import model.CartSummary;
import model.Customer;
import model.Product;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CheckoutInformationPage;
import pages.CheckoutOverviewPage;
import pages.ProductsPage;

import java.util.List;

@Epic("Swag Labs")
@Feature("Checkout")
public class CheckoutTest extends BaseTest {

    @Test(dataProvider = "missingCheckoutFields", groups = "regression")
    @Story("Missing required fields are rejected with the right message")
    @Severity(SeverityLevel.CRITICAL)
    public void missingFieldsAreRejected(String firstName, String lastName, String postalCode, String expected) {
        ProductsPage products = signIn();
        products.addToCart("Sauce Labs Backpack");

        CheckoutInformationPage checkoutInfo = products.goToCart().checkout();
        checkoutInfo = checkoutInfo.continueExpectingFailure(firstName, lastName, postalCode);

        Assert.assertTrue(checkoutInfo.errorMessage().contains(expected),
                "wrong error for first='" + firstName + "' last='" + lastName + "' postal='" + postalCode + "': "
                        + checkoutInfo.errorMessage());
    }

    @DataProvider(name = "missingCheckoutFields")
    public Object[][] missingCheckoutFields() {
        return new Object[][]{
                {"", "Doe", "12345", "First Name is required"},
                {"Jane", "", "12345", "Last Name is required"},
                {"Jane", "Doe", "", "Postal Code is required"},
        };
    }

    @Test(groups = "smoke")
    @Story("The overview totals match a total calculated from the catalogue, in Java")
    @Severity(SeverityLevel.BLOCKER)
    public void overviewTotalsAreCorrect() {
        ProductsPage products = signIn();
        List<Product> catalogue = products.products();
        Product backpack = findByName(catalogue, "Sauce Labs Backpack");
        Product bikeLight = findByName(catalogue, "Sauce Labs Bike Light");

        products.addToCart(backpack.name());
        products.addToCart(bikeLight.name());

        CheckoutOverviewPage overview = products.goToCart().checkout()
                .continueWith(new Customer("Jane", "Doe", "12345"));

        CartSummary expected = new CartSummary(List.of(backpack, bikeLight), Config.taxRate());

        Assert.assertEquals(overview.displayedItemTotal(), expected.itemTotal(), "item total did not match");
        Assert.assertEquals(overview.displayedTax(), expected.tax(), "tax did not match");
        Assert.assertEquals(overview.displayedTotal(), expected.total(), "total did not match");
    }

    @Test(groups = "regression")
    @Story("Cancelling checkout overview returns to the products page and keeps the cart")
    @Severity(SeverityLevel.NORMAL)
    public void cancellingTheOrderReturnsToTheProducts() {
        ProductsPage products = signIn();
        products.addToCart("Sauce Labs Backpack");

        ProductsPage backOnProducts = products.goToCart().checkout()
                .continueWith(new Customer("Jane", "Doe", "12345"))
                .cancel();

        Assert.assertTrue(backOnProducts.isLoaded(), "did not return to the products page");
        Assert.assertEquals(backOnProducts.cartBadge(), 1, "cart was not kept after cancelling");
    }

    @Test(groups = "regression")
    @Story("Finishing the order shows the confirmation and empties the cart")
    @Severity(SeverityLevel.CRITICAL)
    public void finishingTheOrderConfirmsIt() {
        ProductsPage products = signIn();
        products.addToCart("Sauce Labs Backpack");

        var confirmation = products.goToCart().checkout()
                .continueWith(new Customer("Jane", "Doe", "12345"))
                .finish();

        Assert.assertTrue(confirmation.confirmationMessage().contains("Thank you for your order"),
                "wrong confirmation message: " + confirmation.confirmationMessage());
        Assert.assertEquals(confirmation.cartBadge(), 0, "cart badge should be empty after finishing the order");
    }

    private Product findByName(List<Product> products, String name) {
        return products.stream().filter(product -> product.name().equals(name)).findFirst().orElseThrow();
    }
}
