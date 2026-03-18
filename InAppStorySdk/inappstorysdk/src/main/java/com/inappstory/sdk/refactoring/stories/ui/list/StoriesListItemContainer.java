package com.inappstory.sdk.refactoring.stories.ui.list;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListItemCoverState;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListItemState;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListItemViewModel;
import com.inappstory.sdk.refactoring.stories.ui.views.IStoriesListItem;

import java.util.Objects;
import java.util.UUID;

public class StoriesListItemContainer
        extends RecyclerView.ViewHolder
        implements Observer<StoriesListItemState> {
    private StoriesListItemState currentState;
    private StoriesListItemViewModel listItemViewModel;
    private IStoriesListItem storiesListItem;
    private AppearanceManager appearanceManager;
    private String uuid = UUID.randomUUID().toString();

    public ViewGroup getParent() {
        return parent;
    }

    ViewGroup parent = null;

    public StoriesListItemContainer(
            @NonNull View itemView,
            ViewGroup parent,
            IStoriesListItem storiesListItem
    ) {
        super(itemView);
        this.storiesListItem = storiesListItem;
        this.parent = parent;
    }

    private View getDefaultVideoCell() {
        IStoriesListItem iStoriesListItem = storiesListItem;
        if (iStoriesListItem == null) return null;
        return iStoriesListItem.getVideoView() != null ?
                iStoriesListItem.getVideoView() :
                iStoriesListItem.getView();
    }

    private View getDefaultCell() {
        IStoriesListItem iStoriesListItem = storiesListItem;
        if (iStoriesListItem == null) return null;
        return iStoriesListItem.getView();
    }


    private boolean viewCanBeUsed() {
        ViewGroup parent = this.parent;
        if (parent == null) return false;
        if (!parent.isAttachedToWindow()) return false;
        Context context = parent.getContext();
        if (context == null)
            return false;
        if (context instanceof Activity) {
            return !((Activity) context).isFinishing() && !((Activity) context).isDestroyed();
        }
        return true;
    }


    public void attachView(
            StoriesListItemViewModel viewModel,
            IStoriesListItem storiesListItem,
            AppearanceManager appearanceManager
    ) {
        this.listItemViewModel = viewModel;
        this.storiesListItem = storiesListItem;
        this.appearanceManager = appearanceManager;
        if (currentState != null)
            stateIsCreated(currentState);
        viewModel.addSubscriber(this);
    }

    public void detachView() {
        this.storiesListItem = null;
        if (this.listItemViewModel != null)
            this.listItemViewModel.removeSubscriber(this);
        this.listItemViewModel = null;
        this.appearanceManager = null;
        // this.currentState = null;
    }

    Handler handler = new Handler(Looper.getMainLooper());
    ViewGroup vg;
    private void stateIsCreated(StoriesListItemState value) {

        vg = itemView.findViewById(R.id.baseLayout);
        vg.removeAllViews();
        if (value.hasVideoUrl()) {
            vg.addView(getDefaultVideoCell());
        } else {
            vg.addView(getDefaultCell());
        }
        stateIsUpdated(value);
    }

    private void stateIsUpdated(StoriesListItemState value) {

        IStoriesListItem storiesListItem = this.storiesListItem;
        if (storiesListItem == null) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!viewCanBeUsed())
                    return;
                storiesListItem.setId(
                        itemView,
                        value.id()
                );
                storiesListItem.setTitle(
                        itemView,
                        value.title(),
                        ColorUtils.parseColorRGBA(value.titleColor())
                );
                storiesListItem.setHasAudio(itemView, value.hasAudio());
                storiesListItem.setOpened(itemView, value.isOpened());
            }
        });
        updateImageCover(value.coverState());
        if (value.hasVideoUrl())
            updateVideoCover(value.coverState());
    }

    private void updateImageCover(StoriesListItemCoverState coverState) {

        IStoriesListItem storiesListItem = this.storiesListItem;
        if (coverState == null)
            coverState = new StoriesListItemCoverState();
        if (storiesListItem != null) {
            StoriesListItemCoverState finalCoverState = coverState;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!viewCanBeUsed()) return;
                    storiesListItem.setImage(
                            itemView,
                            finalCoverState.imagePath(),
                            ColorUtils.parseColorRGBA(
                                    finalCoverState.backgroundColor()
                            )
                    );
                }
            });

        }
    }

    private void updateVideoCover(StoriesListItemCoverState coverState) {
        IStoriesListItem storiesListItem = this.storiesListItem;
        if (coverState == null) coverState = new StoriesListItemCoverState();
        if (storiesListItem != null) {
            StoriesListItemCoverState finalCoverState = coverState;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!viewCanBeUsed()) return;
                    storiesListItem.setVideo(itemView, finalCoverState.videoPath());
                }
            });
        }
    }

    private void updateOpenedStatus(boolean isOpened) {
        if (!viewCanBeUsed()) return;
        IStoriesListItem storiesListItem = this.storiesListItem;
        if (storiesListItem != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!viewCanBeUsed()) return;
                    storiesListItem.setOpened(itemView, isOpened);
                }
            });
        }
    }

    @Override
    public void onUpdate(StoriesListItemState newValue) {
        if (Objects.equals(newValue, currentState)) return;
        StoriesListItemState current = currentState != null ? currentState.copy() : null;
        currentState = newValue;
        if (newValue == null) {
            return;
        }
        Log.e("listUpdateCurrentState", uuid + " onUpdate " + newValue);
        if (current == null) {
            stateIsCreated(newValue);
            listItemViewModel.initCover(appearanceManager.csCoverQuality());
            listItemViewModel.loadCoverResources(appearanceManager.csCoverQuality());
        } else {
            stateIsUpdated(newValue);
            if (current.isOpened() != newValue.isOpened()) {
                updateOpenedStatus(newValue.isOpened());
            }
            StoriesListItemCoverState newCoverState = newValue.coverState();
            if (newCoverState != null) {
                if (!Objects.equals(current.coverState(), newCoverState)) {
                    if (current.coverState() == null) {
                        updateImageCover(newCoverState);
                        if (newCoverState.videoPath() != null) {
                            updateVideoCover(newCoverState);
                        }
                    } else {
                        if (!Objects.equals(current.coverState().imagePath(),
                                newCoverState.imagePath())) {
                            updateImageCover(newCoverState);
                        }
                        if (!Objects.equals(current.coverState().videoPath(),
                                newCoverState.videoPath())) {
                            updateVideoCover(newCoverState);
                        }
                    }
                }
            }
        }
    }
}
