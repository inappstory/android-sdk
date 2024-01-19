package com.inappstory.sdk.stories.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
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

    public static final Object openProcessLock = new Object();
    public static ArrayList<OpenSessionCallback> callbacks = new ArrayList<>();

    private void saveSession(SessionResponse response, @NonNull InAppStoryService service) {
        if (response == null || response.session == null) return;
        response.session.statisticPermissions = new StatisticPermissions(
                response.isAllowProfiling,
                response.isAllowStatV1,
                response.isAllowStatV2,
                response.isAllowCrash
        );
        response.session.editor = response.editor;
        response.session.save();
        service.saveSessionPlaceholders(response.placeholders);
        service.saveSessionImagePlaceholders(response.imagePlaceholders);
    }

    private void sessionIsSaved(final SessionResponse response, @NonNull final InAppStoryService service) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                synchronized (openProcessLock) {
                    openProcess = false;
                    for (OpenSessionCallback localCallback : callbacks)
                        if (localCallback != null)
                            localCallback.onSuccess();
                    callbacks.clear();
                }
                service.runStatisticThread();
                Downloader.downloadFonts(response.cachedFonts);
            }
        });
    }

    private static final String FEATURES =
            "animation,data,deeplink,placeholder,webp,resetTimers,gameReader,swipeUpItems,sendApi,imgPlaceholder";


    @SuppressLint("HardwareIds")
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
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) {
                openSessionInner(service);
            }
        });
    }

    private void openSessionInner(final @NonNull InAppStoryService service) {
        Context context = service.getContext();
        String platform = "android";
        String deviceId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
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
        NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (!InAppStoryService.isConnected() || networkClient == null) {
            synchronized (openProcessLock) {
                openProcess = false;
            }
            return;
        }
        final String sessionOpenUID = ProfilingManager.getInstance().addTask("api_session_open");
        final String initialUserId = service.getUserId();
        networkClient.enqueue(
                networkClient.getApi().sessionOpen(
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
                        initialUserId
                ),
                new NetworkCallback<SessionResponse>() {
                    @Override
                    public void onSuccess(final SessionResponse response) {
                        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
                            @Override
                            public void use(@NonNull InAppStoryService newService) throws Exception {
                                saveSession(response, service);
                                if (initialUserId == null) {
                                    if (newService.getUserId() != null) {
                                        closeSession(false, true, null);
                                        openSessionInner(newService);
                                        return;
                                    }
                                } else {
                                    if (!initialUserId.equals(newService.getUserId())) {
                                        closeSession(false, true, initialUserId);
                                        openSessionInner(newService);
                                        return;
                                    }
                                }
                                OldStatisticManager.getInstance().eventCount = 0;
                                ProfilingManager.getInstance().setReady(sessionOpenUID);
                                sessionIsSaved(response, newService);
                                CachedSessionData cachedSessionData = new CachedSessionData();
                                cachedSessionData.userId = newService.getUserId();
                                cachedSessionData.placeholders = response.placeholders;
                                cachedSessionData.previewAspectRatio = response.getPreviewAspectRatio();
                                cachedSessionData.sessionId = response.session.id;
                                cachedSessionData.testKey = ApiSettings.getInstance().getTestKey();
                                cachedSessionData.token = ApiSettings.getInstance().getApiKey();
                                cachedSessionData.tags = newService.getTagsString();
                                CachedSessionData.setInstance(cachedSessionData);
                            }
                        });

                    }

                    @Override
                    public Type getType() {
                        return SessionResponse.class;
                    }


                    @Override
                    public void errorDefault(String message) {
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
                }
        );

    }

    private void clearCaches() {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.listStoriesIds.clear();
                service.clearGames();
            }
        });
    }

    public void closeSession(
            boolean sendStatistic,
            final boolean changeUserId,
            final String oldUserId
    ) {
        clearCaches();
        if (Session.getInstance().id != null) {
            List<List<Object>> stat = new ArrayList<>(
                    sendStatistic ?
                            OldStatisticManager.getInstance().statistic :
                            new ArrayList<List<Object>>()
            );
            if (OldStatisticManager.getInstance() != null)
                OldStatisticManager.getInstance().clear();

            final String sessionCloseUID =
                    ProfilingManager.getInstance().addTask("api_session_close");

            NetworkClient networkClient = InAppStoryManager.getNetworkClient();
            if (networkClient == null) {
                InAppStoryService.useInstance(new UseServiceInstanceCallback() {
                    @Override
                    public void use(@NonNull InAppStoryService service) throws Exception {
                        if (changeUserId)
                            service.getListReaderConnector().changeUserId();
                    }
                });
            } else {
                networkClient.enqueue(
                        networkClient.getApi().sessionClose(
                                new StatisticSendObject(
                                        Session.getInstance().id,
                                        stat
                                ),
                                oldUserId
                        ),
                        new NetworkCallback<SessionResponse>() {
                            @Override
                            public void onSuccess(SessionResponse response) {
                                ProfilingManager.getInstance().setReady(sessionCloseUID, true);
                                InAppStoryService inAppStoryService = InAppStoryService.getInstance();
                                if (changeUserId && inAppStoryService != null)
                                    inAppStoryService.getListReaderConnector().changeUserId();
                            }

                            @Override
                            public Type getType() {
                                return SessionResponse.class;
                            }

                            @Override
                            public void errorDefault(String message) {
                                ProfilingManager.getInstance().setReady(sessionCloseUID);
                                InAppStoryService inAppStoryService = InAppStoryService.getInstance();
                                if (changeUserId && inAppStoryService != null)
                                    inAppStoryService.getListReaderConnector().changeUserId();
                            }
                        });
            }
        }
        Session.clear();
    }

}
