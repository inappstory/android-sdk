package com.inappstory.sdk.stories.ui.list.items.ugceditor;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.ui.list.items.BaseStoriesListItem;

public class StoriesListUgcEditorItem extends BaseStoriesListItem {

    public StoriesListUgcEditorItem(@NonNull View itemView, AppearanceManager manager) {
        super(itemView, manager);
        ViewGroup vg = itemView.findViewById(R.id.baseLayout);
        vg.removeAllViews();
        vg.addView(getDefaultUGCCell());
    }

    protected View getDefaultUGCCell() {
        return getUGCListItem.getView();
    }
}
