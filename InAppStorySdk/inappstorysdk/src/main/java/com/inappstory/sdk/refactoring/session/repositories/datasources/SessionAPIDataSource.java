package com.inappstory.sdk.refactoring.session.repositories.datasources;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.network.content.models.SessionResponse;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.refactoring.core.utils.models.Error;
import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.session.UniqueSessionParameters;
import com.inappstory.sdk.refactoring.session.callbacks.CloseSessionCallback;
import com.inappstory.sdk.refactoring.session.callbacks.GetSessionCallback;
import com.inappstory.sdk.refactoring.session.data.network.NSession;
import com.inappstory.sdk.stories.api.models.SessionRequestFields;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.utils.Sizes;

import java.lang.reflect.Type;

public class SessionAPIDataSource {
    private final IASCore core;

    private final String FEATURES =
            "animation,data,deeplink,placeholder,webp,resetTimers,gameReader,swipeUpItems,sendApi,imgPlaceholder,assets,vod,closeStoryApi,slideTimerEndApi,multislideIam,productCart";

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

    public SessionAPIDataSource(IASCore core) {
        this.core = core;
    }

    public void getSession(
            UniqueSessionParameters sessionParameters,
            ResultCallback<NSession> getSessionCallback
    ) {
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
        core.network().enqueue(
                core.network().getApi().sessionOpen(
                        SESSION_FIELDS,
                        SESSION_EXPAND,
                        FEATURES,
                        platform,
                        sessionParameters.anonymous() ? null : deviceId,
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
                        sessionParameters.anonymous(),
                        sessionParameters.userId(),
                        sessionParameters.userSign()
                ), new NetworkCallback<NSession>() {
                    @Override
                    public void onSuccess(NSession response) {
                        getSessionCallback.success(response);
                    }

                    @Override
                    public Type getType() {
                        return NSession.class;
                    }

                    @Override
                    public void errorDefault(String message) {
                        getSessionCallback.error(new Error<>(message));
                    }
                }
        );
    }

    public void updateSession(
            StatisticSendObject statisticSendObject,
            UniqueSessionParameters sessionParameters,
            ResultCallback<Void> updateSessionCallback
    ) {
        core.network().enqueue(
                core.network().getApi().sessionUpdate(
                        statisticSendObject,
                        sessionParameters.userId(),
                        sessionParameters.locale()
                ),
                new NetworkCallback<NSession>() {
                    @Override
                    public void onSuccess(NSession response) {
                        updateSessionCallback.success(null);
                    }

                    @Override
                    public void errorDefault(String message) {
                        updateSessionCallback.error(new Error<>(message));
                    }

                    @Override
                    public Type getType() {
                        return NSession.class;
                    }
                }
        );
    }

    public void closeSession(
            StatisticSendObject statisticSendObject,
            String sessionId,
            String deviceId,
            UniqueSessionParameters sessionParameters,
            ResultCallback<Void> closeSessionCallback
    ) {
        core.network().enqueue(
                core.network().getApi().sessionClose(
                        statisticSendObject,
                        sessionParameters.userId(),
                        deviceId,
                        sessionParameters.locale(),
                        sessionId
                ),
                new NetworkCallback<NSession>() {
                    @Override
                    public void onSuccess(NSession response) {
                        closeSessionCallback.success(null);
                    }

                    @Override
                    public Type getType() {
                        return NSession.class;
                    }

                    @Override
                    public void errorDefault(String message) {
                        closeSessionCallback.error(new Error<>(message));
                    }
                }
        );
    }
}
