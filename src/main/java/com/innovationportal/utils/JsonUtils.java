package com.innovationportal.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.innovationportal.constants.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads JSON test data from {@code src/test/resources/testdata} (classpath {@code testdata/}) with Jackson.
 * String values of the form {@code "${ENV_VAR}"} are replaced with the environment variable's value
 * (a system property with the same name wins, e.g. {@code -DEMPLOYEE_PASSWORD=...}).
 *
 * <pre>{@code
 * UserCredentials employee = JsonUtils.read("users.json", "employee", UserCredentials.class);
 * List<UserCredentials> all = JsonUtils.readList("users.json", "allUsers", UserCredentials.class);
 * }</pre>
 */
public final class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern ENV_PLACEHOLDER = Pattern.compile("^\\$\\{([A-Za-z_][A-Za-z0-9_]*)}$");

    private JsonUtils() {
    }

    /** Binds the whole file to {@code type}. */
    public static <T> T read(String fileName, Class<T> type) {
        return convert(load(fileName), type, fileName);
    }

    /** Binds the value under top-level {@code key} to {@code type}. */
    public static <T> T read(String fileName, String key, Class<T> type) {
        return convert(node(fileName, key), type, fileName + "#" + key);
    }

    /** Binds a top-level JSON array to a list of {@code type}. */
    public static <T> List<T> readList(String fileName, Class<T> type) {
        return toList(load(fileName), type, fileName);
    }

    /** Binds the array under top-level {@code key} to a list of {@code type}. */
    public static <T> List<T> readList(String fileName, String key, Class<T> type) {
        return toList(node(fileName, key), type, fileName + "#" + key);
    }

    private static JsonNode node(String fileName, String key) {
        JsonNode value = load(fileName).get(key);
        if (value == null) {
            throw new IllegalArgumentException(
                    "Key '" + key + "' not found in test data file " + Constants.TEST_DATA_DIR + "/" + fileName);
        }
        return value;
    }

    private static <T> List<T> toList(JsonNode node, Class<T> type, String source) {
        if (!node.isArray()) {
            throw new IllegalArgumentException("Expected a JSON array in " + source);
        }
        List<T> items = new ArrayList<>();
        for (JsonNode item : node) {
            items.add(convert(item, type, source));
        }
        return items;
    }

    private static <T> T convert(JsonNode node, Class<T> type, String source) {
        try {
            return MAPPER.treeToValue(node, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot map " + source + " to " + type.getSimpleName(), e);
        }
    }

    private static JsonNode load(String fileName) {
        String resource = Constants.TEST_DATA_CLASSPATH_DIR + "/" + fileName;
        try (InputStream in = JsonUtils.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalArgumentException(
                        "Test data file not found: " + Constants.TEST_DATA_DIR + "/" + fileName);
            }
            return resolveEnv(MAPPER.readTree(in), fileName);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot parse test data file " + fileName, e);
        }
    }

    private static JsonNode resolveEnv(JsonNode node, String fileName) {
        if (node.isTextual()) {
            Matcher matcher = ENV_PLACEHOLDER.matcher(node.asText());
            if (matcher.matches()) {
                return new TextNode(envValue(matcher.group(1), fileName));
            }
        } else if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = object.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                field.setValue(resolveEnv(field.getValue(), fileName));
            }
        } else if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;
            for (int i = 0; i < array.size(); i++) {
                array.set(i, resolveEnv(array.get(i), fileName));
            }
        }
        return node;
    }

    private static String envValue(String name, String fileName) {
        String value = System.getProperty(name);
        if (value == null) {
            value = System.getenv(name);
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(
                    "Environment variable '" + name + "' referenced in " + fileName + " is not set");
        }
        return value;
    }
}
