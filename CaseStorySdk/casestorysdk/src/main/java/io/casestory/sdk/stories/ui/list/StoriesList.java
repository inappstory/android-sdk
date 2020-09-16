package io.casestory.sdk.stories.ui.list;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.casestory.sdk.AppearanceManager;
import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.exceptions.DataException;
import io.casestory.sdk.stories.api.models.callbacks.LoadStoriesCallback;

public class StoriesList extends RecyclerView {
    public StoriesList(@NonNull Context context) {
        super(context);
    }

    public StoriesList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StoriesList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {

    }

    StoriesAdapter adapter;
    LayoutManager layoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);

    public void setAppearanceManager(AppearanceManager appearanceManager) {
        this.appearanceManager = appearanceManager;
    }

    AppearanceManager appearanceManager;

    public void loadStories() throws DataException {
        if (appearanceManager == null) {
            throw new DataException("Need to set an AppearanceManager", new Throwable("StoriesList data is not valid"));
        }
        if (CaseStoryManager.getInstance().getUserId() == null) {
            throw new DataException("'userId' can't be null", new Throwable("CaseStoryManager data is not valid"));
        }

        if (CaseStoryService.getInstance() != null) {
            CaseStoryService.getInstance().loadStories(new LoadStoriesCallback() {
                @Override
                public void storiesLoaded(List<Integer> storiesIds) {
                    adapter = new StoriesAdapter(storiesIds, appearanceManager, false);
                    setLayoutManager(layoutManager);
                    setAdapter(adapter);
                }
            });

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CaseStoryService.getInstance().loadStories(new LoadStoriesCallback() {
                        @Override
                        public void storiesLoaded(List<Integer> storiesIds) {
                            adapter = new StoriesAdapter(storiesIds, appearanceManager, false);
                            setLayoutManager(layoutManager);
                            setAdapter(adapter);
                        }
                    });
                }
            }, 1000);
        }

    }

}
