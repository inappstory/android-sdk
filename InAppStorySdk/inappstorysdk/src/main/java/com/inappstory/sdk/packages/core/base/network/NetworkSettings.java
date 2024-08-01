package com.inappstory.sdk.packages.core.base.network;


public class NetworkSettings {

    public String getApiKey() {
        return apiKey;
    }

    public String getTestKey() {
        return testKey;
    }

    public String getHost() {
        return host;
    }

    public String getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public String getAppPackageId() {
        return appPackageId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public boolean isDeviceIdEnabled() {
        return isDeviceIdEnabled;
    }

    public NetworkSettings apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public NetworkSettings isDeviceIdEnabled(boolean isDeviceIdEnabled) {
        this.isDeviceIdEnabled = isDeviceIdEnabled;
        return this;
    }

    public NetworkSettings testKey(String testKey) {
        this.testKey = testKey;
        return this;
    }

    public NetworkSettings host(String host) {
        this.host = host;
        return this;
    }

    public NetworkSettings userId(String userId) {
        this.userId = userId;
        return this;
    }

    public NetworkSettings sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public NetworkSettings userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public NetworkSettings languageTag(String languageTag) {
        this.languageTag = languageTag;
        return this;
    }

    public NetworkSettings appPackageId(String appPackageId) {
        this.appPackageId = appPackageId;
        return this;
    }

    public NetworkSettings deviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    private String apiKey;
    private String testKey;
    private String host;
    private String userId;
    private String sessionId;
    private String userAgent;

    private String languageTag;
    private String appPackageId;
    private boolean isDeviceIdEnabled;

    private String deviceId;
}
