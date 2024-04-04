package com.inappstory.sdk.game.reader;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inappstory.sdk.stories.ui.views.IGameReaderLoaderView;
import com.inappstory.sdk.stories.utils.Sizes;


public class GameReaderLoadProgressBarWithText extends RelativeLayout implements IGameReaderLoaderView {

    private boolean isIndeterminate = true;

    GameReaderLoadProgressBar progressBar;
    TextView progressText;


    public GameReaderLoadProgressBarWithText(Context context) {
        this(context, null);
    }

    private final int strokeWidthDP = 4;

    private final int sizeDP = 36;
    private final boolean rounded = false;


    private static Paint COLOR_PAINT;
    private static Paint GRADIENT_PAINT;


    public GameReaderLoadProgressBarWithText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameReaderLoadProgressBarWithText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Sizes.dpToPxExt((int)(2.5 * sizeDP), getContext())
        );
        lp.addRule(CENTER_IN_PARENT);
        setLayoutParams(lp);
        initProgressBar();
        initProgressText();
    }

    private void initProgressBar() {
        this.progressBar = new GameReaderLoadProgressBar(getContext());
        this.progressBar.setIndeterminate(true);
        addView(progressBar);
    }

    private void initProgressText() {
        progressText = new TextView(getContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.addRule(ALIGN_PARENT_BOTTOM);
        lp.addRule(CENTER_HORIZONTAL);
        progressText.setLayoutParams(lp);
        progressText.setTextColor(Color.WHITE);
        progressText.setText("0 %");
        addView(progressText);
    }

    @Override
    public View getView(Context context) {
        return this;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void setProgress(int progress, int max) {
        int currentProgress = (max == 0) ? 0 : (int) (100 * ((1f * progress) / max));
        currentProgress = Math.min(currentProgress, 100);
        this.progressBar.setProgress(currentProgress, 100);
        this.progressText.setText(String.format("%d %%", currentProgress));
    }

    @Override
    public void setIndeterminate(boolean indeterminate) {
        this.isIndeterminate = indeterminate;
        // this.progressText.setVisibility(indeterminate ? INVISIBLE : VISIBLE);
    }
}
