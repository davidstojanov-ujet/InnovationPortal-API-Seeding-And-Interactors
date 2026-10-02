package com.innovationportal.models;

/**
 * Test user loaded from JSON test data. Pattern example for test-data POJOs:
 * a no-args constructor plus getters/setters so Jackson can bind it.
 *
 * <pre>{@code
 * UserCredentials employee = JsonUtils.read("users.json", "employee", UserCredentials.class);
 * loginPage.login(employee.getUsername(), employee.getPassword());
 * }</pre>
 */
public class UserCredentials {

    private String username;
    private String password;

    public UserCredentials() {
    }

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /** Never prints the password. */
    @Override
    public String toString() {
        return "UserCredentials{username='" + username + "'}";
    }
}
