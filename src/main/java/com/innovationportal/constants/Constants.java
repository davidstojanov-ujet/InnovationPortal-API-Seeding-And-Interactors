package com.innovationportal.constants;

import java.time.Duration;

/**
 * Framework-wide constants: timeouts, paths, date format and tag names.
 * Locators never go here; they belong in page objects.
 *
 * <pre>{@code
 * @Test
 * @Tag(Constants.TAG_P1)
 * @Tag(Constants.TAG_SMOKE)
 * void opensHomePage() { ... }
 * }</pre>
 */
public final class Constants {

    // Timeouts. The explicit wait used by WaitUtils comes from config (explicitWaitSeconds);
    // these are fallbacks and fixed short/long waits.
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    public static final Duration SHORT_TIMEOUT = Duration.ofSeconds(3);
    public static final Duration LONG_TIMEOUT = Duration.ofSeconds(30);
    public static final Duration POLLING_INTERVAL = Duration.ofMillis(250);

    // Paths (relative to the project root / classpath)
    public static final String TEST_DATA_DIR = "src/test/resources/testdata";
    /** Classpath location of {@link #TEST_DATA_DIR}. */
    public static final String TEST_DATA_CLASSPATH_DIR = "testdata";
    public static final String CONFIG_DIR = "src/test/resources/config";
    public static final String SCREENSHOT_DIR = "target/screenshots";

    // Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss_SSS";

    // Tags (use with @Tag)
    public static final String TAG_P1 = "P1";
    public static final String TAG_SMOKE = "smoke";
    // TODO(Q29): feature tags proposed in docs/testcases/p1-analysis.md, pending confirmation.
    public static final String TAG_FEATURE_LOGIN = "login";
    public static final String TAG_FEATURE_SUBMIT = "submit";
    public static final String TAG_FEATURE_VALIDATION = "validation";
    public static final String TAG_FEATURE_SECURITY = "security";
    public static final String TAG_FEATURE_APPROVAL = "approval";
    public static final String TAG_FEATURE_IDEALIST = "idealist";
    public static final String TAG_FEATURE_TRACKING = "tracking";

    private Constants() {
    }
}
