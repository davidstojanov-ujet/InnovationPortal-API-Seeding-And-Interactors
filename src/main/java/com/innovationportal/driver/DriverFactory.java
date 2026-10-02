package com.innovationportal.driver;

import com.innovationportal.config.ConfigReader;
import java.time.Duration;
import java.util.Locale;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Creates a {@link WebDriver} from {@link ConfigReader} settings. Driver binaries are
 * resolved by Selenium Manager. No implicit wait is set: use explicit waits only.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = ConfigReader.browser().toLowerCase(Locale.ROOT);
        boolean headless = ConfigReader.headless();
        String windowSize = ConfigReader.windowSize();
        boolean acceptInsecureCerts = ConfigReader.acceptInsecureCerts();

        WebDriver driver = switch (browser) {
            case "chrome", "chromium" -> new ChromeDriver(chromeOptions(headless, windowSize, acceptInsecureCerts));
            case "edge" -> new EdgeDriver(edgeOptions(headless, windowSize, acceptInsecureCerts));
            case "firefox" -> new FirefoxDriver(firefoxOptions(headless, windowSize, acceptInsecureCerts));
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + browser + "'; use chrome, chromium, firefox or edge");
        };
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.pageLoadTimeoutSeconds()));
        return driver;
    }

    private static ChromeOptions chromeOptions(boolean headless, String windowSize, boolean acceptInsecureCerts) {
        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(acceptInsecureCerts);
        if (headless) {
            options.addArguments("--headless=new", headlessScreenInfo(windowSize));
        }
        options.addArguments("--window-size=" + windowSize, "--no-sandbox",
                "--disable-dev-shm-usage", "--disable-gpu");
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless, String windowSize, boolean acceptInsecureCerts) {
        EdgeOptions options = new EdgeOptions();
        options.setAcceptInsecureCerts(acceptInsecureCerts);
        if (headless) {
            options.addArguments("--headless=new", headlessScreenInfo(windowSize));
        }
        options.addArguments("--window-size=" + windowSize, "--no-sandbox",
                "--disable-dev-shm-usage", "--disable-gpu");
        return options;
    }

    /**
     * Headless Chromium reports an 800x600 screen whatever the window size. The portal's CSS hides all
     * content behind a "not supporting Landscape mode" overlay for landscape devices up to 898px wide,
     * so the virtual screen is set to the window size.
     */
    private static String headlessScreenInfo(String windowSize) {
        String[] size = windowSize.split(",");
        return "--screen-info={" + size[0].trim() + "x" + size[1].trim() + "}";
    }

    private static FirefoxOptions firefoxOptions(boolean headless, String windowSize, boolean acceptInsecureCerts) {
        FirefoxOptions options = new FirefoxOptions();
        options.setAcceptInsecureCerts(acceptInsecureCerts);
        if (headless) {
            options.addArguments("-headless");
        }
        String[] size = windowSize.split(",");
        options.addArguments("--width=" + size[0].trim(), "--height=" + size[1].trim());
        return options;
    }
}
