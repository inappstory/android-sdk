package com.inappstory.sdk.stories.ui.reader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.network.JsonParser;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.OverlappingContainerActions;
import com.inappstory.sdk.stories.callbacks.ShareCallback;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.stories.ui.ScreensManager;

import java.util.HashMap;

public class OverlapFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cs_overlap_dialog_fragment, null);
    }

    FrameLayout readerTopContainer;
    ShareCallback callback = CallbackManager.getInstance().getShareCallback();


    OverlappingContainerActions shareActions = new OverlappingContainerActions() {
        @Override
        public void closeView(HashMap<String, Object> data) {
            boolean shared = false;
            if (data.containsKey("shared")) shared = (boolean) data.get("shared");
            ScreensManager.getInstance().setTempShareStatus(shared);
            OverlapFragmentObserver observer = ScreensManager.getInstance().overlapFragmentObserver;
            if (observer != null) observer.closeView(data);
            ScreensManager.getInstance().cleanOverlapFragmentObserver();
            dismissAllowingStateLoss();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OverlapFragmentObserver observer = ScreensManager.getInstance().overlapFragmentObserver;
        if (observer != null) observer.viewIsOpened();
        setStyle(DialogFragment.STYLE_NO_FRAME,
                R.style.OverlapDialogTheme);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                    boolean click = (keyCode == KeyEvent.KEYCODE_BACK)
                            && (event.getAction() == KeyEvent.ACTION_UP);
                    return click && !callback.onBackPress(shareActions);
                }
            });
            View decorView = dialog.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            decorView.setSystemUiVisibility(uiOptions);
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
                    JsonParser.fromJson(
                            getArguments().getString("shareData"), IASShareData.class
                    )
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
}
