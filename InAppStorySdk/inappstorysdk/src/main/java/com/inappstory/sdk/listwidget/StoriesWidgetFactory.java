package com.inappstory.sdk.listwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.inappstory.sdk.WidgetAppearance;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.exceptions.DataException;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.Image;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.stories.utils.Sizes;

import java.lang.ref.SoftReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
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
        setStories();
    }

    public void setStories() {
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


    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mWidgetItems.size();
    }

    HashMap<String, SoftReference<Bitmap>> bmps = new HashMap<>();

    @Override
    public RemoteViews getViewAt(int position) {
        if (ImageLoader.getInstance() == null) {
            new ImageLoader(mContext);
        }
        if (bmps == null) bmps = new HashMap<>();
        View view = View.inflate(mContext, R.layout.cs_widget_grid_item, null);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.cs_widget_grid_item);
        rv.setTextViewText(R.id.title, mWidgetItems.get(position).getTitle());
        View container = view.findViewById(R.id.container);
        View title = view.findViewById(R.id.title);
        View image = view.findViewById(R.id.image);
        Float containerRatio = null;
        if (container != null)
            containerRatio = container.getLayoutParams() != null && container.getLayoutParams().width > 0 ?
                    container.getLayoutParams().width / (1f * container.getLayoutParams().height) : null;
        WidgetAppearance widgetAppearance = AppearanceManager.csWidgetAppearance();
        if (widgetAppearance.getWidgetClass() == null) {
            if (!SharedPreferencesAPI.hasContext()) {
                SharedPreferencesAPI.setContext(mContext);
            }
            widgetAppearance = JsonParser.fromJson(
                    SharedPreferencesAPI.getString("lastWidgetAppearance"), WidgetAppearance.class);
            if (widgetAppearance == null) {
                widgetAppearance = AppearanceManager.csWidgetAppearance();
            } else if (widgetAppearance.getRatio() == null ||
                    (containerRatio != null && containerRatio != widgetAppearance.getRatio())) {
                widgetAppearance.setRatio(containerRatio);
                widgetAppearance.save();
            }
        }

        if (title != null && widgetAppearance.getTextColor() != null)
            rv.setTextColor(R.id.title, widgetAppearance.getTextColor());
        if (image != null)
            try {

                if (mWidgetItems.get(position).getImage() != null) {
                    ImageLoader.getInstance().displayRemoteImage(mWidgetItems.get(position).getImage().get(0).getUrl(), 0, rv,
                            R.id.image, widgetAppearance.getCorners(),
                            containerRatio != null ?
                                    containerRatio : widgetAppearance.getRatio());
                } else {
                    ImageLoader.getInstance().displayRemoteColor(mWidgetItems.get(position).backgroundColor, 0, rv,
                            R.id.image, widgetAppearance.getCorners(),
                            containerRatio != null ?
                                    containerRatio : widgetAppearance.getRatio());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        Intent clickIntent = new Intent();
        clickIntent.putExtra(StoriesWidgetService.POSITION, position);
        clickIntent.putExtra(StoriesWidgetService.ID, mWidgetItems.get(position).id);
        if (container != null)
            rv.setOnClickFillInIntent(R.id.container, clickIntent);
        return rv;
    }


    @Override
    public RemoteViews getLoadingView() {
        Log.e("MyWidget", "getLoadingView");
        return null;
    }


    @Override
    public int getViewTypeCount() {
        Log.e("MyWidget", "getViewTypeCount");
        return mWidgetItems.size();
    }

    @Override
    public long getItemId(int position) {
        Log.e("MyWidget", "getItemId " + position);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

}
