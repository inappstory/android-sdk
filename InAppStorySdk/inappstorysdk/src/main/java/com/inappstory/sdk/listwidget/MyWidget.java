package com.inappstory.sdk.listwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.Arrays;

public class MyWidget extends AppWidgetProvider {

    final String LOG_TAG = "MyWidget";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.e(LOG_TAG, "onEnabled");
        AppearanceManager.csWidgetAppearance(Color.WHITE, Sizes.dpToPxExt(16, context));
    }


    public static final String UPDATE_ALL_WIDGETS = "ias.UPDATE_WIDGETS";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(LOG_TAG, "onReceive");
        if (intent.getAction().equalsIgnoreCase(UPDATE_ALL_WIDGETS)) {
            AppearanceManager.csWidgetAppearance(Color.BLUE, Sizes.dpToPxExt(8, context));
            ComponentName thisAppWidget = new ComponentName(
                    context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            //update(appWidgetManager, context, ids);
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.storiesGrid);
        }
        super.onReceive(context, intent);
    }


    void update(AppWidgetManager appWidgetManager, Context context, int[] appWidgetIds) {

        for (int i = 0; i < appWidgetIds.length; ++i) {

            Intent intent = new Intent(context, GridWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.cs_widget_stories_list);

            rv.setRemoteAdapter(appWidgetIds[i], R.id.storiesGrid, intent);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Log.e(LOG_TAG, "onUpdate");
        update(appWidgetManager, context, appWidgetIds);
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.e(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.e(LOG_TAG, "onDisabled");
    }

}