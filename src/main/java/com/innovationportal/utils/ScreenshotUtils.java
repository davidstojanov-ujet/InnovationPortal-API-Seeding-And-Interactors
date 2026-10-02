package com.innovationportal.utils;

import com.innovationportal.constants.Constants;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Saves a PNG screenshot to {@code target/screenshots/<timestamp>_<testName>.png} and returns the bytes,
 * so callers can also attach them to Allure.
 *
 * <pre>{@code
 * byte[] png = ScreenshotUtils.capture(driver, "IPS-001 login");
 * Allure.addAttachment("Screenshot", "image/png", new ByteArrayInputStream(png), "png");
 * }</pre>
 */
public final class ScreenshotUtils {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern(Constants.TIMESTAMP_FORMAT);

    private ScreenshotUtils() {
    }

    public static byte[] capture(WebDriver driver, String testName) {
        byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Path file = Paths.get(Constants.SCREENSHOT_DIR,
                LocalDateTime.now().format(TIMESTAMP) + "_" + sanitize(testName) + ".png");
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, png);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot save screenshot to " + file, e);
        }
        return png;
    }

    private static String sanitize(String name) {
        return name == null ? "screenshot" : name.replaceAll("[^A-Za-z0-9._-]+", "_");
    }
}
