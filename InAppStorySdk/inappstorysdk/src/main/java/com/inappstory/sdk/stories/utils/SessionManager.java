package com.inappstory.sdk.stories.utils;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.SessionResponse;
import com.inappstory.sdk.stories.api.models.StatisticPermissions;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static SessionManager INSTANCE;

    private static final Object lock = new Object();

    public static SessionManager getInstance() {
        synchronized (lock) {
            if (INSTANCE == null)
                INSTANCE = new SessionManager();
            return INSTANCE;
        }
    }

    public void useOrOpenSession(OpenSessionCallback callback) {
        if (checkOpenStatistic(callback)) {
            callback.onSuccess();
        }
    }

    public boolean checkOpenStatistic(final OpenSessionCallback callback) {
        boolean checkOpen = false;
        synchronized (openProcessLock) {
            checkOpen = openProcess;
        }
        if (InAppStoryService.isConnected()) {
            if (Session.needToUpdate() || checkOpen) {
                openSession(callback);
                return false;
            } else {
                return true;
            }
        } else {
            if (callback != null)
                callback.onError();
            return false;
        }
    }


    public static boolean openProcess = false;

    public static Object openProcessLock = new Object();
    public static ArrayList<OpenSessionCallback> callbacks = new ArrayList<>();

    public void openStatisticSuccess(final SessionResponse response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (response == null || response.session == null) return;
                response.session.statisticPermissions = new StatisticPermissions(
                        response.isAllowProfiling,
                        response.isAllowStatV1,
                        response.isAllowStatV2,
                        response.isAllowCrash
                );
                response.session.editor = response.editor;
                response.session.save();
                InAppStoryService.getInstance().saveSessionPlaceholders(response.placeholders);
                InAppStoryService.getInstance().saveSessionImagePlaceholders(response.imagePlaceholders);
                synchronized (openProcessLock) {
                    openProcess = false;
                    for (OpenSessionCallback localCallback : callbacks)
                        if (localCallback != null)
                            localCallback.onSuccess();
                    callbacks.clear();
                }
                InAppStoryService.getInstance().runStatisticThread();
                Downloader.downloadFonts(response.cachedFonts);
            }
        });
    }

    private static final String FEATURES =
            "animation,data,deeplink,placeholder,webp,resetTimers,gameReader,swipeUpItems,sendApi,imgPlaceholder";

    public void openSession(final OpenSessionCallback callback) {
        synchronized (openProcessLock) {
            if (openProcess) {
                if (callback != null)
                    callbacks.add(callback);
                return;
            }
        }
        synchronized (openProcessLock) {
            callbacks.clear();
            openProcess = true;
            if (callback != null)
                callbacks.add(callback);
        }
        Context context = InAppStoryService.getInstance().getContext();
        String platform = "android";
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);// Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        // deviceId = deviceId + "1";
        String model = Build.MODEL;
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String screenWidth = Integer.toString(Sizes.getScreenSize(context).x);
        String screenHeight = Integer.toString(Sizes.getScreenSize(context).y);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        String screenDpi = Float.toString(metrics.density * 160f);
        String osVersion = Build.VERSION.CODENAME;
        String osSdkVersion = Integer.toString(Build.VERSION.SDK_INT);
        String appPackageId = context.getPackageName();
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            InAppStoryService.createExceptionLog(e);
        }
        String appVersion = (pInfo != null ? pInfo.versionName : "");
        String appBuild = (pInfo != null ? Integer.toString(pInfo.versionCode) : "");
        if (!InAppStoryService.isConnected()) {
            synchronized (openProcessLock) {
                openProcess = false;
            }
            return;
        }
        final String sessionOpenUID = ProfilingManager.getInstance().addTask("api_session_open");
        NetworkClient.getApi().sessionOpen(
                "cache", FEATURES,
                platform,
                deviceId,
                model,
                manufacturer,
                brand,
                screenWidth,
                screenHeight,
                screenDpi,
                osVersion,
                osSdkVersion,
                appPackageId,
                appVersion,
                appBuild,
                InAppStoryService.getInstance().getUserId()
        ).enqueue(new NetworkCallback<SessionResponse>() {
            @Override
            public void onSuccess(SessionResponse response) {
                if (InAppStoryService.isNull()) return;
                OldStatisticManager.getInstance().eventCount = 0;
                ProfilingManager.getInstance().setReady(sessionOpenUID);
                openStatisticSuccess(response);
                CachedSessionData cachedSessionData = new CachedSessionData();
                cachedSessionData.userId = InAppStoryService.getInstance().getUserId();
                cachedSessionData.placeholders = response.placeholders;
                cachedSessionData.previewAspectRatio = response.getPreviewAspectRatio();
                cachedSessionData.sessionId = response.session.id;
                cachedSessionData.testKey = ApiSettings.getInstance().getTestKey();
                cachedSessionData.token = ApiSettings.getInstance().getApiKey();
                cachedSessionData.tags = InAppStoryService.getInstance().getTagsString();
                CachedSessionData.setInstance(cachedSessionData);
            }

            @Override
            public Type getType() {
                return SessionResponse.class;
            }


            @Override
            public void onError(int code, String message) {
                ProfilingManager.getInstance().setReady(sessionOpenUID);
                if (CallbackManager.getInstance().getErrorCallback() != null) {
                    CallbackManager.getInstance().getErrorCallback().sessionError();
                }
                synchronized (openProcessLock) {
                    openProcess = false;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            for (OpenSessionCallback localCallback : callbacks)
                                if (localCallback != null)
                                    localCallback.onError();
                            callbacks.clear();
                        }
                    });
                }


                super.onError(code, message);

            }

            @Override
            public void onTimeout() {
                ProfilingManager.getInstance().setReady(sessionOpenUID);
                if (CallbackManager.getInstance().getErrorCallback() != null) {
                    CallbackManager.getInstance().getErrorCallback().sessionError();
                }
                synchronized (openProcessLock) {
                    openProcess = false;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            for (OpenSessionCallback localCallback : callbacks)
                                if (localCallback != null)
                                    localCallback.onError();
                            callbacks.clear();
                        }
                    });
                }

            }
        });
    }

    private void clearCaches() {
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService != null) {
            inAppStoryService.listStoriesIds.clear();
            inAppStoryService.clearGames();
        }
    }

    public void closeSession(boolean sendStatistic, final boolean changeUserId) {
        clearCaches();
        if (Session.getInstance().id != null) {
            List<List<Object>> stat = new ArrayList<>();
            stat.addAll(sendStatistic ? OldStatisticManager.getInstance().statistic :
                    new ArrayList<List<Object>>());
            if (OldStatisticManager.getInstance() != null)
                OldStatisticManager.getInstance().clear();

            final String sessionCloseUID =
                    ProfilingManager.getInstance().addTask("api_session_close");

            NetworkClient.getApi().sessionClose(new StatisticSendObject(Session.getInstance().id,
                    stat)).enqueue(
                    new NetworkCallback<SessionResponse>() {
                        @Override
                        public void onSuccess(SessionResponse response) {
                            ProfilingManager.getInstance().setReady(sessionCloseUID, true);
                            if (changeUserId && InAppStoryService.isNotNull())
                                InAppStoryService.getInstance().getListReaderConnector().changeUserId();
                        }

                        @Override
                        public Type getType() {
                            return SessionResponse.class;
                        }

                        @Override
                        public void onTimeout() {
                            super.onTimeout();
                            ProfilingManager.getInstance().setReady(sessionCloseUID);
                        }

                        @Override
                        public void onError(int code, String message) {
                            ProfilingManager.getInstance().setReady(sessionCloseUID);
                            if (changeUserId && InAppStoryService.isNotNull())
                                InAppStoryService.getInstance().getListReaderConnector().changeUserId();
                        }
                    });
        }
        Session.clear();
    }

}
