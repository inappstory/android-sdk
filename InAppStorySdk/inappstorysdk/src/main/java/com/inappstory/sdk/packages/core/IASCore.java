package com.inappstory.sdk.packages.core;

import com.inappstory.iasutilsconnector.UtilModulesHolder;
import com.inappstory.iasutilsconnector.json.IJsonParser;
import com.inappstory.sdk.IASLogger;
import com.inappstory.sdk.UserDebugLogManager;
import com.inappstory.sdk.network.JsonParser;

public class IASCore implements IIASCore {

    public static final String IAS_DEBUG_API = "IAS debug api";
    public final static String IAS_ERROR_TAG = "InAppStory_SDK_error";
    private final UtilModulesHolder utilModulesHolder = UtilModulesHolder.INSTANCE;
    private final UserDebugLogManager logManager = new UserDebugLogManager();

    private IASCore() {
        utilModulesHolder.setJsonParser(new IJsonParser() {
            @Override
            public <T> T fromJson(String json, Class<T> typeOfT) {
                return JsonParser.fromJson(json, typeOfT);
            }
        });
    }

    private static IIASCore INSTANCE;
    private static final Object lock = new Object();

    public static void logMessage(String message) {
        getInstance()
                .getLogger()
                .showELog(
                        IAS_ERROR_TAG,
                        message
                );
    }

    public static IIASCore getInstance() {
        synchronized (lock) {
            if (INSTANCE == null)
                INSTANCE = new IASCore();
            return INSTANCE;
        }
    }

    @Override
    public void setLogger(IASLogger logger) {
        logManager.setLogger(logger);
    }

    @Override
    public UserDebugLogManager getLogger() {
        return logManager;
    }

    @Override
    public UtilModulesHolder getUtilModelsHolder() {
        return utilModulesHolder;
    }

}
