package com.inappstory.sdk.listwidget;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.WidgetAppearance;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.exceptions.DataException;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class StoriesWidgetService extends RemoteViewsService {

    public static StoriesWidgetService getInstance() {
        return INSTANCE;
    }

    public static void loadData(Context context) throws DataException {
        if (AppearanceManager.csWidgetAppearance() == null || AppearanceManager.csWidgetAppearance().getWidgetClass() == null)
            throw new DataException("'widgetClass' must not be null", new Throwable("Widget data is not valid"));
        if (isConnected(context)) {
            loadList(context, AppearanceManager.csWidgetAppearance().getWidgetClass());
        } else {
            loadNoConnection(context, AppearanceManager.csWidgetAppearance().getWidgetClass());
        }
    }

    static boolean isConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        } catch (Exception e) {
            return true;
        }
    }

    private static void sendBroadcast(String action, Class widgetClass, Context context) {
        Intent i = new Intent(context, widgetClass);
        i.setAction(action);
        context.sendBroadcast(i);
    }

    public static void loadEmpty(Context context, Class widgetClass) {
        sendBroadcast(UPDATE_EMPTY, widgetClass, context);
    }

    public static void loadAuth(Context context, Class widgetClass) {
        sendBroadcast(UPDATE_AUTH, widgetClass, context);
    }

    public static void loadNoConnection(Context context, Class widgetClass) {
        sendBroadcast(UPDATE_NO_CONNECTION, widgetClass, context);
    }

   /* public static void load(Context context, Class widgetClass) {
        sendBroadcast(UPDATE, widgetClass, context);
    }*/

    public static void loadSuccess(Context context, Class widgetClass) {
        sendBroadcast(UPDATE_SUCCESS, widgetClass, context);
    }


    public static final String UPDATE = "ias_w.UPDATE_WIDGETS";
    public static final String CLICK_ITEM = "ias_w.CLICK_ITEM";
    public static final String POSITION = "item_position";
    public static final String ID = "item_id";
    public static final String UPDATE_SUCCESS = "ias_w.UPDATE_SUCCESS_WIDGETS";
    public static final String UPDATE_EMPTY = "ias_w.UPDATE_EMPTY_WIDGETS";
    public static final String UPDATE_NO_CONNECTION = "ias_w.UPDATE_NO_CONNECTION";
    public static final String UPDATE_AUTH = "ias_w.UPDATE_AUTH";

    private static void loadList(final Context context, final Class widgetClass) {
        CachedSessionData cachedSessionData = CachedSessionData.getInstance(context);
        if (cachedSessionData == null) {
            loadAuth(context, widgetClass);
            return;
        }
        if (InAppStoryManager.getInstance() == null) {
            try {
                new InAppStoryManager.Builder()
                        .userId(cachedSessionData.userId)
                        .context(context)
                        .sandbox(false)
                        .hasLike(true)
                        .hasFavorite(true)
                        .hasShare(true)
                        .create();
            } catch (DataException e) {
                e.printStackTrace();
            }
        }
        if (NetworkClient.getAppContext() == null) {
            NetworkClient.setContext(context);
        }
        Log.e("MyWidget", "request");
        NetworkClient.getApi().getStories(cachedSessionData.sessionId, cachedSessionData.testKey, 0,
                cachedSessionData.tags, null, null).enqueue(new NetworkCallback<List<Story>>() {
            @Override
            public void onSuccess(List<Story> response) {
                Log.e("MyWidget", "requestSuccess");
                if (response.size() > 0) {
                    if (!SharedPreferencesAPI.hasContext()) {
                        SharedPreferencesAPI.setContext(context);
                    }
                    try {
                        SharedPreferencesAPI.saveString("widgetStories", JsonParser.getJson(response));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    CsEventBus.getDefault().post(new ListLoadedEvent());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadSuccess(context, widgetClass);
                        }
                    }, 3000);
                } else {
                    loadEmpty(context, widgetClass);
                }

            }

            @Override
            public Type getType() {
                ParameterizedType ptype = new ParameterizedType() {
                    @NonNull
                    @Override
                    public Type[] getActualTypeArguments() {
                        return new Type[]{Story.class};
                    }

                    @NonNull
                    @Override
                    public Type getRawType() {
                        return List.class;
                    }

                    @Nullable
                    @Override
                    public Type getOwnerType() {
                        return List.class;
                    }
                };
                return ptype;
            }
        });
    }


    private static StoriesWidgetService INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        // TODO Auto-generated method stub
        return new StoriesWidgetFactory(this.getApplicationContext(), intent);
    }

}