package com.inappstory.sdk.listwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.WidgetAppearance;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.core.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;

import java.lang.ref.SoftReference;
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
    }

    public void refreshData() {
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
                                    containerRatio : widgetAppearance.getRatio(), mContext);
                } else {
                    ImageLoader.getInstance().displayRemoteColor(mWidgetItems.get(position).backgroundColor, 0, rv,
                            R.id.image, widgetAppearance.getCorners(),
                            containerRatio != null ?
                                    containerRatio : widgetAppearance.getRatio(), mContext);
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
        return null;
    }


    @Override
    public int getViewTypeCount() {
        return mWidgetItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
