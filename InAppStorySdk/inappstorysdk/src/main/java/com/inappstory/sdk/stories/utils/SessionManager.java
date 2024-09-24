package com.inappstory.sdk.stories.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.SessionRequestFields;
import com.inappstory.sdk.stories.api.models.SessionResponse;
import com.inappstory.sdk.stories.api.models.StatisticPermissions;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.statistic.GetOldStatisticManagerCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.ugc.extinterfaces.IOpenSessionCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        checkOpenStatistic(callback);
    }

    public void addStaticOpenSessionCallback(IOpenSessionCallback callback) {
        synchronized (openProcessLock) {
            staticCallbacks.add(callback);
        }
    }

    public void checkOpenStatistic(final OpenSessionCallback callback) {
        boolean checkOpen = false;
        synchronized (openProcessLock) {
            checkOpen = openProcess;
        }
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null && service.isConnected()) {
            String session = service.getSession().getSessionId();
            if (session.isEmpty() || checkOpen) {
                openSession(callback);
            } else {
                callback.onSuccess(session);
            }
        } else {
            if (callback != null)
                callback.onError();
        }
    }


    public static boolean openProcess = false;

    public final Object openProcessLock = new Object();
    public ArrayList<OpenSessionCallback> callbacks = new ArrayList<>();
    public ArrayList<IOpenSessionCallback> staticCallbacks = new ArrayList<>();

    private void saveSession(final SessionResponse response) {
        if (response == null || response.session == null) return;
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                response.session.statisticPermissions = new StatisticPermissions(
                        response.isAllowProfiling,
                        response.isAllowStatV1,
                        response.isAllowStatV2,
                        response.isAllowCrash
                );
                response.session.isAllowUgc = response.isAllowUgc;
                service.getSession().setSession(response.session);
                service.saveSessionPlaceholders(response.placeholders);
                service.saveSessionImagePlaceholders(response.imagePlaceholders);
            }
        });

    }

    private void sessionIsSaved(final SessionResponse response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                synchronized (openProcessLock) {
                    openProcess = false;
                    for (OpenSessionCallback localCallback : callbacks)
                        if (localCallback != null)
                            localCallback.onSuccess(response.session.id);
                    callbacks.clear();
                }
                InAppStoryService.useInstance(new UseServiceInstanceCallback() {
                    @Override
                    public void use(@NonNull InAppStoryService service) throws Exception {
                        service.runStatisticThread();
                        service.downloadSessionAssets(response.sessionAssets);
                    }
                });
            }
        });
    }

    public void openStatisticSuccess(SessionResponse response) {
        sessionIsSaved(response);
    }

    private final String FEATURES =
            "animation,data,deeplink,placeholder,webp,resetTimers,gameReader,swipeUpItems,sendApi,imgPlaceholder,assets,vod,closeStoryApi";

    private final String SESSION_FIELDS = TextUtils.join(",", new String[]{
            SessionRequestFields.session,
            SessionRequestFields.previewAspectRatio,
            SessionRequestFields.isAllowProfiling,
            SessionRequestFields.isAllowStatV1,
            SessionRequestFields.isAllowStatV2,
            SessionRequestFields.isAllowCrash,
            SessionRequestFields.isAllowUgc,
            SessionRequestFields.placeholders,
            SessionRequestFields.preloadGame,
            SessionRequestFields.imagePlaceholders
    });
    private final String SESSION_EXPAND = TextUtils.join(",", new String[]{
            SessionRequestFields.sessionAssets
    });


    public void openSession(final OpenSessionCallback callback) {
        synchronized (openProcessLock) {
            if (openProcess) {
                if (callback != null)
                    callbacks.add(callback);
                return;
            }
            callbacks.clear();
            openProcess = true;
            if (callback != null)
                callbacks.add(callback);
        }
        openSessionInner();
    }

    @SuppressLint("HardwareIds")
    private void openSessionInner() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        final InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager == null) return;
        Context context = service.getContext();
        String platform = "android";
        String deviceId = null;
        if (manager.isDeviceIDEnabled()) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );
        }
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
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (!InAppStoryService.isServiceConnected() || networkClient == null) {
            synchronized (openProcessLock) {
                openProcess = false;
            }
            return;
        }
        final String sessionOpenUID = ProfilingManager.getInstance().addTask("api_session_open");
        final String initialUserId = InAppStoryService.getInstance().getUserId();
        networkClient.enqueue(
                networkClient.getApi().sessionOpen(
                        SESSION_FIELDS,
                        SESSION_EXPAND,
                        FEATURES,
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
                    public void onSuccess(SessionResponse response) {
                        InAppStoryService service = InAppStoryService.getInstance();
                        if (service == null) return;
                        if (response == null ||
                                response.session == null ||
                                response.session.id == null ||
                                response.session.id.isEmpty()
                        )
                            return;
                        String serviceUserId = service.getUserId();
                        String currentSession = response.session.id;
                        if (initialUserId == null) {
                            if (serviceUserId != null) {
                                InAppStoryManager.showDLog("AdditionalLog", "closeSession: initial user is null, new user not null");
                                closeSession(
                                        false,
                                        true,
                                        manager.getCurrentLocale(),
                                        null,
                                        currentSession
                                );
                                openSessionInner();
                                return;
                            }
                        } else {
                            if (!initialUserId.equals(serviceUserId)) {
                                InAppStoryManager.showDLog("AdditionalLog", "closeSession: initial user " + initialUserId + ", new user " + serviceUserId);
                                closeSession(
                                        false,
                                        true,
                                        manager.getCurrentLocale(),
                                        initialUserId,
                                        currentSession
                                );
                                openSessionInner();
                                return;
                            }
                        }
                        saveSession(response);
                        networkClient.setSessionId(currentSession);
                        service.getListReaderConnector().sessionIsOpened(currentSession);
                        ProfilingManager.getInstance().setReady(sessionOpenUID);
                        openStatisticSuccess(response);
                        CachedSessionData cachedSessionData = new CachedSessionData();
                        cachedSessionData.userId = serviceUserId;
                        cachedSessionData.placeholders = response.placeholders;
                        cachedSessionData.previewAspectRatio = response.getPreviewAspectRatio();
                        cachedSessionData.sessionId = response.session.id;
                        cachedSessionData.testKey = ApiSettings.getInstance().getTestKey();
                        cachedSessionData.token = ApiSettings.getInstance().getApiKey();
                        cachedSessionData.tags = service.getTagsString();
                        CachedSessionData.setInstance(cachedSessionData);

                        if (response.preloadGame) service.restartGamePreloader();
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
                        final List<OpenSessionCallback> localCallbacks = new ArrayList<>();
                        final List<IOpenSessionCallback> localStaticCallbacks = new ArrayList<>();
                        synchronized (openProcessLock) {
                            openProcess = false;
                            localCallbacks.addAll(callbacks);
                            callbacks.clear();
                            localStaticCallbacks.addAll(staticCallbacks);
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                for (OpenSessionCallback localCallback : localCallbacks)
                                    if (localCallback != null)
                                        localCallback.onError();
                                for (IOpenSessionCallback localCallback : localStaticCallbacks)
                                    if (localCallback != null)
                                        localCallback.onError();
                            }
                        });
                    }
                }
        );
    }

    private void clearCaches() {
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService != null) {
            inAppStoryService.listStoriesIds.clear();
            inAppStoryService.clearGames();
        }
    }

    public void closeSession(
            final boolean sendStatistic,
            final boolean changeUserIdOrLocale,
            final Locale oldLocale,
            final String oldUserId,
            final String oldSessionId
    ) {
        if (oldSessionId == null) return;
        clearCaches();
        final InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        OldStatisticManager.useInstance(oldSessionId, new GetOldStatisticManagerCallback() {
            @Override
            public void get(@NonNull OldStatisticManager manager) {
                List<List<Object>> stat = new ArrayList<>(
                        sendStatistic ?
                                manager.statistic :
                                new ArrayList<List<Object>>()
                );
                manager.clear();

                final String sessionCloseUID =
                        ProfilingManager.getInstance().addTask("api_session_close");
                if (networkClient == null) {
                    if (changeUserIdOrLocale)
                        service.getListReaderConnector().userIdChanged();
                    return;
                }

                Log.e("statisticTests", "closeSession");
                networkClient.enqueue(
                        networkClient.getApi().sessionClose(
                                new StatisticSendObject(
                                        oldSessionId,
                                        stat
                                ),
                                oldUserId,
                                oldLocale.toLanguageTag()
                        ),
                        new NetworkCallback<SessionResponse>() {
                            @Override
                            public void onSuccess(SessionResponse response) {
                                ProfilingManager.getInstance().setReady(sessionCloseUID, true);
                                if (changeUserIdOrLocale)
                                    service.getListReaderConnector().userIdChanged();
                            }

                            @Override
                            public Type getType() {
                                return SessionResponse.class;
                            }

                            @Override
                            public void errorDefault(String message) {
                                ProfilingManager.getInstance().setReady(sessionCloseUID);
                                if (changeUserIdOrLocale)
                                    service.getListReaderConnector().userIdChanged();
                            }
                        }
                );
            }
        });
        if (networkClient != null)
            networkClient.setSessionId(null);
        service.getSession().clear(oldSessionId);
    }

}
