package com.innovationportal.pages;

import com.innovationportal.config.ConfigReader;
import java.util.Optional;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * Approval Dashboard ({@code /innovation-portal/approval-dashboard}) for evaluators and committee members.
 * Filtering replaces the grid container by AJAX. Rows have no ids, so a row is found by its title link and
 * its cells are read by position: 1 Idea, 2 Submitted on, 3 Innovation type, 4 Project Name, 5 Domain,
 * 6 Evaluator, 7 Status, 8 Customer Status. Evaluate is on the idea detail page, not in the grid.
 */
public class ApprovalDashboardPage extends BasePage {

    private static final String ROUTE = "/approval-dashboard";
    private static final int EVALUATOR_COLUMN = 5;
    private static final int STATUS_COLUMN = 6;

    private final By dashboardForm = By.id("innovation-portal-approval-dashboard");
    // Filtering rebuilds the form by AJAX and gives the controls a random id suffix, so they are located by name.
    private final By titleFilter = By.name("innovation_title");
    private final By innovationStatus = By.name("innovation_status");
    private final By filterButton = By.name("Filter");
    private final By gridContainer = By.cssSelector("[data-drupal-selector='approval-dashboard-container']");
    private final By rows = By.cssSelector(".approval_dashboard__row.result_row");
    private final By titleLink = By.cssSelector("a[href*='/innovation-portal/idea/']");
    private final By columns = By.cssSelector(".result_row__col");

    public ApprovalDashboardPage(WebDriver driver) {
        super(driver);
    }

    /** Opens the dashboard by direct URL (the route is relative to the configured base URL). */
    public ApprovalDashboardPage open() {
        driver.get(ConfigReader.baseUrl() + ROUTE);
        return this;
    }

    /** True once the dashboard filter form is visible (waits up to the configured timeout). */
    public boolean isLoaded() {
        return isDisplayed(dashboardForm);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /** Text of the selected Innovation Status filter option (default {@code Pending Approval}). */
    public String getSelectedInnovationStatus() {
        return new Select(waits.waitForVisible(innovationStatus)).getFirstSelectedOption().getText().trim();
    }

    /** Filters the grid by Idea Title and waits for the AJAX-replaced grid to render its rows. */
    public ApprovalDashboardPage filterByTitle(String title) {
        type(titleFilter, title);
        WebElement oldContainer = waits.waitForVisible(gridContainer);
        click(filterButton);
        waits.waitForStale(oldContainer);
        waits.waitForVisible(gridContainer);
        try {
            waits.waitFor(d -> !d.findElements(rows).isEmpty());
        } catch (TimeoutException e) {
            // No matching row: the row lookups below return empty values and the test reports it.
        }
        return this;
    }

    public boolean isIdeaListed(String title) {
        return findRow(title).isPresent();
    }

    public String getEvaluator(String title) {
        return columnText(title, EVALUATOR_COLUMN);
    }

    public String getStatus(String title) {
        return columnText(title, STATUS_COLUMN);
    }

    /**
     * Clicks the idea's title link in the grid and returns its detail page. The link opens a new window
     * ({@code target="”_blank”"}), so the driver switches to it.
     */
    public IdeaDetailPage openIdea(String title) {
        WebElement link = findRow(title)
                .flatMap(row -> row.findElements(titleLink).stream()
                        .filter(item -> item.getText().trim().equals(title))
                        .findFirst())
                .orElseThrow(() -> new IllegalStateException("Idea '" + title + "' is not listed in the grid"));
        Set<String> windowsBefore = driver.getWindowHandles();
        scrollIntoView(link);
        waits.waitFor(ExpectedConditions.elementToBeClickable(link)).click();
        String newWindow = waits.waitFor(d -> d.getWindowHandles().stream()
                .filter(handle -> !windowsBefore.contains(handle))
                .findFirst()
                .orElse(null));
        driver.switchTo().window(newWindow);
        return new IdeaDetailPage(driver);
    }

    private String columnText(String title, int index) {
        return findRow(title).map(row -> row.findElements(columns).get(index).getText().trim()).orElse("");
    }

    private Optional<WebElement> findRow(String title) {
        return driver.findElements(rows).stream()
                .filter(row -> row.findElements(titleLink).stream()
                        .anyMatch(link -> link.getText().trim().equals(title)))
                .findFirst();
    }
}
