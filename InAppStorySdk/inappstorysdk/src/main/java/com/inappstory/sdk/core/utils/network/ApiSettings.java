package com.inappstory.sdk.core.utils.network;


import com.inappstory.sdk.core.IASCore;

/**
 * Класс для задания и хранения настроек по доступу к бекенду.
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

    public String getHost() {
        return host;
    }

    private String cacheDirPath;
    private String cmsId;
    private String apiKey;
    private String testKey;
    private String host;

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

    public boolean hostIsDifferent(String host) {
        return ApiSettings.this.host != null && host != null && !ApiSettings.this.host.equals(host);
    }

    public ApiSettings host(String host) {
        ApiSettings.this.host = host;
        return ApiSettings.this;
    }

    public ApiSettings apiKey(String cmsKey) {
        if (ApiSettings.this.apiKey != null && cmsKey != null && !ApiSettings.this.apiKey.equals(cmsKey)) {
            NetworkClient networkClient = IASCore.getInstance().getNetworkClient();
            if (networkClient != null) networkClient.clear();
        }
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
