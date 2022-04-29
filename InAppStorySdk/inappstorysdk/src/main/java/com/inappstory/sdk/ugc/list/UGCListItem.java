package com.inappstory.sdk.ugc.list;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.imageloader.RoundedCornerLayout;
import com.inappstory.sdk.stories.ui.list.BaseStoryListItem;
import com.inappstory.sdk.stories.ui.list.ClickCallback;

public class UGCListItem extends BaseStoryListItem {

    public UGCListItem(@NonNull View itemView, AppearanceManager manager) {
        super(itemView, manager,
                false,
                true);
        ViewGroup vg = itemView.findViewById(R.id.baseLayout);
        vg.removeAllViews();
        vg.addView(getDefaultUGCCell());
    }

    protected View getDefaultUGCCell() {
        if (getUGCListItem != null && getUGCListItem.getView() != null) {
            return getUGCListItem.getView();
        }
        View v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.cs_story_list_inner_ugc, null, false);
        RoundedCornerLayout cv = v.findViewById(R.id.inner_cv);
        cv.setRadius(manager.csListItemRadius());
        cv.setBackgroundColor(Color.WHITE);
        title = v.findViewById(R.id.title);
        return v;
    }

    @Override
    public void bind(Integer id, String titleText, Integer titleColor,
                     String sourceText, String imageUrl, String imagePath,
                     Integer backgroundColor, boolean isOpened, boolean hasAudio,
                     String videoUrl, ClickCallback callback) {

    }

    @Override
    public void bindFavorite() {

    }

    @Override
    public void bindUGC() {

    }
}
