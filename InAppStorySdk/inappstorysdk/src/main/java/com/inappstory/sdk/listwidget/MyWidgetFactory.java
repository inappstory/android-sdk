package com.inappstory.sdk.listwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.exceptions.DataException;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.stories.utils.Sizes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private int mCount = 10;
    private List<Story> mWidgetItems = new ArrayList<>();
    private Context mContext;
    private int mAppWidgetId;

    public MyWidgetFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        if (InAppStoryService.isConnected()) {
            loadList();
        } else {
            loadEmpty();
        }
    }
    private void loadEmpty() {

    }

    private void loadList() {
        CachedSessionData cachedSessionData = CachedSessionData.getInstance(mContext);
        if (cachedSessionData == null) {
            loadEmpty();
            return;
        }
        if (NetworkClient.getAppContext() == null) {
            NetworkClient.setContext(mContext);
        }
        NetworkClient.getApi().getStories(cachedSessionData.sessionId, cachedSessionData.testKey, 0,
                cachedSessionData.tags, null, null).enqueue(new NetworkCallback<List<Story>>() {
            @Override
            public void onSuccess(List<Story> response) {
                Log.e("ias_network", "stories");
                mWidgetItems.clear();
                mWidgetItems.addAll(response);
                Intent i = new Intent(mContext, MyWidget.class);
                i.setAction(MyWidget.UPDATE_ALL_WIDGETS);
                mContext.sendBroadcast(i);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent i = new Intent(mContext, MyWidget.class);
                        i.setAction(MyWidget.UPDATE_ALL_WIDGETS);
                        mContext.sendBroadcast(i);
                    }
                }, 1500);
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

    @Override
    public void onCreate() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDataSetChanged() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroy() {
        //  mWidgetItems.clear();

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mWidgetItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {


        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.cs_widget_grid_item);
        rv.setTextViewText(R.id.title, mWidgetItems.get(position).getTitle());
        rv.setTextColor(R.id.title, AppearanceManager.getInstance().csWidgetAppearance().getTextColor());
        View view = View.inflate(mContext, R.layout.cs_widget_grid_item,null);
        View container = view.findViewById(R.id.container);
        if (mWidgetItems.get(position).getImage() != null) {
            ImageLoader.getInstance().displayRemoteImage(mWidgetItems.get(position).getImage().get(0).getUrl(), 0, rv,
                    R.id.image, AppearanceManager.getInstance().csWidgetAppearance().getCorners(),
                    (1f * container.getLayoutParams().height) / container.getLayoutParams().width);
        } else {
            ImageLoader.getInstance().displayRemoteColor(mWidgetItems.get(position).backgroundColor, 0, rv,
                    R.id.image, AppearanceManager.getInstance().csWidgetAppearance().getCorners(),
                    (1f * container.getLayoutParams().height) / container.getLayoutParams().width);
        }
        Bundle extras = new Bundle();
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        Log.e("widgetPosition", position + "");
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 2;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

}
