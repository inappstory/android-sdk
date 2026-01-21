package com.inappstory.sdk.inappmessage.ui.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SizeF;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.widgets.customicons.CustomIconWithoutStates;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.stories.ui.widgets.TouchFrameLayout;
import com.inappstory.sdk.stories.utils.Sizes;

public abstract class IAMContentContainer<T extends InAppMessageAppearance> extends FrameLayout {
    public static @IdRes int CONTENT_ID = R.id.ias_iam_reader_content;
    public static @IdRes int CONTAINER_ID = R.id.ias_iam_reader_container;
    protected View background;
    protected FrameLayout loaderContainer;
    protected FrameLayout refreshContainer;
    protected TouchFrameLayout refresh;
    protected Rect externalContainerRect = new Rect();
    protected FrameLayout content;
    protected boolean closeEnabled = true;

    public abstract void clearContentBackground();

    protected IAMContainerCallback callback;

    public void showContent() {
    }

    public void setRefreshClick(final OnClickListener clickListener) {
        refresh.setClickListener(v -> {
            clickListener.onClick(v);
            try {
                final CustomIconWithoutStates refreshIconInterface =
                        AppearanceManager.getCommonInstance().csCustomIcons().refreshIcon();
                refreshIconInterface.clickEvent(refresh.getChildAt(0));
            } catch (Exception e) {

            }
        });
    }

    public void showLoader() {
        refreshContainer.setVisibility(GONE);
        loaderContainer.setVisibility(VISIBLE);
        loaderContainer.setAlpha(1f);
    }

    public void hideRefresh() {
        refreshContainer
                .animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        refreshContainer.setVisibility(GONE);
                    }
                });
    }

    public void showRefresh() {
        loaderContainer.setVisibility(GONE);
        refreshContainer.setVisibility(VISIBLE);
        refreshContainer.setAlpha(1);
    }

    public void hideLoader() {
        loaderContainer
                .animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        loaderContainer.setVisibility(GONE);
                    }
                });
    }

    public abstract void showWithAnimation();

    public abstract void showWithoutAnimation();

    public abstract void closeWithAnimation();

    public abstract void closeWithoutAnimation();

    public void uiContainerCallback(IAMContainerCallback callback) {
        this.callback = callback;
    }

    protected void generateRefresh() {
        Context context = getContext();
        int maxRefreshSize = Sizes.dpToPxExt(40, context);
        final CustomIconWithoutStates refreshIconInterface = AppearanceManager.getCommonInstance().csCustomIcons().refreshIcon();
        final View refreshView = refreshIconInterface.createIconView(context, new SizeF(maxRefreshSize, maxRefreshSize));
        refreshView.setClickable(false);
        refresh = new TouchFrameLayout(context);
        refresh.addView(refreshView);
        refresh.setTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                refreshIconInterface.touchEvent(refreshView, event);
                return false;
            }
        });
        FrameLayout.LayoutParams refreshLP = new FrameLayout.LayoutParams(
                maxRefreshSize,
                maxRefreshSize
        );
        refreshLP.gravity = Gravity.CENTER;
        refresh.setLayoutParams(
                refreshLP
        );

        refreshContainer = new FrameLayout(getContext());
        refreshContainer.setClickable(true);
        refreshContainer.setBackgroundColor(Color.TRANSPARENT);
        refreshContainer.setVisibility(GONE);

        refreshContainer.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        refreshContainer.setBackgroundColor(Color.RED);
        refreshContainer.addView(refresh);
    }

    protected void generateLoader(int backgroundColor) {
        double contrast1 = ColorUtils.getColorsContrast(backgroundColor, Color.BLACK);
        double contrast2 = ColorUtils.getColorsContrast(backgroundColor, Color.WHITE);
        loaderContainer = new FrameLayout(getContext());
        loaderContainer.setClickable(true);
        loaderContainer.setBackgroundColor(Color.TRANSPARENT);
        loaderContainer.setVisibility(GONE);
        loaderContainer.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );


        View loader = AppearanceManager.getLoader(
                getContext(),
                contrast1 > contrast2 ?
                        Color.BLACK : Color.WHITE
        );
        loaderContainer.addView(loader);
    }

    protected void init(Context context) {
        //setVisibility(INVISIBLE);
        background = new View(context);
        background.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        background.setBackgroundColor(
                ColorUtils.parseColorRGBA(
                        "#0000005A" //black 0.35
                )
        );
        background.setClickable(true);
        background.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeEnabled)
                    closeWithAnimation();
            }
        });
        addView(background);
        setId(CONTAINER_ID);
    }

    protected abstract void visibleRectIsCalculated();

    public interface AppearanceCallback {
        void invoke();
    }

    public void appearance(T appearance) {
        this.appearance = appearance;
        if (appearance != null) {
            this.closeEnabled = !appearance.disableClose();
        }

        post(new Runnable() {
            @Override
            public void run() {
                getGlobalVisibleRect(externalContainerRect);
                visibleRectIsCalculated();
                content.setVisibility(VISIBLE);
            }
        });
    }

    protected T appearance;


    public IAMContentContainer(@NonNull Context context) {
        super(context);
        init(context);
    }

    public IAMContentContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IAMContentContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
}
