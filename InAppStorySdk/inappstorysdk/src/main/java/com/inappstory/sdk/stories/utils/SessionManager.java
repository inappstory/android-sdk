package com.inappstory.sdk.stories.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.api.impl.IASSettingsImpl;
import com.inappstory.sdk.core.data.models.UniqueSessionParameters;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.SessionRequestFields;
import com.inappstory.sdk.core.network.content.models.SessionResponse;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.ugc.extinterfaces.IOpenSessionCallback;
import com.inappstory.sdk.utils.ISessionHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SessionManager {
    private final IASCore core;

    public SessionManager(IASCore core) {
        this.core = core;
        sessionHolder = new SessionHolder(core);
    }

    public void useOrOpenSession(OpenSessionCallback callback) {
        checkOpenStatistic(callback);
    }

    public void addStaticOpenSessionCallback(IOpenSessionCallback callback) {
        synchronized (openProcessLock) {
            staticCallbacks.add(callback);
        }
    }


    private final ISessionHolder sessionHolder;

    public ISessionHolder getSession() {
        return sessionHolder;
    }

    public void checkOpenStatistic(final OpenSessionCallback callback) {
        final boolean checkOpen;
        synchronized (openProcessLock) {
            checkOpen = openProcess;
        }
        final IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
        new ConnectionCheck().check(core.appContext(), new ConnectionCheckCallback(core) {
            @Override
            public void success() {
                String session = getSession().getSessionId();
                if (session.isEmpty() || checkOpen) {
                    openSession(callback);
                } else {
                    callback.onSuccess(
                            new RequestLocalParameters()
                                    .sessionId(session)
                                    .userId(settingsHolder.userId())
                                    .sendStatistic(settingsHolder.sendStatistic())
                                    .anonymous(settingsHolder.anonymous())
                                    .locale(settingsHolder.lang())
                    );
                }
            }

            @Override
            protected void error() {
                if (callback != null)
                    callback.onError();
            }
        });
    }


    public boolean openProcess = false;

    public final Object openProcessLock = new Object();
    public ArrayList<OpenSessionCallback> callbacks = new ArrayList<>();
    public ArrayList<IOpenSessionCallback> staticCallbacks = new ArrayList<>();

    private void saveSession(final SessionResponse response) {
        if (response == null || response.session == null) return;
        IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
        boolean isSendStatistic = settingsHolder.sendStatistic() && !settingsHolder.anonymous();
        core.statistic().storiesV2().disabled(
                !(isSendStatistic && response.isAllowStatV2)
        );
        core.statistic().profiling().disabled(
                !(isSendStatistic && response.isAllowProfiling)
        );
        core.statistic().exceptions().disabled(
                !(isSendStatistic && response.isAllowCrash)
        );
        core.statistic().iamV1().disabled(!(isSendStatistic && (
                response.isAllowStatV1 ||
                        response.isAllowStatV2)
        ));
        core.statistic().bannersV1().disabled(!(isSendStatistic && (
                response.isAllowStatV1 ||
                        response.isAllowStatV2)
        ));
        ((IASSettingsImpl) core.settingsAPI()).sessionPlaceholders(response.placeholders);
        ((IASSettingsImpl) core.settingsAPI()).sessionImagePlaceholders(response.imagePlaceholders);
    }

    private void sessionIsSaved(final SessionResponse response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
                synchronized (openProcessLock) {
                    openProcess = false;
                    for (OpenSessionCallback localCallback : callbacks)
                        if (localCallback != null)
                            localCallback.onSuccess(
                                    new RequestLocalParameters()
                                            .sessionId(response.session.id)
                                            .userId(settingsHolder.userId())
                                            .sendStatistic(settingsHolder.sendStatistic())
                                            .anonymous(settingsHolder.anonymous())
                                            .locale(settingsHolder.lang())
                            );
                    callbacks.clear();
                }

            }
        });
        core.statistic().storiesV1(new GetStatisticV1Callback() {
            @Override
            public void get(@NonNull IASStatisticStoriesV1 manager) {
                manager.restartSchedule();
            }
        });
        core.assetsHolder().setAssets(response.sessionAssets);
        core.assetsHolder().downloadAssets();
    }

    public void openStatisticSuccess(SessionResponse response) {
        sessionIsSaved(response);
    }

    private final String FEATURES =
            "animation,data,deeplink,placeholder,webp,resetTimers,gameReader,swipeUpItems,sendApi,imgPlaceholder,assets,vod,closeStoryApi,slideTimerEndApi,multislideIam";

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
        }
        synchronized (openProcessLock) {
            callbacks.clear();
            openProcess = true;
            if (callback != null)
                callbacks.add(callback);
        }
        openSessionInner();
    }

    @SuppressLint("HardwareIds")
    private void openSessionInner() {
        if (core.network().getBaseUrl() == null) {
            synchronized (openProcessLock) {
                openProcess = false;
            }
            return;
        }

        Context context = core.appContext();
        final String platform = "android";
        final IASDataSettingsHolder dataSettingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        final String deviceId = dataSettingsHolder.deviceId();
        final String model = Build.MODEL;
        final String manufacturer = Build.MANUFACTURER;
        final String brand = Build.BRAND;
        final String screenWidth = Integer.toString(Sizes.getScreenSize(context).x);
        final String screenHeight = Integer.toString(Sizes.getScreenSize(context).y);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final String screenDpi = Float.toString(metrics.density * 160f);
        final String osVersion = Build.VERSION.CODENAME;
        final String osSdkVersion = Integer.toString(Build.VERSION.SDK_INT);
        final String appPackageId = context.getPackageName();
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            core.exceptionManager().createExceptionLog(e);
        }
        final String appVersion = (pInfo != null ? pInfo.versionName : "");
        final String appBuild = (pInfo != null ? Integer.toString(pInfo.versionCode) : "");

        new ConnectionCheck().check(
                context,
                new ConnectionCheckCallback(core) {
                    @Override
                    public void success() {
                        final String sessionOpenUID = core.statistic().profiling().addTask("api_session_open");
                        final UniqueSessionParameters initialSessionParameters = dataSettingsHolder.sessionParameters();
                        core.network().enqueue(
                                core.network().getApi().sessionOpen(
                                        SESSION_FIELDS,
                                        SESSION_EXPAND,
                                        FEATURES,
                                        platform,
                                        initialSessionParameters.anonymous() ? null : deviceId,
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
                                        initialSessionParameters.anonymous(),
                                        initialSessionParameters.userId(),
                                        initialSessionParameters.userSign()
                                ),
                                new NetworkCallback<SessionResponse>() {
                                    @Override
                                    public void onSuccess(SessionResponse response) {
                                        UniqueSessionParameters currentSessionParameters = dataSettingsHolder.sessionParameters();
                                        String currentSession = getSession().getSessionId();
                                        if (!Objects.equals(currentSessionParameters, initialSessionParameters)) {
                                            closeSession(
                                                    initialSessionParameters.anonymous(),
                                                    initialSessionParameters.sendStatistic(),
                                                    true,
                                                    initialSessionParameters.locale(),
                                                    initialSessionParameters.userId(),
                                                    initialSessionParameters.anonymous() ? null : deviceId,
                                                    currentSession
                                            );
                                            openSessionInner();
                                            return;
                                        }
                                        core.statistic().profiling().setReady(sessionOpenUID);
                                        CachedSessionData cachedSessionData = new CachedSessionData();
                                        cachedSessionData.userId = currentSessionParameters.userId();
                                        cachedSessionData.locale = currentSessionParameters.locale();
                                        cachedSessionData.placeholders = response.placeholders;
                                        cachedSessionData.previewAspectRatio = response.getPreviewAspectRatio();
                                        cachedSessionData.isAllowUGC = response.isAllowUgc;
                                        cachedSessionData.sessionId = response.session.id;
                                        cachedSessionData.testKey = core.projectSettingsAPI().testKey();
                                        cachedSessionData.token = core.projectSettingsAPI().apiKey();
                                        cachedSessionData.tags =
                                                TextUtils.join(",", dataSettingsHolder.tags());
                                        boolean isSendStatistic = dataSettingsHolder.sendStatistic() && !dataSettingsHolder.anonymous();
                                        sessionHolder.setSession(cachedSessionData, !(isSendStatistic && response.isAllowStatV1));
                                        core.network().setSessionId(response.session.id);
                                        if (response.preloadGame)
                                            core.contentPreload().restartGamePreloader();
                                        saveSession(response);
                                        openStatisticSuccess(response);
                                        core.inAppStoryService()
                                                .getListReaderConnector().sessionIsOpened(currentSession);

                                    }

                                    @Override
                                    public Type getType() {
                                        return SessionResponse.class;
                                    }


                                    @Override
                                    public void errorDefault(String message) {
                                        core.statistic().profiling().setReady(sessionOpenUID);
                                        core.callbacksAPI().useCallback(IASCallbackType.ERROR,
                                                new UseIASCallback<ErrorCallback>() {
                                                    @Override
                                                    public void use(@NonNull ErrorCallback callback) {
                                                        callback.sessionError();
                                                    }
                                                }
                                        );
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

                    @Override
                    protected void error() {
                        super.error();
                        synchronized (openProcessLock) {
                            openProcess = false;
                        }
                    }
                }
        );

    }

    private void clearCaches() {
        core.storiesListVMHolder().clear();
        core.contentLoader().clearGames();
        core.contentLoader().clearIamWereLoadedStatuses();
    }

    public void closeSession(
            final boolean anonymous,
            final boolean sendStatistic,
            final boolean changeSessionSettings,
            final String oldLang,
            final String oldUserId,
            final String oldDeviceId,
            final String oldSessionId
    ) {
        if (oldSessionId == null) return;
        clearCaches();
        core.statistic().storiesV1(oldSessionId, new GetStatisticV1Callback() {
            @Override
            public void get(@NonNull IASStatisticStoriesV1 manager) {
                List<List<Object>> stat = new ArrayList<>(
                        sendStatistic && !anonymous?
                                manager.extractCurrentStatistic() :
                                new ArrayList<List<Object>>()
                );

                final String sessionCloseUID =
                        core.statistic().profiling().addTask("api_session_close");
                Log.e("statisticTests", "closeSession");
                core.network().enqueue(
                        core.network().getApi().sessionClose(
                                new StatisticSendObject(
                                        oldSessionId,
                                        stat
                                ),
                                oldUserId,
                                oldDeviceId,
                                oldLang
                        ),
                        new NetworkCallback<SessionResponse>() {
                            @Override
                            public void onSuccess(SessionResponse response) {
                                core.statistic().profiling().setReady(sessionCloseUID, true);
                                if (changeSessionSettings) {
                                    core.widgetViewModels().bannerPlaceViewModels().reloadSession();
                                    core.inAppStoryService().getListReaderConnector().userIdChanged();
                                }
                            }

                            @Override
                            public Type getType() {
                                return SessionResponse.class;
                            }

                            @Override
                            public void errorDefault(String message) {
                                core.statistic().profiling().setReady(sessionCloseUID);
                                if (changeSessionSettings) {
                                    core.inAppStoryService().getListReaderConnector().userIdChanged();
                                    core.widgetViewModels().bannerPlaceViewModels().reloadSession();
                                }
                            }
                        }
                );
            }
        });
        core.network().removeSessionId(oldSessionId);
        sessionHolder.clear(oldSessionId);
    }

}
