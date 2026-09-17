package tests;

import config.Config;
import core.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.ProductsPage;

@Epic("Swag Labs")
@Feature("Login")
public class LoginTest extends BaseTest {

    @Test(groups = "smoke")
    @Story("A valid user reaches the products page")
    @Severity(SeverityLevel.BLOCKER)
    @Description("standard_user signs in and lands on /inventory.html with heading Products")
    public void standardUserCanLogIn() {
        ProductsPage products = signIn();

        Assert.assertTrue(products.currentUrl().contains("/inventory.html"),
                "did not land on the inventory page: " + products.currentUrl());
        Assert.assertEquals(products.header(), "Products", "wrong heading on the products page");
    }

    @Test(dataProvider = "rejectedLogins", groups = "regression")
    @Story("Bad credentials are rejected with the right message")
    @Severity(SeverityLevel.CRITICAL)
    public void rejectedLoginShowsTheRightError(String user, String password, String expected) {
        LoginPage login = openLoginPage().loginExpectingFailure(user, password);

        Assert.assertTrue(login.errorMessage().contains(expected),
                "wrong error for '" + user + "': " + login.errorMessage());
    }

    @DataProvider(name = "rejectedLogins")
    public Object[][] rejectedLogins() {
        return new Object[][]{
                {Config.get("locked.user"), Config.get("password"), "locked out"},
                {Config.get("standard.user"), "wrong_password", "do not match"},
                {"no_such_user", Config.get("password"), "do not match"},
                {"", "", "Username is required"},
                {Config.get("standard.user"), "", "Password is required"},
        };
    }

    @Test(groups = "regression")
    @Story("The login page renders all of its parts")
    @Severity(SeverityLevel.NORMAL)
    public void loginPageRendersCorrectly() {
        LoginPage login = openLoginPage();

        SoftAssert soft = new SoftAssert();
        soft.assertTrue(login.isUsernameVisible(), "username field missing");
        soft.assertTrue(login.isPasswordVisible(), "password field missing");
        soft.assertTrue(login.isLoginButtonVisible(), "login button missing");
        soft.assertTrue(login.isLoaded(), "logo missing");
        soft.assertAll();
    }

    @Test(dependsOnMethods = "standardUserCanLogIn", groups = "regression")
    @Story("A signed-in user can sign out again")
    @Severity(SeverityLevel.CRITICAL)
    public void userCanLogOut() {
        LoginPage login = signIn().logout();

        Assert.assertTrue(login.isLoaded(), "did not return to the login page");
    }
}
