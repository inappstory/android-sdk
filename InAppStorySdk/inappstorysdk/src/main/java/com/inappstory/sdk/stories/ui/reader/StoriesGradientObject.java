package com.inappstory.sdk.stories.ui.reader;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StoriesGradientObject implements Serializable {
    public StoriesGradientObject csGradientHeight(Integer csGradientHeight) {
        this.csGradientHeight = csGradientHeight;
        return this;
    }

    public StoriesGradientObject csColors(List<Integer> csColors) {
        this.csColors = csColors;
        return this;
    }

    public StoriesGradientObject csLocations(List<Float> csLocations) {
        this.csLocations = csLocations;
        return this;
    }


    public Integer csGradientHeight = 100;
    public List<Integer> csColors = new ArrayList<>();
    public List<Float> csLocations = new ArrayList<>();

    public StoriesGradientObject() {
        csColors.add(Color.parseColor("#00000000"));
        csColors.add(Color.parseColor("#50000000"));
        csLocations.add(0f);
        csLocations.add(1f);
    }
}