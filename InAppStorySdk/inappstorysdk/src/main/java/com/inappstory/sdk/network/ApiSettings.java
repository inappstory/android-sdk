package com.inappstory.sdk.network;

/**
 * Класс для задания и хранения настроек по доступу к текущей витрине.
 */

public class ApiSettings {
    private static ApiSettings INSTANCE;

    public String getCacheDirPath() {
        return cacheDirPath;
    }

    public String getCmsId() {
        return cmsId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getTestKey() {
        return testKey;
    }

    public String getCmsUrl() {
        return cmsUrl;
    }

    private String cacheDirPath;
    private String cmsId;
    private String apiKey;
    private String testKey;
    private String cmsUrl;


    public String getWebUrl() {
        return webUrl;
    }

    public ApiSettings setWebUrl(String webUrl) {
        ApiSettings.this.webUrl = webUrl;
        return ApiSettings.this;
    }

    private String webUrl;

    public static ApiSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApiSettings();
        }
        return INSTANCE;
    }

    public ApiSettings cacheDirPath(String cacheDirPath) {
        ApiSettings.this.cacheDirPath = cacheDirPath;
        return ApiSettings.this;
    }

    public ApiSettings cmsId(String cmsId) {
        ApiSettings.this.cmsId = cmsId;
        return ApiSettings.this;
    }

    public ApiSettings cmsUrl(String cmsUrl) {
        if (ApiSettings.this.cmsUrl != null && cmsUrl != null && !ApiSettings.this.cmsUrl.equals(cmsUrl))
            NetworkClient.clear();
        ApiSettings.this.cmsUrl = cmsUrl;
        return ApiSettings.this;
    }

    public ApiSettings apiKey(String cmsKey) {
        if (ApiSettings.this.apiKey != null && cmsKey != null && !ApiSettings.this.apiKey.equals(cmsKey))
            NetworkClient.clear();
        ApiSettings.this.apiKey = cmsKey;
        return ApiSettings.this;
    }

    public ApiSettings testKey(String testKey) {
        ApiSettings.this.testKey = testKey;
        return ApiSettings.this;
    }

    public ApiSettings() {
    }


}
