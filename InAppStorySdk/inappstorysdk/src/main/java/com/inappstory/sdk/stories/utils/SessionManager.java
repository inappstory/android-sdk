package com.inappstory.sdk.stories.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.network.NetworkClient;
import com.inappstory.sdk.core.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionCallback;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.stories.api.models.SessionResponse;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
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

    public void openSessionSuccess(final SessionResponse response) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                InAppStoryService.getInstance().runStatisticThread();


                InAppStoryService.getInstance().saveSessionPlaceholders(response.placeholders);
                InAppStoryService.getInstance().saveSessionImagePlaceholders(response.imagePlaceholders);
            }
        });
    }


    private static final String FEATURES =
            "animation,data,deeplink,placeholder,webp,resetTimers,gameReader,swipeUpItems,sendApi,imgPlaceholder";


    @SuppressLint("HardwareIds")
    public void openSession(
            Context context,
            String userId,
            final IGetSessionCallback<SessionResponse> callback
    ) {
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
            callback.onError();
            return;
        }
        final String sessionOpenUID = ProfilingManager.getInstance().addTask("api_session_open");
        networkClient.enqueue(
                networkClient.getApi().sessionOpen(
                        "cache",
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
                        userId
                ),
                new NetworkCallback<SessionResponse>() {
                    @Override
                    public void onSuccess(SessionResponse response) {
                        ProfilingManager.getInstance().setReady(sessionOpenUID);
                        if (response.session == null) {
                            callback.onError();
                            if (CallbackManager.getInstance().getErrorCallback() != null) {
                                CallbackManager.getInstance().getErrorCallback().sessionError();
                            }
                            return;
                        }
                        OldStatisticManager.getInstance().eventCount = 0;
                        callback.onSuccess(response);
                        openSessionSuccess(response);
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
                    }
                }
        );
    }

    private void clearCaches() {
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService != null) {
            inAppStoryService.cachedListStories.clear();
            inAppStoryService.clearGames();
        }
    }

    public void closeSession(
            SessionDTO sessionDTO,
            final boolean sendStatistic
    ) {
        clearCaches();
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
            return;
        }
        networkClient.enqueue(
                networkClient.getApi().sessionClose(
                        new StatisticSendObject(
                                sessionDTO.getId(),
                                stat
                        )
                ),
                new NetworkCallback<SessionResponse>() {
                    @Override
                    public void onSuccess(SessionResponse response) {
                        ProfilingManager.getInstance().setReady(sessionCloseUID, true);
                    }

                    @Override
                    public Type getType() {
                        return SessionResponse.class;
                    }

                    @Override
                    public void errorDefault(String message) {
                        ProfilingManager.getInstance().setReady(sessionCloseUID);
                    }
                }
        );
    }

}
