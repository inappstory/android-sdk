package com.inappstory.sdk.inappmessage.ui.reader;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class IAMContentFragment extends Fragment {
    WebView contentWebView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentWebView = new WebView(view.getContext().getApplicationContext());
    }
}
