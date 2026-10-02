<!-- PR title: "<JIRA-KEY>: <ticket title>". Branch: feature/<JIRA-KEY>-<short-name>. See docs/PROJECT_CONTEXT.md. -->

Jira: <JIRA-KEY>

## What changed
-

## How to run
```
mvn -q clean test-compile
```

## Results
-

## Open questions
-

## Checklist
- [ ] Only what the ticket asks for
- [ ] Locators only in page objects, assertions only in tests
- [ ] Explicit waits only (no Thread.sleep, no implicit waits)
- [ ] Test data from JSON files, no hardcoded credentials
- [ ] Every test has @Test, @Tag("P1"), a feature tag and @DisplayName("<TestCaseID> - <title>")
