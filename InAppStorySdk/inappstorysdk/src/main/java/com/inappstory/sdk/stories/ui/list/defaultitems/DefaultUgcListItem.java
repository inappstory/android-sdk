package com.inappstory.sdk.stories.ui.list.defaultitems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.ui.list.UGCListItemSimpleAppearance;
import com.inappstory.sdk.stories.ui.views.RoundedCornerLayout;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.ugc.list.IStoriesListUGCItem;

public class DefaultUgcListItem implements IStoriesListUGCItem {
    AppearanceManager appearanceManager;
    Context context;
    View container;
    private RoundedCornerLayout cornerLayout;

    public DefaultUgcListItem(AppearanceManager appearanceManager, Context context) {
        this.context = context;
        this.appearanceManager = appearanceManager;
    }

    private void setContainerSize() {
        if (container == null) return;
        if (appearanceManager.getRealHeight(context) != null) {
            container.getLayoutParams().height = appearanceManager.getRealHeight(context);
        }
        if (appearanceManager.getRealWidth(context) != null) {
            container.getLayoutParams().width = appearanceManager.getRealWidth(context);
        }
        container.requestLayout();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setDefaultViews() {
        if (cornerLayout != null) {
            cornerLayout.setRadius(Math.max(appearanceManager.csListItemRadius(context) - Sizes.dpToPxExt(4, context), 0));
            cornerLayout.setBackgroundColor(Color.TRANSPARENT);
        }
        UGCListItemSimpleAppearance ugcListItemAppearance = appearanceManager.csUGCListItemSimpleAppearance();
        if (ugcListItemAppearance != null) {
            RelativeLayout.LayoutParams imageLp = (RelativeLayout.LayoutParams) image.getLayoutParams();
            int backgroundColor = getNonNullValue(ugcListItemAppearance.csBackgroundColor(),
                    Color.parseColor("#0C62F3"));
            int iconId = getNonNullValue(ugcListItemAppearance.csIconId(),
                    R.drawable.ic_new_ugc);
            int iconMargin = getNonNullValue(ugcListItemAppearance.csIconMargin(),
                    Sizes.dpToPxExt(16, context));
            image.setImageDrawable(context.getResources().getDrawable(iconId));
            imageLp.setMargins(iconMargin, iconMargin, iconMargin, iconMargin);
            background.setBackgroundColor(backgroundColor);
            if (ugcListItemAppearance.csIconColor() != null) {
                image.setColorFilter(ugcListItemAppearance.csIconColor());
            }
            image.requestLayout();
        }
    }
    private int getNonNullValue(Integer value, int defValue) {
        return value != null ? value : defValue;
    }

    private void bindViews(View parent) {
        container = parent.findViewById(R.id.container);
        cornerLayout = parent.findViewById(R.id.item_cv);
        image = parent.findViewById(R.id.image);
        background = parent.findViewById(R.id.background);
    }
    AppCompatImageView image;
    View background;

    @Override
    public View getView() {
        View v = LayoutInflater.from(context).inflate(
                R.layout.cs_story_list_inner_ugc,
                null,
                false
        );
        bindViews(v);
        setContainerSize();
        setDefaultViews();
        return v;
    }
}
