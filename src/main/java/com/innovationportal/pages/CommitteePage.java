package com.innovationportal.pages;

import com.innovationportal.config.ConfigReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Committee List ({@code /innovation-portal/committee}): one card per committee type ("Delivery Unit (DU)",
 * "department", "Core Committee"), each holding a table of committees and their members. The default
 * evaluator of a committee is the member whose list item carries the "default Evaluator" pill.
 */
public class CommitteePage extends BasePage {

    private static final String ROUTE = "/committee";
    private static final String DU_SECTION = "Delivery Unit (DU)";
    private static final String DEPARTMENT_SECTION = "department";

    private final By committeeGrid = By.id("committeeGrid");
    private final By sectionCards = By.cssSelector("#committeeGrid .table-card");
    private final By sectionHeading = By.cssSelector(".table-card-header h3");
    private final By rows = By.cssSelector("tr.committee-row");
    private final By committeeName = By.cssSelector("td.committee-name");
    private final By members = By.cssSelector("td.member-cell li");
    private final By memberName = By.cssSelector(".member-name");
    private final By defaultEvaluatorPill = By.cssSelector(".pill.lead");

    public CommitteePage(WebDriver driver) {
        super(driver);
    }

    /** Opens the Committee List directly (the route is relative to the configured base URL). */
    public CommitteePage open() {
        driver.get(ConfigReader.baseUrl() + ROUTE);
        waits.waitForVisible(committeeGrid);
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(committeeGrid);
    }

    /** Default evaluator of the department committee named {@code department}, or an empty string if none. */
    public String getDepartmentDefaultEvaluator(String department) {
        return findSection(DEPARTMENT_SECTION)
                .flatMap(section -> section.findElements(rows).stream()
                        .filter(row -> row.findElement(committeeName).getText().trim().equalsIgnoreCase(department))
                        .findFirst())
                .map(this::defaultEvaluatorOf)
                .orElse("");
    }

    /** Default evaluators of all Delivery Unit (DU) committees. */
    public Set<String> getDuDefaultEvaluators() {
        Set<String> evaluators = new LinkedHashSet<>();
        findSection(DU_SECTION).ifPresent(section -> section.findElements(rows).stream()
                .map(this::defaultEvaluatorOf)
                .filter(name -> !name.isEmpty())
                .forEach(evaluators::add));
        return evaluators;
    }

    private Optional<WebElement> findSection(String heading) {
        return driver.findElements(sectionCards).stream()
                .filter(card -> card.findElement(sectionHeading).getText().trim().equalsIgnoreCase(heading))
                .findFirst();
    }

    /** Name of the first member with the "default Evaluator" pill (some rows also hold an empty member). */
    private String defaultEvaluatorOf(WebElement row) {
        List<WebElement> rowMembers = row.findElements(members);
        return rowMembers.stream()
                .filter(member -> !member.findElements(defaultEvaluatorPill).isEmpty())
                .map(member -> member.findElement(memberName).getText().trim())
                .filter(name -> !name.isEmpty())
                .findFirst()
                .orElse("");
    }
}
