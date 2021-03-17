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
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.exceptions.DataException;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.stories.utils.Sizes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StoriesWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    List<Story> mWidgetItems = new ArrayList<>();
    private Context mContext;
    private int mAppWidgetId;

    public StoriesWidgetFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        setStories();
        if (ImageLoader.getInstance() == null) {
            new ImageLoader(mContext);
        }
        CsEventBus.getDefault().register(this);
    }

    @CsSubscribe
    public void refreshData(ListLoadedEvent event) {
        Log.e("MyWidget", "ListLoadedEvent");
        setStories();

    }

    void setStories() {
        if (!SharedPreferencesAPI.hasContext()) {
            SharedPreferencesAPI.setContext(mContext);
        }
        String wStories = SharedPreferencesAPI.getString("widgetStories");
        if (wStories != null)
            mWidgetItems = JsonParser.listFromJson(SharedPreferencesAPI.getString("widgetStories"), Story.class);
    }

    @Override
    public void onCreate() {

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
        View view = View.inflate(mContext, R.layout.cs_widget_grid_item, null);
        View container = view.findViewById(R.id.container);

        if (mWidgetItems.get(position).getImage() != null) {
            ImageLoader.getInstance().displayRemoteImage(mWidgetItems.get(position).getImage().get(0).getUrl(), 0, rv,
                    R.id.image, AppearanceManager.getInstance().csWidgetAppearance().getCorners(),
                    (container.getLayoutParams() != null && container.getLayoutParams().width != 0 ?
                    (1f * container.getLayoutParams().height) / container.getLayoutParams().width : null));
        } else {
            ImageLoader.getInstance().displayRemoteColor(mWidgetItems.get(position).backgroundColor, 0, rv,
                    R.id.image, AppearanceManager.getInstance().csWidgetAppearance().getCorners(),
                    (container.getLayoutParams() != null && container.getLayoutParams().width != 0 ?
                            (1f * container.getLayoutParams().height) / container.getLayoutParams().width : null));
        }
        Intent clickIntent = new Intent();
        clickIntent.putExtra(StoriesWidgetService.POSITION, position);
        clickIntent.putExtra(StoriesWidgetService.ID, mWidgetItems.get(position).id);
        rv.setOnClickFillInIntent(R.id.container, clickIntent);
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
        return mWidgetItems.size();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

}
