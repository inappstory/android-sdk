package com.inappstory.sdk.stories.api.models.slidestructure;

import com.inappstory.sdk.core.network.annotations.models.SerializedName;

import java.util.ArrayList;

public class Element {
    public String id;
    public Geometry geometry;
    public int inTemplateIndex;
    public boolean newPadding;
    public boolean underline;
    public boolean bold;
    public boolean italic;
    public boolean secondaryFont;
    @SerializedName("strikethrough")
    public boolean strikeThrough;
    public boolean stroke;
    public boolean animated = true;
    public Background background;
    public Border border;
    public String content;
    public String align;
    public String color;
    public String blurred;
    public Thumbnail thumbnail;
    public String type;
    public String path;
    public String linkType;
    public String linkTarget;
    public float textSize;
    public float lineHeight;
    public float padding;
    public float width;
    public float height;
    public float size;
    public Float opacity;
    public Animation animation;
    public ArrayList<Source> sources;
}
