package com.inappstory.sdk.stories.ui;

import java.util.HashMap;

public interface OverlapFragmentObserver {
    void closeView(HashMap<String, Object> data);

    void viewIsOpened();

    void viewIsClosed();
}
