package com.inappstory.sdk.game.reader;

import static android.widget.RelativeLayout.CENTER_IN_PARENT;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.ui.views.IGameLoaderView;
import com.inappstory.sdk.stories.utils.Sizes;

public class GameLoadProgressBar extends ProgressBar implements IGameLoaderView {
    public GameLoadProgressBar(Context context) {
        super(context);
        init();
    }

    public GameLoadProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameLoadProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setIndeterminate(false);
        setProgressDrawable(getResources().getDrawable(R.drawable.cs_circular_progress_bar));
        setBackground(getResources().getDrawable(R.drawable.cs_circle_shape));
        setMax(100);
        setProgress(0);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                Sizes.dpToPxExt(40, getContext()),
                Sizes.dpToPxExt(40, getContext())
        );
        lp.addRule(CENTER_IN_PARENT);
        setLayoutParams(lp);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setProgress(int progress, int max) {
        super.setMax(max);
        super.setProgress(progress);
    }
}
