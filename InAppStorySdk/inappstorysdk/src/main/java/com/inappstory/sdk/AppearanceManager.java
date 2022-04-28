package com.inappstory.sdk;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.ui.list.StoriesList;
import com.inappstory.sdk.stories.ui.list.StoryTouchListener;
import com.inappstory.sdk.stories.ui.reader.StoriesGradientObject;
import com.inappstory.sdk.stories.ui.views.goodswidget.ICustomGoodsItem;
import com.inappstory.sdk.stories.ui.views.goodswidget.ICustomGoodsWidget;
import com.inappstory.sdk.stories.ui.views.IGameLoaderView;
import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.stories.ui.views.ILoaderView;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.ugc.list.IStoriesListUGCItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines appearance of the stories list, as well as some elements of the reader.
 * It must be set globally for the library, or separately for the list before calling {@link StoriesList#loadStoriesInner()}.
 * For a global setting, you must call the static method of the class {@link #setCommonInstance(AppearanceManager)}.
 */
public class AppearanceManager {

    public static final String CS_CLOSE_POSITION = "closePosition";
    public static final String CS_NAVBAR_COLOR = "navBarColor";
    public static final String CS_STORY_READER_ANIMATION = "storyReaderAnimation";

    public static final String CS_TIMER_GRADIENT_ENABLE = "timerGradientEnable";
    public static final String CS_TIMER_GRADIENT = "timerGradient";

    public static final String CS_HAS_LIKE = "hasLike";
    public static final String CS_HAS_FAVORITE = "hasFavorite";
    public static final String CS_HAS_SHARE = "hasShare";
    public static final String CS_CLOSE_ON_SWIPE = "closeOnSwipe";

    public static final String CS_CLOSE_ON_OVERSCROLL = "closeOnOverscroll";
    public static final String CS_READER_OPEN_ANIM = "readerOpenAnimation";
    public static final String CS_FAVORITE_ICON = "iconFavorite";
    public static final String CS_LIKE_ICON = "iconLike";
    public static final String CS_DISLIKE_ICON = "iconDislike";
    public static final String CS_SHARE_ICON = "iconShare";
    public static final String CS_CLOSE_ICON = "iconClose";
    public static final String CS_SOUND_ICON = "iconSound";
    public static final String CS_REFRESH_ICON = "iconRefresh";
    public static final String CS_READER_SETTINGS = "readerSettings";


    public static final String CS_COVER_QUALITY = "coverQuality";


    public static final int TOP_LEFT = 1;
    public static final int TOP_RIGHT = 2;
    public static final int BOTTOM_LEFT = 3;
    public static final int BOTTOM_RIGHT = 4;

    public static final int ANIMATION_CUBE = 2;
    public static final int ANIMATION_DEPTH = 1;

    private boolean csListItemTitleVisibility = true;
    private int csListItemTitleSize = -1;
    private int csListItemTitleColor = Color.WHITE;


    private int csListItemRadius = -1;

    private boolean csListItemSourceVisibility = false;
    private int csListItemSourceSize = -1;
    private int csListItemSourceColor = Color.WHITE;

    private Integer csListItemWidth;
    private Integer csListItemHeight;

    private boolean csListItemBorderVisibility = true;
    private int csListItemBorderColor = Color.BLACK;

    private IGetFavoriteListItem csFavoriteListItemInterface;
    private IStoriesListItem csListItemInterface;
    private IStoriesListUGCItem csListUGCItemInterface;
    private ILoaderView csLoaderView;
    private IGameLoaderView csGameLoaderView;
    private StoryTouchListener storyTouchListener;
    private static WidgetAppearance csWidgetAppearance;

    private ICustomGoodsWidget csCustomGoodsWidget;
    private ICustomGoodsItem csCustomGoodsItem;

    private boolean csHasLike;
    private boolean csHasFavorite;
    private boolean csHasUGC;
    private boolean csHasShare;
    private boolean csTimerGradientEnable = true;

    private int csFavoriteIcon;
    private int csLikeIcon;
    private int csDislikeIcon;
    private int csShareIcon;
    private int csCloseIcon;
    private int csRefreshIcon;
    private int csSoundIcon;
    private int csNavBarColor = Color.TRANSPARENT;
    private int csNightNavBarColor = Color.TRANSPARENT;

    private Typeface csCustomFont;
    private Typeface csCustomBoldFont;
    private Typeface csCustomItalicFont;
    private Typeface csCustomBoldItalicFont;
    private Typeface csCustomSecondaryFont;
    private Typeface csCustomSecondaryBoldFont;
    private Typeface csCustomSecondaryItalicFont;
    private Typeface csCustomSecondaryBoldItalicFont;

    private StoriesGradientObject csTimerGradient;



    public AppearanceManager csTimerGradient(StoriesGradientObject csTimerGradient) {
        this.csTimerGradient = csTimerGradient;
        return AppearanceManager.this;
    }

    public StoriesGradientObject csTimerGradient() {
        return csTimerGradient;
    }

    private int csCoverQuality;

    /**
     * use to set quality for story covers
     *
     * @param csCoverQuality (csCoverQuality) quality for covers
     *                       {@link com.inappstory.sdk.stories.api.models.Image#QUALITY_MEDIUM}
     *                       {@link com.inappstory.sdk.stories.api.models.Image#QUALITY_HIGH}
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csCoverQuality(int csCoverQuality) {
        this.csCoverQuality = csCoverQuality;
        return AppearanceManager.this;
    }

    public int csCoverQuality() {
        return csCoverQuality;
    }

    /**
     * use to set navigation bar color in stories reader
     *
     * @param csNavBarColor (csNavBarColor) color for nav bar
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csNavBarColor(int csNavBarColor) {
        this.csNavBarColor = csNavBarColor;
        return AppearanceManager.this;
    }

    public int csNavBarColor() {
        return csNavBarColor;
    }

    /**
     * use to set night-mode navigation bar color in stories reader
     *
     * @param csNightNavBarColor (csNightNavBarColor) color for nav bar
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csNightNavBarColor(int csNightNavBarColor) {
        this.csNightNavBarColor = csNightNavBarColor;
        return AppearanceManager.this;
    }

    public int csNightNavBarColor() {
        return csNightNavBarColor != 0 ? csNightNavBarColor : csNavBarColor;
    }


    public boolean csCloseOnSwipe() {
        return csCloseOnSwipe;
    }

    public boolean csCloseOnOverscroll() {
        return csCloseOnOverscroll;
    }

    /**
     * use to set if stories reader can be closed by swipe down
     *
     * @param closeOnSwipe (closeOnSwipe) true - if reader has to be closed by swipe down
     *                     true by default
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csCloseOnSwipe(boolean closeOnSwipe) {
        this.csCloseOnSwipe = csCloseOnSwipe;
        return AppearanceManager.this;
    }


    /**
     * use to set if stories reader can be closed by swipe right
     * on first slide of first story or last slide of last story
     *
     * @param closeOnOverscroll (closeOnOverscroll) true - if reader has to be closed by swipe
     *                          true by default
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csCloseOnOverscroll(boolean closeOnOverscroll) {
        this.csCloseOnOverscroll = closeOnOverscroll;
        return AppearanceManager.this;
    }

    private boolean csCloseOnSwipe = true;
    private boolean csCloseOnOverscroll = true;

    private static AppearanceManager commonInstance;

    public static AppearanceManager getCommonInstance() {
        if (commonInstance == null) {
            synchronized (lock) {
                if (commonInstance == null)
                    commonInstance = new AppearanceManager();
            }
        }
        return commonInstance;
    }

    private static Object lock = new Object();

    /**
     * use to set common {@link AppearanceManager}
     *
     * @param manager (manager) {@link AppearanceManager} instance
     */
    public static void setCommonInstance(AppearanceManager manager) {
        synchronized (lock) {
            commonInstance = manager;
        }
    }

    /**
     * use to set common {@link AppearanceManager}
     *
     * @param manager (manager) {@link AppearanceManager} instance
     * @deprecated will be removed in SDK 2.0
     * Switch to {@link AppearanceManager#setCommonInstance(AppearanceManager)})
     */
    @Deprecated
    public static void setInstance(AppearanceManager manager) {
        synchronized (lock) {
            commonInstance = manager;
        }
    }


    public boolean csTimerGradientEnable() {
        return csTimerGradientEnable;
    }

    public Typeface csCustomFont() {
        return csCustomFont;
    }



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


    public Typeface csCustomSecondaryFont() {
        return csCustomSecondaryFont;
    }

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
     *
     * @param csListItemHeight (csListItemHeight)
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csListItemHeight(Integer csListItemHeight) {
        this.csListItemHeight = csListItemHeight;
        return AppearanceManager.this;
    }


    /**
     * use to set custom touch effect on list item
     *
     * @param storyTouchListener (storyTouchListener) {@link StoryTouchListener}
     * @return {@link AppearanceManager}
     * @deprecated will be removed in 2.0
     * Use {@link StoriesList#setStoryTouchListener(StoryTouchListener)} instead
     */
    @Deprecated
    public AppearanceManager csStoryTouchListener(StoryTouchListener storyTouchListener) {
        this.storyTouchListener = storyTouchListener;
        return AppearanceManager.this;
    }

    public StoryTouchListener csStoryTouchListener() {
        return this.storyTouchListener;
    }

    /**
     * use to set custom list item width in default cells
     *
     * @param csListItemWidth (csListItemWidth)
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csListItemWidth(Integer csListItemWidth) {
        this.csListItemWidth = csListItemWidth;
        return AppearanceManager.this;
    }

    /**
     * use to set custom font for list items in default cells
     *
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

    public boolean csHasLike() {
        return csHasLike;
    }

    public boolean csHasFavorite() {
        return csHasFavorite;

    }

    public boolean csHasUGC() {
        return csHasUGC;
    }

    public boolean csHasShare() {
        return csHasShare;
    }

    public int csFavoriteIcon() {
        return csFavoriteIcon != 0 ? csFavoriteIcon : R.drawable.ic_stories_status_favorite;
    }

    public int csLikeIcon() {
        return csLikeIcon != 0 ? csLikeIcon : R.drawable.ic_stories_status_like;
    }

    public int csDislikeIcon() {
        return csDislikeIcon != 0 ? csDislikeIcon : R.drawable.ic_stories_status_dislike;
    }

    public int csShareIcon() {
        return csShareIcon != 0 ? csShareIcon : R.drawable.ic_share_status;
    }

    public int csCloseIcon() {
        return csCloseIcon != 0 ? csCloseIcon : R.drawable.ic_stories_close;
    }

    public int csRefreshIcon() {
        return csRefreshIcon != 0 ? csRefreshIcon : R.drawable.ic_refresh;
    }

    public int csSoundIcon() {
        return csSoundIcon != 0 ? csSoundIcon : R.drawable.ic_stories_status_sound;
    }

    /**
     * use to turn on/off like/dislike features (available in stories reader)
     *
     * @param hasLike (hasLike) true - to use this feature
     *                false by default
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csHasLike(boolean hasLike) {
        this.csHasLike = hasLike;
        return AppearanceManager.this;
    }

    /**
     * use to turn on/off gradient under timer (available in stories reader)
     *
     * @param gradientEnable (gradientEnable) true - to use this feature
     *                       false by default
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csTimerGradientEnable(boolean gradientEnable) {
        this.csTimerGradientEnable = gradientEnable;
        return AppearanceManager.this;
    }


    /**
     * use to turn on/off favorite feature (favorite cell in list and add/remove)
     *
     * @param hasFavorite (hasFavorite) true - to use this feature
     *                    false by default
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csHasFavorite(boolean hasFavorite) {
        this.csHasFavorite = hasFavorite;
        return AppearanceManager.this;
    }

    public AppearanceManager csHasUGC(boolean hasUGC) {
        this.csHasUGC = hasUGC;
        return AppearanceManager.this;
    }


    /**
     * use to turn on/off share feature (available in stories reader)
     *
     * @param hasShare (hasShare) true - to use this feature
     *                 false by default
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csHasShare(boolean hasShare) {
        this.csHasShare = hasShare;
        return AppearanceManager.this;
    }

    /**
     * use to change default favorite icon (available in stories reader)
     *
     * @param favoriteIcon (favoriteIcon) drawable id
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csFavoriteIcon(int favoriteIcon) {
        csFavoriteIcon = favoriteIcon;
        return AppearanceManager.this;
    }

    /**
     * use to change default like icon (available in stories reader)
     *
     * @param likeIcon (likeIcon) drawable id
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csLikeIcon(int likeIcon) {
        csLikeIcon = likeIcon;
        return AppearanceManager.this;
    }

    /**
     * use to change default dislike icon (available in stories reader)
     *
     * @param dislikeIcon (dislikeIcon) drawable id
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csDislikeIcon(int dislikeIcon) {
        csDislikeIcon = dislikeIcon;
        return AppearanceManager.this;
    }

    /**
     * use to change default share icon (available in stories reader)
     *
     * @param shareIcon (shareIcon) drawable id
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csShareIcon(int shareIcon) {
        csShareIcon = shareIcon;
        return AppearanceManager.this;
    }


    /**
     * use to change default close icon (available in stories reader)
     *
     * @param closeIcon (closeIcon) drawable id
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csCloseIcon(int closeIcon) {
        csCloseIcon = closeIcon;
        return AppearanceManager.this;
    }

    /**
     * use to change default refresh icon (available in stories reader)
     *
     * @param refreshIcon (refreshIcon) drawable id
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csRefreshIcon(int refreshIcon) {
        csRefreshIcon = refreshIcon;
        return AppearanceManager.this;
    }

    /**
     * use to change default sound icon (available in stories reader)
     *
     * @param soundIcon (soundIcon) drawable id
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csSoundIcon(int soundIcon) {
        csSoundIcon = soundIcon;
        return AppearanceManager.this;
    }

    public Typeface getFont(boolean secondary, boolean bold, boolean italic) {
        if (secondary) {
            if (bold) {
                if (italic) {
                    if (csCustomSecondaryBoldItalicFont == null)
                        return getFont(secondary, bold, false);
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

    public AppearanceManager csListItemRadius(int csListItemRadius) {
        this.csListItemRadius = csListItemRadius;
        return AppearanceManager.this;
    }

    public int csListItemRadius() {
        if (csListItemRadius == -1) return Sizes.dpToPxExt(16);
        return csListItemRadius;
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


    @Deprecated
    public AppearanceManager csListItemTitleVisibility(boolean csListItemTitleVisibility) {
        this.csListItemTitleVisibility = csListItemTitleVisibility;
        return AppearanceManager.this;
    }

    /**
     * use to set font size for list items in default cells
     *
     * @param csListItemTitleSize (csListItemTitleSize)
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csListItemTitleSize(int csListItemTitleSize) {
        this.csListItemTitleSize = csListItemTitleSize;
        return AppearanceManager.this;
    }

    /**
     * use to set font color for list items in default cells
     *
     * @param csListItemTitleColor (csListItemTitleColor)
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csListItemTitleColor(int csListItemTitleColor) {
        this.csListItemTitleColor = csListItemTitleColor;
        return AppearanceManager.this;
    }

    @Deprecated
    public AppearanceManager csListItemSourceVisibility(boolean csListItemSourceVisibility) {
        this.csListItemSourceVisibility = csListItemSourceVisibility;
        return AppearanceManager.this;
    }

    @Deprecated
    public AppearanceManager csListItemSourceSize(int csListItemSourceSize) {
        this.csListItemSourceSize = csListItemSourceSize;
        return AppearanceManager.this;
    }

    @Deprecated
    public AppearanceManager csListItemSourceColor(int csListItemSourceColor) {
        this.csListItemSourceColor = csListItemSourceColor;
        return AppearanceManager.this;
    }

    @Deprecated
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
     *
     * @param csListItemBorderColor (csListItemBorderColor)
     * @return {@link AppearanceManager}
     */
    public AppearanceManager csListItemBorderColor(int csListItemBorderColor) {
        this.csListItemBorderColor = csListItemBorderColor;
        return AppearanceManager.this;
    }

    @Deprecated
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

    @Deprecated
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

    public AppearanceManager csIsDraggable(boolean csIsDraggable) {
        this.csIsDraggable = csIsDraggable;
        return AppearanceManager.this;
    }


    public IGetFavoriteListItem csFavoriteListItemInterface() {
        return csFavoriteListItemInterface;
    }

    public AppearanceManager csFavoriteListItemInterface(IGetFavoriteListItem csFavoriteListItemInterface) {
        this.csFavoriteListItemInterface = csFavoriteListItemInterface;
        return AppearanceManager.this;
    }


    public ICustomGoodsWidget csCustomGoodsWidget() {
        return csCustomGoodsWidget;
    }



    public AppearanceManager csCustomGoodsWidget(ICustomGoodsWidget csCustomGoodsWidget) {
        this.csCustomGoodsWidget = csCustomGoodsWidget;
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

    public IStoriesListUGCItem csListUGCItemInterface() {
        return csListUGCItemInterface;
    }

    public AppearanceManager csListItemInterface(IStoriesListUGCItem csListUGCItemInterface) {
        this.csListUGCItemInterface = csListUGCItemInterface;
        return AppearanceManager.this;
    }

    public static View getLoader(Context context) {
        View v = null;
        RelativeLayout.LayoutParams relativeParams;
        if (commonInstance != null
                && commonInstance.csLoaderView() != null) {
            v = commonInstance.csLoaderView().getView();
        } else {
            v = new ProgressBar(context) {{
                setIndeterminate(true);
                getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            }};
        }
        relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        v.setLayoutParams(relativeParams);
        return v;
    }

}
