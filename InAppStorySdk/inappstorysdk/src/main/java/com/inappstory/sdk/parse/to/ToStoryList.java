package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.InAppMessageLimit;
import com.inappstory.sdk.core.network.content.models.Story;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public final class ToStoryList extends ToModelParser<List<Story>> {
    public ToStoryList(String value) {
        super(value);
    }

    public ToStoryList(JSONArray value) {
        super(value);
    }

    @Override
    protected List<Story> parseList(JSONArray stories) {
        try {
            List<Story> storyList = new ArrayList<>();
            for (int i = 0; i < stories.length(); i++) {
                storyList.add(new ToStory(stories.getJSONObject(i)).parse());
            }
            return storyList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected List<Story> parseString(@NonNull String value) {
        try {
            JSONArray stories = new JSONArray(value);
            List<Story> storyList = new ArrayList<>();
            for (int i = 0; i < stories.length(); i++) {
                storyList.add(new ToStory(stories.getJSONObject(i)).parse());
            }
            return storyList;
        } catch (Exception e) {
            return null;
        }
    }
}
