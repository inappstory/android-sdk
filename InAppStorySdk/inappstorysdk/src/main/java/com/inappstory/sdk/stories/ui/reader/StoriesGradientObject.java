package com.inappstory.sdk.stories.ui.reader;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class StoriesGradientObject {
    public void csGradientHeight(Integer csGradientHeight) {
        this.csGradientHeight = csGradientHeight;
    }

    public void csColors(List<Integer> csColors) {
        this.csColors = csColors;
    }

    public void csLocations(List<Float> csLocations) {
        this.csLocations = csLocations;
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