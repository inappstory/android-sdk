package com.inappstory.sdk.core.api;

public interface IASProjectSettingsInternal extends IASProjectSettings {
    IASProjectSettingsInternal apiKey(String apiKey);
    IASProjectSettingsInternal testKey(String testKey);
    IASProjectSettingsInternal host(String host);
    IASProjectSettingsInternal cacheDir(String dir);
    String host();
    String cacheDir();
}
