package com.inappstory.sdk.inappmessage.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.R;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;

public abstract class IAMContentContainer<T extends InAppMessageAppearance> extends FrameLayout {
    public static @IdRes int CONTENT_ID = R.id.ias_iam_reader_content;
    public static @IdRes int CONTAINER_ID = R.id.ias_iam_reader_container;

    protected IAMContainerCallback callback;

    public abstract void showWithAnimation();

    public abstract void showWithoutAnimation();

    public abstract void closeWithAnimation();

    public abstract void closeWithoutAnimation();

    public void uiContainerCallback(IAMContainerCallback callback) {
        this.callback = callback;
    }

    protected abstract void init(Context context);

    public void appearance(T appearance) {
        this.appearance = appearance;
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
