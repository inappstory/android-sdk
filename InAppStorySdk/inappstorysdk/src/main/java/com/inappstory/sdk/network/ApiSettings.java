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

    public String getCmsKey() {
        return cmsKey;
    }

    public String getCmsUrl() {
        return cmsUrl;
    }

    private String cacheDirPath;
    private String cmsId;
    private String cmsKey;
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

    public ApiSettings cmsKey(String cmsKey) {
        if (ApiSettings.this.cmsKey != null && cmsKey != null && !ApiSettings.this.cmsKey.equals(cmsKey))
            NetworkClient.clear();
        ApiSettings.this.cmsKey = cmsKey;
        return ApiSettings.this;
    }

    public ApiSettings() {
    }


}
