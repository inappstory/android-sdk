package com.inappstory.sdk;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.NonNull;

import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.ui.list.StoriesList;
import com.inappstory.sdk.stories.ui.list.StoryTouchListener;
import com.inappstory.sdk.stories.ui.views.IGameLoaderView;
import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.stories.ui.views.ILoaderView;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Sizes;

/**
 * Defines appearance of the stories list, as well as some elements of the reader.
 * It must be set globally for the library, or separately for the list before calling {@link StoriesList#loadStories()}.
 * For a global setting, you must call the static method of the class {@link #setInstance(AppearanceManager)}.
 */
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
    private IGameLoaderView csGameLoaderView;
    private StoryTouchListener storyTouchListener;
    private static WidgetAppearance csWidgetAppearance;


    private static AppearanceManager mainInstance;

    public static AppearanceManager getInstance() {
        return mainInstance;
    }

    /**
     * use to set global {@link AppearanceManager}
     * @param manager (manager) {@link AppearanceManager} instance
     */
    public static void setInstance(AppearanceManager manager) {
        mainInstance = manager;
    }


    public Typeface csCustomFont() {
        return csCustomFont;
    }

    Typeface csCustomFont;
    Typeface csCustomBoldFont;
    Typeface csCustomItalicFont;

    public Typeface csCustomBoldFont() {
        return csCustomBoldFont;
    }

    public Typeface csCustomItalicFont() {
        return csCustomItalicFont;
    }

    public Typeface csCustomBoldItalicFont() {
        return csCustomBoldItalicFont;
    }

    public Typeface csCustomSecondaryBoldFont() {
        return csCustomSecondaryBoldFont;
    }

    public Typeface csCustomSecondaryItalicFont() {
        return csCustomSecondaryItalicFont;
    }

    public Typeface csCustomSecondaryBoldItalicFont() {
        return csCustomSecondaryBoldItalicFont;
    }

    Typeface csCustomBoldItalicFont;

    public Typeface csCustomSecondaryFont() {
        return csCustomSecondaryFont;
    }

    Typeface csCustomSecondaryFont;
    Typeface csCustomSecondaryBoldFont;
    Typeface csCustomSecondaryItalicFont;
    Typeface csCustomSecondaryBoldItalicFont;

    public static WidgetAppearance csWidgetAppearance() {
        if (csWidgetAppearance == null) csWidgetAppearance = new WidgetAppearance();
        return csWidgetAppearance;
    }

    public static void csWidgetAppearance(@NonNull Context context,
                                          @NonNull Class widgetClass,
                                          Integer corners) {
        csWidgetAppearance();
        csWidgetAppearance.widgetClass = widgetClass;
        csWidgetAppearance.corners = corners;
        csWidgetAppearance.context = context;
        csWidgetAppearance.sandbox = false;
        csWidgetAppearance.save();
    }

    /**
     * use to set custom list item height in default cells
     * @param csListItemHeight (csListItemHeight)
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csListItemHeight(Integer csListItemHeight) {
        this.csListItemHeight = csListItemHeight;
        return AppearanceManager.this;
    }


    /**
     * use to set custom touch effect on list item
     * @param storyTouchListener (storyTouchListener) {@link StoryTouchListener}
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csStoryTouchListener(StoryTouchListener storyTouchListener) {
        this.storyTouchListener = storyTouchListener;
        return AppearanceManager.this;
    }

    public StoryTouchListener csStoryTouchListener() {
        return this.storyTouchListener;
    }

    /**
     * use to set custom list item width in default cells
     * @param csListItemWidth (csListItemWidth)
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csListItemWidth(Integer csListItemWidth) {
        this.csListItemWidth = csListItemWidth;
        return AppearanceManager.this;
    }

    /**
     * use to set custom font for list items in default cells
     * @param csCustomFont (csCustomFont) {@link Typeface} font
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csCustomFont(Typeface csCustomFont) {
        this.csCustomFont = csCustomFont;
        return AppearanceManager.this;
    }

    public AppearanceManager csCustomBoldFont(Typeface csCustomFont) {
        this.csCustomBoldFont = csCustomFont;
        return AppearanceManager.this;
    }

    public Typeface getFont(boolean secondary, boolean bold, boolean italic) {
        if (secondary) {
            if (bold) {
                if (italic) {
                    if (csCustomSecondaryBoldItalicFont == null) return getFont(secondary, bold, false);
                    return csCustomSecondaryBoldItalicFont;
                } else {
                    if (csCustomSecondaryBoldFont == null) return getFont(secondary, false, italic);
                    return csCustomSecondaryBoldFont;
                }
            } else {
                if (italic) {
                    if (csCustomSecondaryItalicFont == null) return getFont(secondary, bold, false);
                    return csCustomSecondaryItalicFont;
                } else {
                    if (csCustomSecondaryFont == null) return getFont(false, bold, italic);
                    return csCustomSecondaryFont;
                }
            }
        } else {
            if (bold) {
                if (italic) {
                    if (csCustomBoldItalicFont == null) return getFont(secondary, bold, false);
                    return csCustomBoldItalicFont;
                } else {
                    if (csCustomBoldFont == null) return getFont(secondary, false, italic);
                    return csCustomBoldFont;
                }
            } else {
                if (italic) {
                    if (csCustomItalicFont == null) return getFont(secondary, bold, false);
                    return csCustomItalicFont;
                } else {
                    if (csCustomFont == null) return null;
                    return csCustomFont;
                }
            }
        }
    }

    public AppearanceManager csCustomItalicFont(Typeface csCustomFont) {
        this.csCustomItalicFont = csCustomFont;
        return AppearanceManager.this;
    }


    public AppearanceManager csCustomBoldItalicFont(Typeface csCustomFont) {
        this.csCustomBoldItalicFont = csCustomFont;
        return AppearanceManager.this;
    }

    public AppearanceManager csCustomSecondaryFont(Typeface csCustomFont) {
        this.csCustomSecondaryFont = csCustomFont;
        return AppearanceManager.this;
    }

    public AppearanceManager csCustomSecondaryBoldFont(Typeface csCustomFont) {
        this.csCustomSecondaryBoldFont = csCustomFont;
        return AppearanceManager.this;
    }

    public AppearanceManager csCustomSecondaryItalicFont(Typeface csCustomFont) {
        this.csCustomSecondaryItalicFont = csCustomFont;
        return AppearanceManager.this;
    }

    public AppearanceManager csCustomSecondaryBoldItalicFont(Typeface csCustomFont) {
        this.csCustomSecondaryBoldItalicFont = csCustomFont;
        return AppearanceManager.this;
    }

    private boolean csListOpenedItemBorderVisibility = false;
    private int csListOpenedItemBorderColor = Color.GRAY;


    public AppearanceManager csListItemTitleVisibility(boolean csListItemTitleVisibility) {
        this.csListItemTitleVisibility = csListItemTitleVisibility;
        return AppearanceManager.this;
    }

    /**
     * use to set font size for list items in default cells
     * @param csListItemTitleSize (csListItemTitleSize)
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csListItemTitleSize(int csListItemTitleSize) {
        this.csListItemTitleSize = csListItemTitleSize;
        return AppearanceManager.this;
    }

    /**
     * use to set font color for list items in default cells
     * @param csListItemTitleColor (csListItemTitleColor)
     * @return {@link AppearanceManager}
     */
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

    /**
     * use to set border color for list items in default cells
     * @param csListItemBorderColor (csListItemBorderColor)
     * @return {@link AppearanceManager}
     */
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

    public AppearanceManager csGameLoaderView(IGameLoaderView csGameLoaderView) {
        this.csGameLoaderView = csGameLoaderView;
        return AppearanceManager.this;
    }

    public IGameLoaderView csGameLoaderView() {
        return csGameLoaderView;
    }

    public IStoriesListItem csListItemInterface() {
        return csListItemInterface;
    }

    public AppearanceManager csListItemInterface(IStoriesListItem csListItemInterface) {
        this.csListItemInterface = csListItemInterface;
        return AppearanceManager.this;
    }


}
