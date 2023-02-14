package com.inappstory.sdk.stories.ui.list;


import java.io.Serializable;

public class UGCListItemAppearance implements Serializable {
    public UGCListItemAppearance() {

    }

    public UGCListItemAppearance csBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public UGCListItemAppearance csIconColor(int iconColor) {
        this.iconColor = iconColor;
        return this;
    }

    public UGCListItemAppearance csIconId(int iconId) {
        this.iconId = iconId;
        return this;
    }

    public UGCListItemAppearance csIconMargin(int iconMargin) {
        this.iconMargin = iconMargin;
        return this;
    }

    public Integer csIconMargin() {
        return iconMargin;
    }

    public Integer csIconId() {
        return iconId;
    }

    public Integer csIconColor() {
        return iconColor;
    }

    public Integer csBackgroundColor() {
        return backgroundColor;
    }

    Integer iconColor;
    Integer backgroundColor;
    Integer iconMargin;
    Integer iconId;
}
