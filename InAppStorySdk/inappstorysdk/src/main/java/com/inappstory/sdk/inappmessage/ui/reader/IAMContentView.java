package com.inappstory.sdk.inappmessage.ui.reader;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.R;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideState;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderSlideViewModel;
import com.inappstory.sdk.stories.utils.Observer;

public class IAMContentView extends FrameLayout implements Observer<IAMReaderSlideState> {
    IAMWebView webView;
    IAMWebViewController webViewController;
    IIAMReaderSlideViewModel readerSlideViewModel;
    IAMReaderSlideState currentState;

    public IAMContentView(@NonNull Context context) {
        super(context);
    }

    public IAMContentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IAMContentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public IAMContentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        inflate(context, R.layout.cs_inappmessage_content_layout, this);
        webView = findViewById(R.id.webView);
    }

    @Override
    public void onUpdate(IAMReaderSlideState newValue) {

    }
}
