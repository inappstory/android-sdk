package com.inappstory.sdk;

import android.graphics.Color;
import android.graphics.Typeface;

import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.stories.ui.views.ILoaderView;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Sizes;

public class AppearanceManager {

    public static final String CS_CLOSE_POSITION = "closePosition";
    public static final String CS_STORY_READER_ANIMATION = "storyReaderAnimation";
    public static final String CS_HAS_LIKE = "hasLike";
    public static final String CS_HAS_FAVORITE = "hasFavorite";
    public static final String CS_HAS_SOUND = "hasSound";
    public static final String CS_HAS_SHARE = "hasShare";
    public static final String CS_CLOSE_ON_SWIPE = "closeOnSwipe";
    public static final String CS_READER_OPEN_ANIM = "readerOpenAnimation";


    public static final int TOP_LEFT = 1;
    public static final int TOP_RIGHT = 2;
    public static final int BOTTOM_LEFT = 3;
    public static final int BOTTOM_RIGHT = 4;

    public static final int ANIMATION_CUBE = 2;
    public static final int ANIMATION_DEPTH = 1;

    private boolean csListItemTitleVisibility = true;
    private int csListItemTitleSize = -1;
    private int csListItemTitleColor = Color.WHITE;

    private boolean csListItemSourceVisibility = false;
    private int csListItemSourceSize = -1;
    private int csListItemSourceColor = Color.WHITE;

    private Integer csListItemWidth;
    private Integer csListItemHeight;

    private boolean csListItemBorderVisibility = true;
    private int csListItemBorderColor = Color.BLACK;

    private IGetFavoriteListItem csFavoriteListItemInterface;
    private IStoriesListItem csListItemInterface;
    private ILoaderView csLoaderView;

    private static AppearanceManager mainInstance;

    public static AppearanceManager getInstance() {
        return mainInstance;
    }

    public static void setInstance(AppearanceManager manager) {
        mainInstance = manager;
    }

    public Typeface csCustomFont() {
        return csCustomFont;
    }

    Typeface csCustomFont;

    public AppearanceManager csListItemHeight(Integer csListItemHeight) {
        this.csListItemHeight = csListItemHeight;
        return AppearanceManager.this;
    }

    public AppearanceManager csListItemWidth(Integer csListItemWidth) {
        this.csListItemWidth = csListItemWidth;
        return AppearanceManager.this;
    }

    public AppearanceManager csCustomFont(Typeface csCustomFont) {
        this.csCustomFont = csCustomFont;
        return AppearanceManager.this;
    }

    private boolean csListOpenedItemBorderVisibility = false;
    private int csListOpenedItemBorderColor = Color.GRAY;

    public AppearanceManager csListItemTitleVisibility(boolean csListItemTitleVisibility) {
        this.csListItemTitleVisibility = csListItemTitleVisibility;
        return AppearanceManager.this;
    }

    public AppearanceManager csListItemTitleSize(int csListItemTitleSize) {
        this.csListItemTitleSize = csListItemTitleSize;
        return AppearanceManager.this;
    }



    public AppearanceManager csListItemTitleColor(int csListItemTitleColor) {
        this.csListItemTitleColor = csListItemTitleColor;
        return AppearanceManager.this;
    }

    public AppearanceManager csListItemSourceVisibility(boolean csListItemSourceVisibility) {
        this.csListItemSourceVisibility = csListItemSourceVisibility;
        return AppearanceManager.this;
    }

    public AppearanceManager csListItemSourceSize(int csListItemSourceSize) {
        this.csListItemSourceSize = csListItemSourceSize;
        return AppearanceManager.this;
    }

    public AppearanceManager csListItemSourceColor(int csListItemSourceColor) {
        this.csListItemSourceColor = csListItemSourceColor;
        return AppearanceManager.this;
    }

    public AppearanceManager csListItemBorderVisibility(boolean csListItemBorderVisibility) {
        this.csListItemBorderVisibility = csListItemBorderVisibility;
        return AppearanceManager.this;
    }

   /* public AppearanceManager csListItemBorderSize(int csListItemBorderSize) {
        this.csListItemBorderSize = csListItemBorderSize;
        return AppearanceManager.this;
    }*/

