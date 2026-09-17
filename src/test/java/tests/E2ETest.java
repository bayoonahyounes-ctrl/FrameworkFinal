package tests;

import core.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import model.Customer;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutCompletePage;
import pages.ProductsPage;
import utils.CsvReader;

@Epic("Swag Labs")
@Feature("End to end purchase")
public class E2ETest extends BaseTest {

    private static final String PRODUCT = "Sauce Labs Backpack";

    @Test(dataProvider = "customers", groups = "e2e")
    @Story("A customer can complete a purchase, start to finish")
    @Severity(SeverityLevel.BLOCKER)
    public void customerCanCompleteAPurchase(Customer customer) {
        ProductsPage products = signIn();
        products.addToCart(PRODUCT);

        CheckoutCompletePage confirmation = products.goToCart()
                .checkout()
                .continueWith(customer)
                .finish();

        Assert.assertTrue(confirmation.confirmationMessage().contains("Thank you for your order"),
                "order was not confirmed for " + customer);
        Assert.assertEquals(confirmation.cartBadge(), 0, "cart was not emptied for " + customer);
    }

    @DataProvider(name = "customers")
    public Object[][] customers() {
        return CsvReader.readCustomers("testdata/customers.csv").stream()
                .map(customer -> new Object[]{customer})
                .toArray(Object[][]::new);
    }

    @Test(groups = "e2e")
    @Story("Cancelling halfway through checkout leaves the basket untouched")
    @Severity(SeverityLevel.CRITICAL)
    public void cancellingHalfwayKeepsTheBasket() {
        ProductsPage products = signIn();
        products.addToCart(PRODUCT);
        products.addToCart("Sauce Labs Bike Light");

        CartPage cart = products.goToCart()
                .checkout()
                .cancel();

        Assert.assertEquals(cart.itemCount(), 2, "basket lost items after cancelling checkout");
        Assert.assertTrue(cart.itemNames().contains(PRODUCT), "backpack missing after cancelling");
        Assert.assertTrue(cart.itemNames().contains("Sauce Labs Bike Light"), "bike light missing after cancelling");
    }
}
