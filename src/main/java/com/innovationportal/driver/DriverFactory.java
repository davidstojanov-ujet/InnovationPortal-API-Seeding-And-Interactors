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

        WebDriver driver = switch (browser) {
            case "chrome", "chromium" -> new ChromeDriver(chromeOptions(headless, windowSize));
            case "edge" -> new EdgeDriver(edgeOptions(headless, windowSize));
            case "firefox" -> new FirefoxDriver(firefoxOptions(headless, windowSize));
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + browser + "'; use chrome, chromium, firefox or edge");
        };
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.pageLoadTimeoutSeconds()));
        return driver;
    }

    private static ChromeOptions chromeOptions(boolean headless, String windowSize) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=" + windowSize, "--no-sandbox",
                "--disable-dev-shm-usage", "--disable-gpu");
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless, String windowSize) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=" + windowSize, "--no-sandbox",
                "--disable-dev-shm-usage", "--disable-gpu");
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless, String windowSize) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        String[] size = windowSize.split(",");
        options.addArguments("--width=" + size[0].trim(), "--height=" + size[1].trim());
        return options;
    }
}
