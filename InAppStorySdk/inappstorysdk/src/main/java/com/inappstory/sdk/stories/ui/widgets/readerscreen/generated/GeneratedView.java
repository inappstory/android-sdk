package com.inappstory.sdk.stories.ui.widgets.readerscreen.generated;

import android.view.View;
import android.view.ViewGroup;

public class GeneratedView {
    String type;

    View view;

    public void addView(ViewGroup root) {
        if (root == null || view == null) return;
        root.addView(view);
    }

    public GeneratedView(String type, View view) {
        this.type = type;
        this.view = view;
    }

    public String getType() {
        return type;
    }

    public View getView() {
        return view;
    }


}
