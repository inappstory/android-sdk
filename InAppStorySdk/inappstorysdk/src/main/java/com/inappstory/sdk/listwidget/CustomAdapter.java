package com.inappstory.sdk.listwidget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inappstory.sdk.R;

public class CustomAdapter extends BaseAdapter {
    Context context;
    String texts1[];
    String texts2[];
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, String[] texts1, String[] texts2) {
        this.context = applicationContext;
        this.texts1 = texts1;
        this.texts2 = texts2;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return texts1.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        return view;
    }
}