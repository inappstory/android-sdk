package com.inappstory.sdk.listwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class GridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        // TODO Auto-generated method stub
        return new MyWidgetFactory(this.getApplicationContext(), intent);
    }

}