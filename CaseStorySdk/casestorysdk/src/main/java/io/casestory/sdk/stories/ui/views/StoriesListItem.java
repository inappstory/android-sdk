package io.casestory.sdk.stories.ui.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public abstract class StoriesListItem extends View implements IStoriesListItem {
    public StoriesListItem(Context context) {
        super(context);
    }

    public StoriesListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StoriesListItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StoriesListItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
