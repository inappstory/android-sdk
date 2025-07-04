package com.inappstory.sdk.ugc.list;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.widgets.roundedlayout.RoundedCornerLayout;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.ui.list.BaseStoryListItem;
import com.inappstory.sdk.stories.ui.list.ClickCallback;
import com.inappstory.sdk.stories.ui.list.UGCListItemSimpleAppearance;
import com.inappstory.sdk.stories.utils.Sizes;

public class UGCListItem extends BaseStoryListItem {

    public UGCListItem(@NonNull View itemView, ViewGroup parent, AppearanceManager manager) {
        super(
                itemView,
                parent,
                manager,
                false,
                true
        );
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
        cv.setRadius(Math.max(manager.csListItemRadius(itemView.getContext()) - Sizes.dpToPxExt(4, itemView.getContext()), 0));
        cv.setBackgroundColor(Color.TRANSPARENT);
        View outerLayout = v.findViewById(R.id.outerLayout);
        if (manager.getRealHeight(itemView.getContext()) != null) {
            outerLayout.getLayoutParams().height = manager.getRealHeight(itemView.getContext());
        }
        if (manager.getRealWidth(itemView.getContext()) != null) {
            outerLayout.getLayoutParams().width = manager.getRealWidth(itemView.getContext());
        }
        UGCListItemSimpleAppearance ugcListItemAppearance = manager.csUGCListItemSimpleAppearance();
        if (ugcListItemAppearance != null) {
            AppCompatImageView image = v.findViewById(R.id.image);
            RelativeLayout.LayoutParams imageLp = (RelativeLayout.LayoutParams) image.getLayoutParams();
            View background = v.findViewById(R.id.background);
            int backgroundColor = getNonNullValue(ugcListItemAppearance.csBackgroundColor(),
                    Color.parseColor("#0C62F3"));
            int iconId = getNonNullValue(ugcListItemAppearance.csIconId(),
                    R.drawable.ic_new_ugc);
            int iconMargin = getNonNullValue(ugcListItemAppearance.csIconMargin(),
                    Sizes.dpToPxExt(16, itemView.getContext()));
            image.setImageDrawable(itemView.getContext().getResources().getDrawable(iconId));
            imageLp.setMargins(iconMargin, iconMargin, iconMargin, iconMargin);
            background.setBackgroundColor(backgroundColor);
            if (ugcListItemAppearance.csIconColor() != null) {
                image.setColorFilter(ugcListItemAppearance.csIconColor());
            }
            image.requestLayout();
        }
        return v;
    }

    private int getNonNullValue(Integer value, int defValue) {
        return value != null ? value : defValue;
    }

    @Override
    public void bind(Integer id, String titleText, Integer titleColor,
                     String imageUrl,
                     Integer backgroundColor, boolean isOpened, boolean hasAudio,
                     String videoUrl, StoryData storyData, ClickCallback callback) {

    }

    @Override
    public void bindFavorite() {

    }

    @Override
    public void bindUGC() {
    }
}
