package com.inappstory.sdk.core.ui.widgets.bs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.R;

public class BaseBottomSheetContainer extends BottomSheetContainer {


    public BaseBottomSheetContainer(
            @NonNull Context context
    ) {
        super(context);
    }

    public BaseBottomSheetContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
    }

    public BaseBottomSheetContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    public BaseBottomSheetContainer(
            @NonNull Context context,
            @NonNull BaseConfig config
    ) {
        super(context, config);
    }

    @NonNull
    @Override
    protected FrameLayout onCreateSheetContentView(@NonNull Context context) {
        return (FrameLayout) LayoutInflater.from(context).inflate(
                R.layout.cs_empty_layout,
                this,
                false
        );
    }
}
