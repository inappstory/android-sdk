package com.inappstory.sdk.stories.ui.widgets.readerscreen.generated;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.inappstory.sdk.eventbus.CsEventBus;

public class GeneratedTextView extends AppCompatTextView implements GeneratedViewCallback {

    boolean isLoaded;

    public GeneratedTextView(@NonNull Context context) {
        super(context);
    }

    public GeneratedTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GeneratedTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onLoaded() {
        isLoaded = true;

        CsEventBus.getDefault().post(new CheckGeneratorEvent());
    }

    @Override
    public boolean isLoaded() {
        return true;
    }
}
