package com.inappstory.sdk.stories.ui.utils;

import androidx.fragment.app.Fragment;

public interface FragmentAction<T extends Fragment> {
    void invoke(T fragment);

    void error();
}