    public AppearanceManager csListItemBorderColor(int csListItemBorderColor) {
        this.csListItemBorderColor = csListItemBorderColor;
        return AppearanceManager.this;
    }

    public AppearanceManager csListOpenedItemBorderVisibility(boolean csListOpenedItemBorderVisibility) {
        this.csListOpenedItemBorderVisibility = csListOpenedItemBorderVisibility;
        return AppearanceManager.this;
    }


    public AppearanceManager csListOpenedItemBorderColor(int csListOpenedItemBorderColor) {
        this.csListOpenedItemBorderColor = csListOpenedItemBorderColor;
        return AppearanceManager.this;
    }

    public AppearanceManager csListItemMargin(int csListItemMargin) {
        this.csListItemMargin = csListItemMargin;
        return AppearanceManager.this;
    }

    public AppearanceManager csShowStatusBar(boolean csShowStatusBar) {
        this.csShowStatusBar = csShowStatusBar;
        return AppearanceManager.this;
    }

    public AppearanceManager csClosePosition(int csClosePosition) {
        this.csClosePosition = csClosePosition;
        return AppearanceManager.this;
    }

    public AppearanceManager csStoryReaderAnimation(int csStoryReaderAnimation) {
        this.csStoryReaderAnimation = csStoryReaderAnimation;
        return AppearanceManager.this;
    }


    public boolean csListItemTitleVisibility() {
        return csListItemTitleVisibility;
    }

    public int csListItemTitleSize() {
        if (csListItemTitleSize == -1) return Sizes.dpToPxExt(14);
        return csListItemTitleSize;
    }

    public int csListItemTitleColor() {
        return csListItemTitleColor;
    }

    public Integer csListItemHeight() {
        return csListItemHeight;
    }

    public Integer csListItemWidth() {
        return csListItemWidth;
    }

    public boolean csListItemSourceVisibility() {
        return csListItemSourceVisibility;
    }

    public int csListItemSourceSize() {
        if (csListItemSourceSize == -1) return Sizes.dpToPxExt(14);
        return csListItemSourceSize;
    }

    public int csListItemSourceColor() {
        return csListItemSourceColor;
    }

    public boolean csListItemBorderVisibility() {
        return csListItemBorderVisibility;
    }

    /*public int csListItemBorderSize() {
        return csListItemBorderSize;
    }*/

    public int csListItemBorderColor() {
        return csListItemBorderColor;
    }

    public boolean csListOpenedItemBorderVisibility() {
        return csListOpenedItemBorderVisibility;
    }

    public int csListOpenedItemBorderColor() {
        return csListOpenedItemBorderColor;
    }

    public int csListItemMargin() {
        return csListItemMargin;
    }

    public boolean csShowStatusBar() {
        return csShowStatusBar;
    }

    public int csClosePosition() {
        return csClosePosition;
    }

    public int csStoryReaderAnimation() {
        return csStoryReaderAnimation;
    }

    private int csListItemMargin = Sizes.dpToPxExt(4);
    private boolean csShowStatusBar = false;
    private int csClosePosition = TOP_RIGHT; //1 - topLeft, 2 - topRight, 3 - bottomLeft, 4 - bottomRight;
    private int csStoryReaderAnimation = ANIMATION_CUBE;
    private boolean csIsDraggable = true;

    public boolean csIsDraggable() {
        return csIsDraggable;
    }

    public void csIsDraggable(boolean csIsDraggable) {
        this.csIsDraggable = csIsDraggable;
    }


    public IGetFavoriteListItem csFavoriteListItemInterface() {
        return csFavoriteListItemInterface;
    }

    public AppearanceManager csFavoriteListItemInterface(IGetFavoriteListItem csFavoriteListItemInterface) {
        this.csFavoriteListItemInterface = csFavoriteListItemInterface;
        return AppearanceManager.this;
    }



    public AppearanceManager csLoaderView(ILoaderView csLoaderView) {
        this.csLoaderView = csLoaderView;
        return AppearanceManager.this;
    }

    public ILoaderView csLoaderView() {
        return csLoaderView;
    }

    public IStoriesListItem csListItemInterface() {
        return csListItemInterface;
    }

    public AppearanceManager csListItemInterface(IStoriesListItem csListItemInterface) {
        this.csListItemInterface = csListItemInterface;
        return AppearanceManager.this;
    }














}
