package com.inappstory.sdk.stories.ui.widgets.readerscreen.generated;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.inappstory.sdk.eventbus.CsEventBus;

public class GeneratedImageView extends AppCompatImageView implements GeneratedViewCallback {
    boolean isLoaded;


    public GeneratedImageView(@NonNull Context context) {
        super(context);
    }

    public GeneratedImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GeneratedImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onLoaded() {
        isLoaded = true;
        CsEventBus.getDefault().post(new CheckGeneratorEvent());
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }
}
