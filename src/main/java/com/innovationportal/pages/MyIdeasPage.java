package com.innovationportal.pages;

import com.innovationportal.config.ConfigReader;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * My Ideas grid ({@code /innovation-portal/my-idea-list}). Rows have no ids, so a row is found by its
 * title link and its cells are read by position: 3 = Innovation type, last = Action/Status.
 */
public class MyIdeasPage extends BasePage {

    private static final String ROUTE = "/my-idea-list";

    private final By rows = By.cssSelector(".My_ideas_dashboard .result_row");
    private final By titleLink = By.cssSelector("a[href*='/innovation-portal/idea/']");
    private final By columns = By.cssSelector(".result_row__col");
    private final By status = By.cssSelector(".action_col > div");

    public MyIdeasPage(WebDriver driver) {
        super(driver);
    }

    /** Opens My Ideas directly (the route is relative to the configured base URL). */
    public MyIdeasPage open() {
        driver.get(ConfigReader.baseUrl() + ROUTE);
        waits.waitForVisible(rows);
        return this;
    }

    public boolean isIdeaListed(String title) {
        return findRow(title).isPresent();
    }

    public String getInnovationType(String title) {
        return findRow(title).map(row -> row.findElements(columns).get(2).getText().trim()).orElse("");
    }

    public String getStatus(String title) {
        return findRow(title).map(row -> row.findElement(status).getText().trim()).orElse("");
    }

    private Optional<WebElement> findRow(String title) {
        return driver.findElements(rows).stream()
                .filter(row -> row.findElements(titleLink).stream()
                        .anyMatch(link -> link.getText().trim().equals(title)))
                .findFirst();
    }
}
