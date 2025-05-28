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

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.stories.callbacks.OverlappingContainerActions;
import com.inappstory.sdk.stories.callbacks.ShareCallback;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class OverlapFragment extends Fragment implements IASBackPressHandler {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cs_overlap_dialog_fragment, container, false);
    }

    FrameLayout readerTopContainer;
    View shareView;

    OverlappingContainerActions shareActions;

    private boolean closed = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                OverlapFragmentObserver observer =
                        core
                                .screensManager()
                                .getShareProcessHandler()
                                .overlapFragmentObserver();
                if (observer != null) observer.viewIsOpened();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                Boolean shared = core
                        .screensManager()
                        .getShareProcessHandler()
                        .getTempShareStatus();
                if (shared != null) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().useCallback(IASCallbackType.SHARE_ADDITIONAL,
                        new UseIASCallback<ShareCallback>() {
                            @Override
                            public void use(@NonNull ShareCallback callback) {
                                if (shareView != null)
                                    callback.onDestroyView(shareView);
                            }

                            @Override
                            public void onDefault() {
                                getParentFragmentManager().popBackStack();
                            }
                        }
                );
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shareActions = new ShareOverlappingContainerActions(getParentFragmentManager());
        readerTopContainer = view.findViewById(R.id.ias_stories_top_container);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().useCallback(IASCallbackType.SHARE_ADDITIONAL,
                        new UseIASCallback<ShareCallback>() {
                            @Override
                            public void use(@NonNull ShareCallback callback) {
                                Context context = getContext();
                                if (context == null) return;
                                HashMap<String, Object> content = new HashMap<>();
                                content.put("slidePayload", getArguments().getString("slidePayload"));
                                content.put("storyId", getArguments().getInt("storyId"));
                                content.put("slideIndex", getArguments().getInt("slideIndex"));
                                content.put("shareData",
                                        (IASShareData) getArguments().getSerializable("shareData")
                                );
                                readerTopContainer.removeAllViews();
                                shareView = callback.getView(context, content, shareActions);
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
                );
            }
        });
    }

    private WeakReference<View> shareViewRef;

    @Override
    public boolean onBackPressed() {
        if (shareView != null) {
            final boolean[] res = {false};
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.callbacksAPI().useCallback(IASCallbackType.SHARE_ADDITIONAL,
                            new UseIASCallback<ShareCallback>() {
                                @Override
                                public void use(@NonNull ShareCallback callback) {
                                    res[0] = callback.onBackPress(shareView, shareActions);
                                }

                                @Override
                                public void onDefault() {
                                    getParentFragmentManager().popBackStack();
                                }
                            }
                    );
                }
            });
            return res[0];
        }
        getParentFragmentManager().popBackStack();
        return true;
    }
}
