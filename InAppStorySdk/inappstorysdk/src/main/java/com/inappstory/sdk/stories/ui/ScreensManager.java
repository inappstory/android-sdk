package com.inappstory.sdk.stories.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.ui.reader.StoriesActivity;
import com.inappstory.sdk.stories.ui.reader.StoriesDialogFragment;
import com.inappstory.sdk.stories.ui.reader.StoriesFixedActivity;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;

import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_OVERSCROLL;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static com.inappstory.sdk.AppearanceManager.CS_DISLIKE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_FAVORITE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_FAVORITE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_LIKE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_SHARE;
import static com.inappstory.sdk.AppearanceManager.CS_LIKE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_REFRESH_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_SHARE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_SOUND_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;

public class ScreensManager {

    private ScreensManager() {

    }

    private static volatile ScreensManager INSTANCE;

    public static ScreensManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ScreensManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new ScreensManager();
            }
        }
        return INSTANCE;
    }

    public void openStoriesReader(Context outerContext, AppearanceManager manager,
                                  ArrayList<Integer> storiesIds, int index, int source) {
        if (Sizes.isTablet() && outerContext != null && outerContext instanceof AppCompatActivity) {
            DialogFragment settingsDialogFragment = new StoriesDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("index", index);
            bundle.putInt("source", source);
            bundle.putIntegerArrayList("stories_ids", storiesIds);
            if (manager == null) {
                manager = AppearanceManager.getInstance();
            }
            if (manager != null) {
                bundle.putInt(CS_CLOSE_POSITION, manager.csClosePosition());
                bundle.putInt(CS_STORY_READER_ANIMATION, manager.csStoryReaderAnimation());
                bundle.putBoolean(CS_CLOSE_ON_OVERSCROLL, manager.csCloseOnOverscroll());
                bundle.putBoolean(CS_CLOSE_ON_SWIPE, manager.csCloseOnSwipe());
                bundle.putBoolean(CS_HAS_LIKE, manager.csHasLike());
                bundle.putBoolean(CS_HAS_FAVORITE, manager.csHasFavorite());
                bundle.putBoolean(CS_HAS_SHARE, manager.csHasShare());
                bundle.putInt(CS_CLOSE_ICON, manager.csCloseIcon());
                bundle.putInt(CS_REFRESH_ICON, manager.csRefreshIcon());
                bundle.putInt(CS_SOUND_ICON, manager.csSoundIcon());
                bundle.putInt(CS_FAVORITE_ICON, manager.csFavoriteIcon());
                bundle.putInt(CS_LIKE_ICON, manager.csLikeIcon());
                bundle.putInt(CS_DISLIKE_ICON, manager.csDislikeIcon());
                bundle.putInt(CS_SHARE_ICON, manager.csShareIcon());
            }
            settingsDialogFragment.setArguments(bundle);
            settingsDialogFragment.show(
                    ((AppCompatActivity) outerContext).getSupportFragmentManager(),
                    "DialogFragment");
        } else {
            Intent intent2 = new Intent(InAppStoryManager.getInstance().getContext(),
                    (AppearanceManager.getInstance() == null || AppearanceManager.getInstance().csIsDraggable()) ?
                            StoriesActivity.class : StoriesFixedActivity.class);
            intent2.putExtra("index", index);
            intent2.putExtra("source", source);
            intent2.putIntegerArrayListExtra("stories_ids", storiesIds);
            if (manager != null) {
                intent2.putExtra(CS_CLOSE_POSITION, manager.csClosePosition());
                intent2.putExtra(CS_STORY_READER_ANIMATION, manager.csStoryReaderAnimation());
                intent2.putExtra(CS_CLOSE_ON_OVERSCROLL, manager.csCloseOnOverscroll());
                intent2.putExtra(CS_CLOSE_ON_SWIPE, manager.csCloseOnSwipe());
                intent2.putExtra(CS_HAS_LIKE, manager.csHasLike());
                intent2.putExtra(CS_HAS_FAVORITE, manager.csHasFavorite());
                intent2.putExtra(CS_HAS_SHARE, manager.csHasShare());
                intent2.putExtra(CS_CLOSE_ICON, manager.csCloseIcon());
                intent2.putExtra(CS_REFRESH_ICON, manager.csRefreshIcon());
                intent2.putExtra(CS_SOUND_ICON, manager.csSoundIcon());
                intent2.putExtra(CS_FAVORITE_ICON, manager.csFavoriteIcon());
                intent2.putExtra(CS_LIKE_ICON, manager.csLikeIcon());
                intent2.putExtra(CS_DISLIKE_ICON, manager.csDislikeIcon());
                intent2.putExtra(CS_SHARE_ICON, manager.csShareIcon());
            }
            if (outerContext == null) {
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                InAppStoryManager.getInstance().getContext().startActivity(intent2);
            } else {
                outerContext.startActivity(intent2);
            }
        }
    }
}
