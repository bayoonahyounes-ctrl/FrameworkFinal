package core;

import config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import pages.LoginPage;
import pages.ProductsPage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    private static final Path SCREENSHOTS = Paths.get("target", "screenshots");

    private long classStarted;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        log.info("SUITE > url={} headless={} timeout={}s",
                Config.baseUrl(), Config.headless(), Config.timeout().toSeconds());
        clearOldScreenshots();
    }

    @BeforeTest(alwaysRun = true)
    public void testSetup(ITestContext context) {
        log.info("TEST  > {}", context.getName());
    }

    @BeforeGroups(groups = "smoke", alwaysRun = true)
    public void smokeGroupSetup() {
        log.info("GROUP > smoke starting");
    }

    @BeforeClass(alwaysRun = true)
    public void classSetup() {
        classStarted = System.currentTimeMillis();
        log.info("CLASS > {}", getClass().getSimpleName());
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.create(Config.headless());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quit();
    }

    @AfterClass(alwaysRun = true)
    public void classTeardown() {
        log.info("CLASS < {} ({} ms)", getClass().getSimpleName(), System.currentTimeMillis() - classStarted);
    }

    @AfterGroups(groups = "smoke", alwaysRun = true)
    public void smokeGroupTeardown() {
        log.info("GROUP < smoke finished");
    }

    @AfterTest(alwaysRun = true)
    public void testTeardown(ITestContext context) {
        log.info("TEST  < {}", context.getName());
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        log.info("SUITE < results in target/allure-results, screenshots in {}, log in target/logs/test.log", SCREENSHOTS);
    }

    protected WebDriver driver() {
        return DriverFactory.get();
    }

    protected LoginPage openLoginPage() {
        return LoginPage.navigateTo(driver());
    }

    protected ProductsPage signIn() {
        return signIn(Config.get("standard.user"), Config.get("password"));
    }

    protected ProductsPage signIn(String user, String password) {
        return openLoginPage().loginAs(user, password);
    }

    private void clearOldScreenshots() {
        if (!Files.isDirectory(SCREENSHOTS)) {
            return;
        }
        try (Stream<Path> files = Files.list(SCREENSHOTS)) {
            files.filter(file -> file.toString().endsWith(".png")).forEach(file -> {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    log.warn("could not delete {}: {}", file, e.getMessage());
                }
            });
        } catch (IOException e) {
            log.warn("could not clear {}: {}", SCREENSHOTS, e.getMessage());
        }
    }
}
