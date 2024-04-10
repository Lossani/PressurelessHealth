package com.xempre.pressurelesshealth.models;

public class ResponseLogin {
    private String expiry;
    private String token;
    private User user;

    public String getExpiry() {
        return expiry;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
