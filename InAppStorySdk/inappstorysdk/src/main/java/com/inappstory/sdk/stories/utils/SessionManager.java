package com.inappstory.sdk.stories.utils;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.StatisticResponse;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.events.ChangeUserIdForListEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.inappstory.sdk.InAppStoryService.IAS_LOG;

public class SessionManager {

    private static volatile SessionManager INSTANCE;

    public static SessionManager getInstance() {
        if (INSTANCE == null) {
            synchronized (SessionManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new SessionManager();
            }
        }
        return INSTANCE;
    }

    public void useOrOpenSession(OpenSessionCallback callback) {
        if (checkOpenStatistic(callback)) {
            callback.onSuccess();
        }
    }

    public boolean checkOpenStatistic(final OpenSessionCallback callback) {
        if (InAppStoryService.isConnected()) {
            if (StatisticSession.needToUpdate()) {
                openSession(callback);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    public static boolean openProcess = false;

    public static Object openProcessLock = new Object();
    public static ArrayList<OpenSessionCallback> callbacks = new ArrayList<>();

    public void openStatisticSuccess(final StatisticResponse response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                response.session.save();
                InAppStoryService.getInstance().saveSessionPlaceholders(response.placeholders);
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
            "animation,data,deeplink,placeholder,webp,resetTimers,gameReader";

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

        Log.d(IAS_LOG, "open session");
        Context context = InAppStoryManager.getInstance().getContext();
        String platform = "android";
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);// Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        // deviceId = deviceId + "1";
        String model = Build.MODEL;
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String screenWidth = Integer.toString(Sizes.getScreenSize().x);
        String screenHeight = Integer.toString(Sizes.getScreenSize().y);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        String screenDpi = Float.toString(metrics.density * 160f);
        String osVersion = Build.VERSION.CODENAME;
        String osSdkVersion = Integer.toString(Build.VERSION.SDK_INT);
        String appPackageId = context.getPackageName();
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVersion = (pInfo != null ? pInfo.versionName : "");
        String appBuild = (pInfo != null ? Integer.toString(pInfo.versionCode) : "");
        if (!InAppStoryService.isConnected()) {
            synchronized (openProcessLock) {
                openProcess = false;
            }
            return;
        }
        NetworkClient.getApi().statisticsOpen(
                "cache",
                InAppStoryManager.getInstance().getTagsString(), FEATURES,
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
                InAppStoryManager.getInstance().getUserId()
        ).enqueue(new NetworkCallback<StatisticResponse>() {
            @Override
            public void onSuccess(StatisticResponse response) {
                Log.d(IAS_LOG, "opened");
                openStatisticSuccess(response);
                CachedSessionData cachedSessionData = new CachedSessionData();
                cachedSessionData.userId = InAppStoryManager.getInstance().getUserId();
                cachedSessionData.placeholders = response.placeholders;
                cachedSessionData.sessionId = response.session.id;
                cachedSessionData.testKey = InAppStoryManager.getInstance().getTestKey();
                cachedSessionData.token = InAppStoryManager.getInstance().getApiKey();
                cachedSessionData.tags = InAppStoryManager.getInstance().getTagsString();
                CachedSessionData.setInstance(cachedSessionData);
            }

            @Override
            public Type getType() {
                return StatisticResponse.class;
            }

            @Override
            public void onError(int code, String message) {
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
                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.OPEN_SESSION));
                super.onError(code, message);

            }

            @Override
            public void onTimeout() {
                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.OPEN_SESSION));
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

    public void closeSession(boolean sendStatistic, final boolean changeUserId) {
        if (StatisticSession.getInstance().id != null) {
            NetworkClient.getApi().statisticsClose(new StatisticSendObject(StatisticSession.getInstance().id,
                    sendStatistic ? OldStatisticManager.getInstance().statistic : new ArrayList<List<Object>>())).enqueue(
                    new NetworkCallback<StatisticResponse>() {
                        @Override
                        public void onSuccess(StatisticResponse response) {
                            if (changeUserId)
                                CsEventBus.getDefault().post(new ChangeUserIdForListEvent());
                        }

                        @Override
                        public Type getType() {
                            return StatisticResponse.class;
                        }

                        @Override
                        public void onError(int code, String message) {
                            if (changeUserId)
                                CsEventBus.getDefault().post(new ChangeUserIdForListEvent());
                        }
                    });
        }
        StatisticSession.clear();
    }

}
