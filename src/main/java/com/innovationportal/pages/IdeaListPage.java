package com.innovationportal.pages;

import com.innovationportal.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Idea List ({@code /innovation-portal/dashboard/all_idea_list}): the approved ideas visible to all employees.
 * The grid has no Confidential column, so visibility is checked by Idea Title after filtering. Filtering
 * rebuilds the whole form by AJAX (new random id suffixes), so controls are located by name and the form by class.
 */
public class IdeaListPage extends BasePage {

    private static final String ROUTE = "/dashboard/all_idea_list";

    private final By listForm = By.cssSelector("form.innovation-portal-dashboards");
    private final By titleFilter = By.name("innovation_title");
    private final By filterButton = By.name("Filter");
    private final By rows = By.cssSelector(".all_idea_dashboard__row.result_row");
    private final By titleLink = By.cssSelector("a[href*='/innovation-portal/idea/']");

    public IdeaListPage(WebDriver driver) {
        super(driver);
    }

    /** Opens the Idea List by direct URL (the route is relative to the configured base URL). */
    public IdeaListPage open() {
        driver.get(ConfigReader.baseUrl() + ROUTE);
        return this;
    }

    /** True once the Idea List filter form is visible and the browser is on the Idea List route. */
    public boolean isLoaded() {
        return isDisplayed(listForm) && driver.getCurrentUrl().contains(ROUTE);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /** Filters the grid by Idea Title and waits for the AJAX-rebuilt form to render again. */
    public IdeaListPage filterByTitle(String title) {
        type(titleFilter, title);
        WebElement oldForm = waits.waitForVisible(listForm);
        click(filterButton);
        waits.waitForStale(oldForm);
        waits.waitForVisible(listForm);
        return this;
    }

    public boolean isIdeaListed(String title) {
        return driver.findElements(rows).stream()
                .flatMap(row -> row.findElements(titleLink).stream())
                .anyMatch(link -> link.getText().trim().equals(title));
    }
}
