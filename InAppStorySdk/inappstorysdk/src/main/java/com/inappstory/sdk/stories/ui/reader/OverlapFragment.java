package com.inappstory.sdk.stories.ui.reader;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.R;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.share.ShareListener;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.OverlappingContainerActions;
import com.inappstory.sdk.stories.callbacks.ShareCallback;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.core.ui.screens.ScreensManager;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;

import java.util.HashMap;

public class OverlapFragment extends Fragment implements IASBackPressHandler {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cs_overlap_dialog_fragment, container, false);
    }

    FrameLayout readerTopContainer;
    ShareCallback callback = CallbackManager.getInstance().getShareCallback();


    OverlappingContainerActions shareActions = new OverlappingContainerActions() {
        @Override
        public void closeView(HashMap<String, Object> data) {
            boolean shared = false;
            if (data.containsKey("shared")) shared = (boolean) data.get("shared");

            OverlapFragmentObserver observer = ScreensManager.getInstance().overlapFragmentObserver;
            if (observer != null) observer.closeView(data);
            ScreensManager.getInstance().cleanOverlapFragmentObserver();
            try {
                getParentFragmentManager().popBackStack();
            } catch (IllegalStateException e) {
                ScreensManager.getInstance().setTempShareStatus(shared);
            }
            if (shareListener != null) {
                if (shared)
                    shareListener.onSuccess(true);
                else
                    shareListener.onCancel();
            }
        }
    };

    private boolean closed = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OverlapFragmentObserver observer = ScreensManager.getInstance().overlapFragmentObserver;
        if (observer != null) observer.viewIsOpened();
    }


    @Override
    public void onResume() {
        super.onResume();
        Boolean shared = ScreensManager.getInstance().getTempShareStatus();
        if (shared != null) {
            getParentFragmentManager().popBackStack();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readerTopContainer = view.findViewById(R.id.ias_stories_top_container);
        Context context = getContext();
        if (callback != null && context != null) {
            HashMap<String, Object> content = new HashMap<>();
            content.put("slidePayload", getArguments().getString("slidePayload"));
            content.put("storyId", getArguments().getInt("storyId"));
            content.put("slideIndex", getArguments().getInt("slideIndex"));
            content.put("shareData",
                    (IASShareData) getArguments().getSerializable("shareData")
            );
            readerTopContainer.removeAllViews();
            View shareView = callback.getView(context, content, shareActions);
            shareView.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    )
            );
            readerTopContainer.addView(shareView);
            readerTopContainer.setVisibility(View.VISIBLE);
            callback.viewIsVisible(shareView);
        }
    }

    public ShareListener shareListener;

    @Override
    public boolean onBackPressed() {
        return callback.onBackPress(shareActions);
    }
}
