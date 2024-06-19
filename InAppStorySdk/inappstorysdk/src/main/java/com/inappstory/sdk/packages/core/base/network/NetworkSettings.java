package com.inappstory.sdk.packages.core.base.network;

import java.util.Locale;

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


    public NetworkSettings setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public NetworkSettings setTestKey(String testKey) {
        this.testKey = testKey;
        return this;
    }

    public NetworkSettings setHost(String host) {
        this.host = host;
        return this;
    }

    public NetworkSettings setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public NetworkSettings setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public NetworkSettings setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public NetworkSettings setLanguageTag(String languageTag) {
        this.languageTag = languageTag;
        return this;
    }

    public NetworkSettings setAppPackageId(String appPackageId) {
        this.appPackageId = appPackageId;
        return this;
    }

    public NetworkSettings setDeviceId(String deviceId) {
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

    private String deviceId;
}
