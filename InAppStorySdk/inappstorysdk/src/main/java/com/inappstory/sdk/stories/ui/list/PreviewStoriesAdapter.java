package com.inappstory.sdk.stories.ui.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.api.models.Story;

import java.util.ArrayList;
import java.util.List;

public class PreviewStoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<Story> stories = new ArrayList<>();

    AppearanceManager manager;
    public Context context;

    public PreviewStoriesAdapter(Context context, AppearanceManager manager) {
        this.context = context;
        this.manager = manager;
        Story story1 = new Story();
        Story story2 = new Story();
        Story story3 = new Story();
        story1.id = 1;
        story2.id = 2;
        story3.id = 3;
        story1.title = "story1";
        story2.title = "story1";
        story3.title = "story1";
        story1.backgroundColor = "#FF0000";
        story2.title = "#00FF00";
        story3.title = "#0000FF";
        stories.add(story1);
        stories.add(story2);
        stories.add(story3);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cs_story_list_custom_item, parent, false);
        return new StoryListItem(v, manager, false, false, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    /*    Story story = stories.get(position);
        holder.bindPreview(story.id,
                story.getTitle(),
                Color.parseColor("#FFFF00"),
                Color.parseColor(story.getBackgroundColor()),
                manager);*/
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
