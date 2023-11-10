package com.inappstory.sdk.core;

import com.inappstory.sdk.stories.api.models.Story.StoryType;
import com.inappstory.sdk.stories.uidomain.list.listnotify.IStoriesListNotify;

import java.util.HashSet;
import java.util.Set;

public class ListNotifier {

    private final Object lock = new Object();

    Set<IStoriesListNotify> storiesListNotifySet = new HashSet<>();

    public void changeStory(int storyId, StoryType type, String listID) {
        if (listID == null) return;
        synchronized (lock) {
            for (IStoriesListNotify sub : storiesListNotifySet) {
                if (listID.equals(sub.getListUID()))
                    sub.changeStory(storyId, type);
            }
        }
    }

    public void closeReader(String listID) {
        if (listID == null) return;
        synchronized (lock) {
            for (IStoriesListNotify sub : storiesListNotifySet) {
                if (listID.equals(sub.getListUID()))
                    sub.closeReader();
            }
        }
    }

    public void openReader(String listID) {
        if (listID == null) return;
        synchronized (lock) {
            for (IStoriesListNotify sub : storiesListNotifySet) {
                if (listID.equals(sub.getListUID()))
                    sub.openReader();
            }
        }
    }

    public void addStoriesListNotify(IStoriesListNotify storiesListNotify) {
        synchronized (lock) {
            storiesListNotifySet.add(storiesListNotify);
        }
    }

    public void removeStoriesListNotify(IStoriesListNotify storiesListNotify) {
        synchronized (lock) {
            if (storiesListNotifySet == null) return;
            storiesListNotifySet.remove(storiesListNotify);
        }
    }
}