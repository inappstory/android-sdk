package com.inappstory.sdk.stories.ui.widgets.readerscreen.generated;

import android.view.View;
import android.view.ViewGroup;

public class GeneratedView {
    String type;

    View view;
    ViewGroup viewContainer;

    public void addView(ViewGroup root) {
        if (root == null || view == null) return;
        root.addView(view);
    }

    public GeneratedView(String type, View view, ViewGroup viewContainer) {
        this.type = type;
        this.view = view;
        this.viewContainer = viewContainer;
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

    public ViewGroup getViewContainer() {
        return viewContainer;
    }


}
