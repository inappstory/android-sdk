## Home Screen Widget

When creating a widget, it is possible to add a list of stories. This will display the first 4 elements of the list.
To do this, you need to set the properties of the list using the method:
```
AppearanceManager.csWidgetAppearance(
    Context context, //context, it is best to pass the widget context, required parameter  
    Class widgetClass //widget class (WidgetName.class), required parameter 
    Integer itemCornerRadius //radius of corners of list cells, optional parameter 
)
```

The list is a `GridView`, so when marking up the widget, you need to add the corresponding element .
Example:
```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:gravity="center_horizontal"
    android:orientation="vertical">
    ...
    <GridView
        android:id="@+id/storiesGrid"
        android:layout_width="320dp"
        android:layout_height="90dp"
        android:layout_margin="8dp"
        android:horizontalSpacing="6dp"
        android:numColumns="4"
        android:verticalSpacing="6dp" />
    ...
</LinearLayout>
```

In the manifest file of the widget, you must set a filter for events:
```
<receiver
    android:name=".MyWidget"
    android:label="MyWidget">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
        <action android:name="ias_w.UPDATE_WIDGETS"/> //comes when there is a need to load the list from the server 
        <action android:name="ias_w.UPDATE_SUCCESS_WIDGETS"/> //comes in case of successful receipt of a non-empty list of stories from the server 
        <action android:name="ias_w.UPDATE_NO_CONNECTION"/> //comes in if when trying to get the list from the server failed to connect to the Internet 
        <action android:name="ias_w.UPDATE_EMPTY_WIDGETS"/> //comes in case of receiving an empty list of stories from the server 
        <action android:name="ias_w.UPDATE_AUTH"/> //comes in if the user is not authorized in the InAppStorySDK 
        <action android:name="ias_w.CLICK_ITEM"/> //comes when clicking on a list item of the story widget 
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/widget_metadata"/>                 
</receiver>
```

The corresponding event constants are defined as follows:
```
public static final String UPDATE = "ias_w.UPDATE_WIDGETS";
public static final String CLICK_ITEM = "ias_w.CLICK_ITEM";
public static final String POSITION = "item_position";
public static final String ID = "item_id";
public static final String UPDATE_SUCCESS = "ias_w.UPDATE_SUCCESS_WIDGETS";
public static final String UPDATE_EMPTY = "ias_w.UPDATE_EMPTY_WIDGETS";
public static final String UPDATE_NO_CONNECTION = "ias_w.UPDATE_NO_CONNECTION";
public static final String UPDATE_AUTH = "ias_w.UPDATE_AUTH";
```

In the `onReceive` method of the widget, you can subscribe to them.
Example:
```
@Override
public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equalsIgnoreCase(UPDATE_SUCCESS)) {
        createSuccessData(context);
    } else if (intent.getAction().equalsIgnoreCase(UPDATE)) {
        try {
            StoriesWidgetService.loadData(context);
        } catch (DataException e) {
            e.printStackTrace();
        }
    } else if (intent.getAction().equalsIgnoreCase(UPDATE_EMPTY)) {
        createEmptyWidget();
    } else if (intent.getAction().equalsIgnoreCase(UPDATE_AUTH)) {
        createAuthWidget();
    } else if (intent.getAction().equalsIgnoreCase(UPDATE_NO_CONNECTION)) {
        createNoConnectionWidget();
    } else if (intent.getAction().equalsIgnoreCase(CLICK_ITEM)) {
        int itemId = intent.getIntExtra(StoriesWidgetService.ID, -1);
        int itemPos = intent.getIntExtra(StoriesWidgetService.POSITION, -1);
        if (itemPos != -1) {
            Toast.makeText(context, "Clicked on item " + itemPos + ", id " + itemId, Toast.LENGTH_LONG).show();
        }
    }
    super.onReceive(context, intent);
}
```

Exmaple `createSuccessData()` function:
```
void createSuccessData(final Context context) {
    ComponentName thisAppWidget = new ComponentName(
            context.getPackageName(), getClass().getName());
    final AppWidgetManager appWidgetManager = AppWidgetManager
            .getInstance(context);
    final int appWidgetIds[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
    for (int i = 0; i < appWidgetIds.length; ++i) {

        Intent intent = new Intent(context, StoriesWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.cs_widget_stories_list);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        rv.setRemoteAdapter(appWidgetIds[i], R.id.storiesGrid, intent);
        setClick(rv, context, appWidgetIds[i]);
        appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.storiesGrid);
    }
}

void setClick(RemoteViews rv, Context context, int appWidgetId) {
    Intent listClickIntent = new Intent(context, MyWidget.class);
    listClickIntent.setAction(CLICK_ITEM);
    PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0, listClickIntent, 0);
    rv.setPendingIntentTemplate(R.id.storiesGrid, listClickPIntent);
}
```

The `StoriesWidgetService.loadData (Context context)` method is used directly to load the list. It can be called, for example, from the `onUpdate` or `onEnabled` method of the widget. 
Example:
```
@Override
public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                     int[] appWidgetIds) {
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            try {
                StoriesWidgetService.loadData(context);
            } catch (DataException e) {
                e.printStackTrace();
            }
        }
    }, 500);
    updateData(appWidgetManager, context, appWidgetIds);
    super.onUpdate(context, appWidgetManager, appWidgetIds);
}
```

By default, the cells of the list of the widget are square, 70x70. It is specified in the `cs_widget_grid_item.xml` file. To change, you need to reload this file, while maintaining the identifiers and type of the container, title, image elements. The container element sets the proportions of the cells, so the cell size must be determined in it.
Example:
```
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:gravity="center_horizontal"
    android:orientation="vertical">

    <RelativeLayout
        android:id="@+id/container"
        android:layout_width="70dp"
        android:clickable="true"
        android:layout_height="70dp">

        <ImageView
            android:id="@+id/image"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:clickable="false"
            android:gravity="center"
            android:scaleType="fitCenter" />
        <TextView
            android:id="@+id/title"
            android:layout_width="match_parent"
            android:maxWidth="55dp"
            android:clickable="false"
            android:padding="8dp"
            android:textSize="10sp"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:layout_alignParentBottom="true"
            android:maxLines="3"
            android:textColor="@color/white" />
    </RelativeLayout>
</FrameLayout>
```
