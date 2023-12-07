package com.inappstory.sdk.stories.ui.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BindFragment extends Fragment {
    protected boolean successfulBind = true;

    protected abstract void bindViews(View view);

    protected abstract int getLayoutId();

    @NonNull
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View v = inflater.inflate(getLayoutId(), container, false);
        bindViews(v);
        return v;
    }
}
