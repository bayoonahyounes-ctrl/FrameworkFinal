package listeners;

import config.Config;
import core.DriverFactory;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestListener implements ITestListener, IInvokedMethodListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    private static final Path SCREENSHOTS = Paths.get("target", "screenshots");

    @Override
    public void onStart(ITestContext context) {
        log.info("=== {} starting ===", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("START   : {}", name(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("PASSED  : {} ({} ms)", name(result), result.getEndMillis() - result.getStartMillis());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("FAILED  : {}", name(result));
        log.error("  reason: {}", result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("SKIPPED : {}", name(result));
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("=== {} finished ===", context.getName());
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (!method.isTestMethod() || !DriverFactory.hasDriver()) {
            return;
        }
        boolean failed = result.getStatus() == ITestResult.FAILURE;
        if (!failed && !Config.screenshotOnSuccess()) {
            return;
        }
        WebDriver driver = DriverFactory.get();
        byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        Allure.getLifecycle().addAttachment(name(result), "image/png", "png", png);
        Allure.addAttachment("page url", driver.getCurrentUrl());
        if (failed) {
            save(png, result.getName());
        }
    }

    private void save(byte[] png, String testName) {
        try {
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            Files.createDirectories(SCREENSHOTS);
            Path file = SCREENSHOTS.resolve(testName + "_" + stamp + ".png");
            Files.write(file, png);
            log.info("  shot  : {}", file.toAbsolutePath());
        } catch (Exception e) {
            log.warn("  could not save screenshot: {}", e.getMessage());
        }
    }

    private String name(ITestResult result) {
        Object[] parameters = result.getParameters();
        if (parameters.length == 0) {
            return result.getName();
        }
        return result.getName() + " [" + parameters[0] + "]";
    }
}
