package com.inappstory.sdk;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.inappstory.sdk.banners.ui.list.DefaultBannerListAppearance;
import com.inappstory.sdk.core.banners.ICustomBannerListAppearance;
import com.inappstory.sdk.core.ui.widgets.customicons.IASDefaultAppearanceIcons;
import com.inappstory.sdk.core.ui.widgets.customicons.IASDefaultIconCreator;
import com.inappstory.sdk.core.ui.widgets.customicons.IIASDefaultIconCreator;
import com.inappstory.sdk.stories.ui.widgets.LoadProgressBar;
import com.inappstory.sdk.banners.ui.place.DefaultBannerPlaceAppearance;
import com.inappstory.sdk.core.banners.ICustomBannerPlaceAppearance;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.stories.ui.list.StoriesList;
import com.inappstory.sdk.stories.ui.list.StoryTouchListener;
import com.inappstory.sdk.stories.ui.list.UGCListItemSimpleAppearance;
import com.inappstory.sdk.stories.ui.reader.StoriesGradientObject;
import com.inappstory.sdk.game.ui.IGameReaderLoaderView;
import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.ui.views.IStoryReaderLoaderView;
import com.inappstory.sdk.stories.ui.views.goodswidget.ICustomGoodsItem;
import com.inappstory.sdk.stories.ui.views.goodswidget.ICustomGoodsWidget;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.ugc.list.IStoriesListUGCItem;
import com.inappstory.sdk.core.ui.widgets.customicons.CustomIconWithStates;
import com.inappstory.sdk.core.ui.widgets.customicons.CustomIconWithoutStates;

/**
 * Defines appearance of the stories list, as well as some elements of the reader.
 * It must be set globally for the library, or separately for the list before calling {@link StoriesList#loadStories()}.
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
    public static final String CS_READER_BACKGROUND_COLOR = "readerBackgroundColor";

    public static final String CS_READER_RADIUS = "readerRadius";
    public static final String CS_CLOSE_ON_OVERSCROLL = "closeOnOverscroll";
    public static final String CS_READER_PRESENTATION_STYLE = "presentationStyle";
    public static final String CS_FAVORITE_ICON = "iconFavorite";
    public static final String CS_LIKE_ICON = "iconLike";
    public static final String CS_DISLIKE_ICON = "iconDislike";
    public static final String CS_SHARE_ICON = "iconShare";
    public static final String CS_CLOSE_ICON = "iconClose";
    public static final String CS_SOUND_ICON = "iconSound";
    public static final String CS_REFRESH_ICON = "iconRefresh";
    public static final String CS_READER_SETTINGS = "readerSettings";


    public static final String CS_COVER_QUALITY = "coverQuality";

    public static final int DISABLE = -1;
    public static final int ZOOM = 0;
    public static final int FADE = 1;
    public static final int POPUP = 2;

    @Deprecated
    public static final int TOP_LEFT = 1;
    @Deprecated
    public static final int TOP_RIGHT = 2;
    @Deprecated
    public static final int BOTTOM_LEFT = 3;
    @Deprecated
    public static final int BOTTOM_RIGHT = 4;

    public static final int TOP_START = -1;

    public static final int TOP_END = -2;
    public static final int BOTTOM_START = -3;
    public static final int BOTTOM_END = -4;

    public static final int ANIMATION_DEPTH = 1;
    public static final int ANIMATION_CUBE = 2;
    public static final int ANIMATION_COVER = 3;
    public static final int ANIMATION_FLAT = 4;

    public AppearanceManager clone() {
        return new AppearanceManager(this);
    }

    @NonNull
    public static AppearanceManager checkOrCreateAppearanceManager(AppearanceManager manager) {
        AppearanceManager result = manager;
        if (result == null) result = AppearanceManager.getCommonInstance();
        if (result == null) result = new AppearanceManager();
        return result;
    }

    public AppearanceManager() {
    }

    public AppearanceManager(AppearanceManager other) {
        this.csListItemTitleVisibility = other.csListItemTitleVisibility;
        this.csListItemTitleSize = other.csListItemTitleSize;
        this.csListItemTitleColor = other.csListItemTitleColor;
        this.csListItemRadius = other.csListItemRadius;
        this.csListItemWidth = other.csListItemWidth;
        this.csListItemHeight = other.csListItemHeight;
        this.csListItemBorderVisibility = other.csListItemBorderVisibility;
        this.csListItemBorderColor = other.csListItemBorderColor;
        this.csBannerPlaceInterface = other.csBannerPlaceInterface;
        this.csFavoriteListItemInterface = other.csFavoriteListItemInterface;
        this.csListItemInterface = other.csListItemInterface;
        this.csListUGCItemInterface = other.csListUGCItemInterface;
        this.csStoryLoaderView = other.csStoryLoaderView;
        this.csGameReaderLoaderView = other.csGameReaderLoaderView;
        this.storyTouchListener = other.storyTouchListener;
        this.csCustomGoodsWidget = other.csCustomGoodsWidget;
        this.csCustomGoodsItem = other.csCustomGoodsItem;
        this.csListOverscroll = other.csListOverscroll;
        this.csHasLike = other.csHasLike;
        this.csHasFavorite = other.csHasFavorite;
        this.csHasUGC = other.csHasUGC;
        this.csHasShare = other.csHasShare;
        this.csTimerGradientEnable = other.csTimerGradientEnable;
        this.csListItemGradientEnable = other.csListItemGradientEnable;
        this.csFavoriteIcon = other.csFavoriteIcon;
        this.csLikeIcon = other.csLikeIcon;
        this.csDislikeIcon = other.csDislikeIcon;
        this.csShareIcon = other.csShareIcon;
        this.csCloseIcon = other.csCloseIcon;
        this.csRefreshIcon = other.csRefreshIcon;
        this.csSoundIcon = other.csSoundIcon;
        this.csNavBarColor = other.csNavBarColor;
        this.csNightNavBarColor = other.csNightNavBarColor;
        this.csReaderBackgroundColor = other.csReaderBackgroundColor;
        this.csCustomFont = other.csCustomFont;
        this.csCustomBoldFont = other.csCustomBoldFont;
        this.csCustomItalicFont = other.csCustomItalicFont;
        this.csCustomBoldItalicFont = other.csCustomBoldItalicFont;
        this.csCustomSecondaryFont = other.csCustomSecondaryFont;
        this.csCustomSecondaryBoldFont = other.csCustomSecondaryBoldFont;
        this.csCustomSecondaryItalicFont = other.csCustomSecondaryItalicFont;
        this.csCustomSecondaryBoldItalicFont = other.csCustomSecondaryBoldItalicFont;
        this.csTimerGradient = other.csTimerGradient;
        this.csReaderRadius = other.csReaderRadius;
        this.csCoverQuality = other.csCoverQuality;
        this.csCloseOnSwipe = other.csCloseOnSwipe;
        this.csCloseOnOverscroll = other.csCloseOnOverscroll;
        this.csListOpenedItemBorderVisibility = other.csListOpenedItemBorderVisibility;
        this.csListOpenedItemBorderColor = other.csListOpenedItemBorderColor;
        this.csListItemMargin = other.csListItemMargin;
        this.csClosePosition = other.csClosePosition;
        this.csStoryReaderAnimation = other.csStoryReaderAnimation;
        this.csIsDraggable = other.csIsDraggable;
        this.csListItemRatio = other.csListItemRatio;
        this.csColumnCount = other.csColumnCount;
        this.csUGCListItemSimpleAppearance = other.csUGCListItemSimpleAppearance;
    }


    private boolean csListItemTitleVisibility = true;
    private int csListItemTitleSize = -1;
    private int csListItemTitleColor = Color.WHITE;


    private int csListItemRadius = -1;

    private Integer csListItemWidth;
    private Integer csListItemHeight;
    private Integer csColumnCount;
    private Float csListItemRatio;

    private boolean csListItemBorderVisibility = true;
    private int csListItemBorderColor = Color.BLACK;

    private IGetFavoriteListItem csFavoriteListItemInterface;
    private IStoriesListItem csListItemInterface;
    private IStoriesListUGCItem csListUGCItemInterface;

    private ICustomBannerPlaceAppearance csBannerPlaceInterface = new DefaultBannerPlaceAppearance();
    private ICustomBannerListAppearance csBannerListInterface = new DefaultBannerListAppearance();

    private IStoryReaderLoaderView csStoryLoaderView;

    private IGameReaderLoaderView csGameReaderLoaderView;
    private StoryTouchListener storyTouchListener;

    private ICustomGoodsWidget csCustomGoodsWidget;
    private ICustomGoodsItem csCustomGoodsItem;

    private boolean csListOverscroll = true;

    private boolean csHasLike;
    private boolean csHasFavorite;
    private boolean csHasUGC;
    private boolean csHasShare;
    private boolean csTimerGradientEnable = true;
    private boolean csListItemGradientEnable = true;

    int csFavoriteIcon;
    int csLikeIcon;
    int csDislikeIcon;
    int csShareIcon;
    int csCloseIcon;
    int csRefreshIcon;
    int csSoundIcon;
    private int csNavBarColor = Color.BLACK;
    private int csNightNavBarColor = Color.BLACK;
    private int csReaderBackgroundColor = Color.BLACK;
    private ICustomAppearanceIcons customAppearanceIcons;

    private Typeface csCustomFont;

    private Typeface csCustomBoldFont;
    private Typeface csCustomItalicFont;
    private Typeface csCustomBoldItalicFont;
    private Typeface csCustomSecondaryFont;
    private Typeface csCustomSecondaryBoldFont;
    private Typeface csCustomSecondaryItalicFont;
    private Typeface csCustomSecondaryBoldItalicFont;


    private int csListItemMargin = -1;
    private int csClosePosition = BOTTOM_END; //1 - topLeft, 2 - topRight, 3 - bottomLeft, 4 - bottomRight;
    private int csStoryReaderAnimation = ANIMATION_CUBE;

    private int csStoryReaderPresentationStyle = ZOOM;
    private boolean csIsDraggable = true;

    private StoriesGradientObject csTimerGradient;
    private UGCListItemSimpleAppearance csUGCListItemSimpleAppearance
            = new UGCListItemSimpleAppearance();
    private int csReaderRadius = -1;
    private int csCoverQuality = Image.QUALITY_MEDIUM;

    private boolean csCloseOnSwipe = true;
    private boolean csCloseOnOverscroll = true;


    private float getCurrentRatio() {
        if (csListItemHeight != null
                && csListItemWidth != null
                && csListItemHeight > 0
                && csListItemWidth > 0
        )
            return 1f * csListItemHeight / csListItemWidth;
        if (csListItemRatio != null && csListItemRatio > 0) return csListItemRatio;
        InAppStoryManager manager = InAppStoryManager.getInstance();
        CachedSessionData sessionData = manager.iasCore().sessionManager()
                .getSession().sessionData();
        if (sessionData != null) return sessionData.previewAspectRatio;
        return 1f;
    }

    private int getScaledWidth(Context context, Integer containerWidth) {
        return (int) (((containerWidth != null ? containerWidth : Sizes.getScreenSize(context).x) -
                (float) (csColumnCount + 1) * csListItemMargin) / csColumnCount);
    }


    public Integer getRealWidth(Context context) {
        return getRealWidth(context, null);
    }

    public Integer getRealHeight(Context context) {
        return getRealHeight(context, null);
    }

    public Integer getRealWidth(Context context, Integer containerWidth) {
        if (csColumnCount != null && csColumnCount > 0) {
            return getScaledWidth(context, containerWidth);
        } else {
            if (csListItemWidth != null && csListItemWidth > 0)
                return csListItemWidth;
            float ratio = getCurrentRatio();
            if (csListItemHeight != null && csListItemHeight > 0) {
                return (int) (csListItemHeight * ratio);
            } else {
                return (int) (Sizes.dpToPxExt(120, context) * ratio);
            }
        }
    }

    public Integer getRealHeight(Context context, Integer containerWidth) {
        float ratio = getCurrentRatio();
        if (csColumnCount != null && csColumnCount > 0) {
            return (int) (getScaledWidth(context, containerWidth) / ratio);
        } else {
            if (csListItemHeight != null && csListItemHeight > 0)
                return csListItemHeight;
            if (csListItemWidth != null && csListItemWidth > 0) {
                return (int) (csListItemWidth / ratio);
            } else {
                return Sizes.dpToPxExt(120, context);
            }
        }
    }


    public AppearanceManager csUGCListItemSimpleAppearance(UGCListItemSimpleAppearance csUGCListItemSimpleAppearance) {
        this.csUGCListItemSimpleAppearance = csUGCListItemSimpleAppearance;
        return AppearanceManager.this;
    }


    public UGCListItemSimpleAppearance csUGCListItemSimpleAppearance() {
        return csUGCListItemSimpleAppearance;
    }


    public AppearanceManager csReaderBackgroundColor(int csReaderBackgroundColor) {
        this.csReaderBackgroundColor = csReaderBackgroundColor;
        return AppearanceManager.this;
    }

    public int csReaderBackgroundColor() {
        return csReaderBackgroundColor;
    }

    public AppearanceManager csReaderRadius(int csReaderRadius) {

        this.csReaderRadius = csReaderRadius;
        return AppearanceManager.this;
    }

    public int csReaderRadius(Context context) {
        if (csReaderRadius == -1) return Sizes.dpToPxExt(4, context);
        return csReaderRadius;
    }

    public AppearanceManager csColumnCount(int csColumnCount) {
        this.csColumnCount = csColumnCount;
        return AppearanceManager.this;
    }

    public Integer csColumnCount() {
        return csColumnCount;
    }


    public AppearanceManager csTimerGradient(StoriesGradientObject csTimerGradient) {
        this.csTimerGradient = csTimerGradient;
        return AppearanceManager.this;
    }

    public StoriesGradientObject csTimerGradient() {
        return csTimerGradient;
    }

    /**
     * use to set quality for story covers
     *
     * @param csCoverQuality (csCoverQuality) quality for covers
     *                       {@link Image#QUALITY_MEDIUM}
     *                       {@link Image#QUALITY_HIGH}
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
        this.csCloseOnSwipe = closeOnSwipe;
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

    public boolean csListItemGradientEnable() {
        return csListItemGradientEnable;
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


    public AppearanceManager csListItemRatio(Float csListItemRatio) {
        this.csListItemRatio = csListItemRatio;
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

    public AppearanceManager csListOverscroll(boolean csListOverscroll) {
        this.csListOverscroll = csListOverscroll;
        return AppearanceManager.this;
    }

    public StoryTouchListener csStoryTouchListener() {
        return this.storyTouchListener;
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

    public boolean csListOverscroll() {
        return csListOverscroll;
    }

    public boolean csHasUGC() {
        return csHasUGC;
    }

    public boolean csHasShare() {
        return csHasShare;
    }

    public ICustomAppearanceIcons csCustomIcons() {
        if (customAppearanceIcons == null) {
            customAppearanceIcons = new IASDefaultAppearanceIcons(AppearanceManager.this);
        }
        return customAppearanceIcons;
    }

    public AppearanceManager csCustomIcons(
            final CustomFavoriteIconInterface favorite,
            final CustomLikeIconInterface like,
            final CustomDislikeIconInterface dislike,
            final CustomShareIconInterface share,
            final CustomSoundIconInterface sound,
            final CustomCloseIconInterface close,
            final CustomRefreshIconInterface refresh
    ) {
        final IIASDefaultIconCreator creator = new IASDefaultIconCreator();
        customAppearanceIcons = new ICustomAppearanceIcons() {
            @Override
            public CustomIconWithStates favoriteIcon() {
                return favorite != null ? favorite : creator.generateDefaultIcon(csFavoriteIcon());
            }

            @Override
            public CustomIconWithStates likeIcon() {
                return like != null ? like : creator.generateDefaultIcon(csLikeIcon());
            }

            @Override
            public CustomIconWithStates dislikeIcon() {
                return dislike != null ? dislike : creator.generateDefaultIcon(csDislikeIcon());
            }

            @Override
            public CustomIconWithStates shareIcon() {
                return share != null ? share : creator.generateDefaultIcon(csShareIcon());
            }

            @Override
            public CustomIconWithStates soundIcon() {
                return sound != null ? sound : creator.generateDefaultIcon(csSoundIcon());
            }

            @Override
            public CustomIconWithoutStates closeIcon() {
                return close != null ? close : creator.generateDefaultStatelessIcon(csCloseIcon());
            }

            @Override
            public CustomIconWithoutStates refreshIcon() {
                return refresh != null ? refresh : creator.generateDefaultStatelessIcon(csRefreshIcon());
            }
        };
        return this;
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
     * use to turn on/off gradient in stories list cells (for non-custom)
     *
     * @param gradientEnable (gradientEnable) true - to use this feature
     *                       true by default
     * @return {@link AppearanceManager}
     */

    public AppearanceManager csListItemGradientEnable(boolean gradientEnable) {
        this.csListItemGradientEnable = gradientEnable;
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
        if (this != getCommonInstance()) {
            if (getCommonInstance().csFavoriteIcon == 0)
                getCommonInstance().csFavoriteIcon(favoriteIcon);
        }
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
        if (this != getCommonInstance()) {
            if (getCommonInstance().csLikeIcon == 0)
                getCommonInstance().csLikeIcon(likeIcon);
        }
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
        if (this != getCommonInstance()) {
            if (getCommonInstance().csDislikeIcon == 0)
                getCommonInstance().csDislikeIcon(dislikeIcon);
        }
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
        if (this != getCommonInstance()) {
            if (getCommonInstance().csShareIcon == 0)
                getCommonInstance().csShareIcon(shareIcon);
        }
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
        if (this != getCommonInstance()) {
            if (getCommonInstance().csCloseIcon == 0)
                getCommonInstance().csCloseIcon(closeIcon);
        }
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
        if (this != getCommonInstance()) {
            if (getCommonInstance().csRefreshIcon == 0)
                getCommonInstance().csRefreshIcon(refreshIcon);
        }
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
        if (this != getCommonInstance()) {
            if (getCommonInstance().csSoundIcon == 0)
                getCommonInstance().csSoundIcon(soundIcon);
        }
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

    public int csListItemRadius(Context context) {
        if (csListItemRadius == -1) {
            return Sizes.dpToPxExt(16, context);
        }
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
    public AppearanceManager csListItemBorderColor(Integer csListItemBorderColor) {
        if (csListItemBorderColor == null) {
            csListItemBorderVisibility = false;
        } else {
            csListItemBorderVisibility = true;
            this.csListItemBorderColor = csListItemBorderColor;
        }
        return AppearanceManager.this;
    }

    public AppearanceManager csListOpenedItemBorderColor(Integer csListOpenedItemBorderColor) {
        if (csListOpenedItemBorderColor == null) {
            csListOpenedItemBorderVisibility = false;
        } else {
            csListOpenedItemBorderVisibility = true;
            this.csListOpenedItemBorderColor = csListOpenedItemBorderColor;
        }
        return AppearanceManager.this;
    }

    public AppearanceManager csListItemMargin(int csListItemMargin) {
        this.csListItemMargin = csListItemMargin;
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

    public AppearanceManager csStoryReaderPresentationStyle(int csStoryReaderPresentationStyle) {
        this.csStoryReaderPresentationStyle = csStoryReaderPresentationStyle;
        return AppearanceManager.this;
    }


    public boolean csListItemTitleVisibility() {
        return csListItemTitleVisibility;
    }

    public int csListItemTitleSize(Context context) {
        if (csListItemTitleSize == -1) return Sizes.dpToPxExt(12, context);
        return csListItemTitleSize;
    }

    public int csListItemTitleColor() {
        return csListItemTitleColor;
    }

    public Integer csListItemHeight() {
        return csListItemHeight;
    }

    public Float csListItemRatio() {
        return csListItemRatio;
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

    public int csListItemMargin(Context context) {
        if (csListItemMargin == -1) return Sizes.dpToPxExt(4, context);
        return csListItemMargin;
    }

    public int csClosePosition() {
        return csClosePosition;
    }

    public int csStoryReaderAnimation() {
        return csStoryReaderAnimation;
    }

    public int csStoryReaderPresentationStyle() {
        return csStoryReaderPresentationStyle;
    }


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


    public AppearanceManager csStoryLoaderView(IStoryReaderLoaderView csStoryLoaderView) {
        this.csStoryLoaderView = csStoryLoaderView;
        return AppearanceManager.this;
    }

    public IStoryReaderLoaderView csStoryLoaderView() {
        return csStoryLoaderView;
    }

    public AppearanceManager csGameReaderLoaderView(IGameReaderLoaderView csGameReaderLoaderView) {
        this.csGameReaderLoaderView = csGameReaderLoaderView;
        return AppearanceManager.this;
    }

    public IGameReaderLoaderView csGameReaderLoaderView() {
        return csGameReaderLoaderView;
    }

    public IStoriesListItem csListItemInterface() {
        return csListItemInterface;
    }

    public AppearanceManager csListItemInterface(IStoriesListItem csListItemInterface) {
        this.csListItemInterface = csListItemInterface;
        return AppearanceManager.this;
    }

    public ICustomBannerPlaceAppearance csBannerPlaceInterface() {
        return csBannerPlaceInterface != null ? csBannerPlaceInterface : new DefaultBannerPlaceAppearance();
    }


    public ICustomBannerListAppearance csBannerListInterface() {
        return csBannerListInterface != null ? csBannerListInterface : new DefaultBannerListAppearance();
    }

    public AppearanceManager csBannerPlaceInterface(ICustomBannerPlaceAppearance csBannerPlaceInterface) {
        this.csBannerPlaceInterface = csBannerPlaceInterface;
        return AppearanceManager.this;
    }

    public AppearanceManager csBannerListInterface(ICustomBannerListAppearance csBannerListInterface) {
        this.csBannerListInterface = csBannerListInterface;
        return AppearanceManager.this;
    }

    public IStoriesListUGCItem csListUGCItemInterface() {
        return csListUGCItemInterface;
    }

    public AppearanceManager csListUGCItemInterface(IStoriesListUGCItem csListUGCItemInterface) {
        this.csListUGCItemInterface = csListUGCItemInterface;
        return AppearanceManager.this;
    }

    public static View getLoader(Context context, int color) {
        if (commonInstance != null) {
            FrameLayout.LayoutParams relativeParams;
            View v = null;
            if (commonInstance.csStoryLoaderView() != null) {
                v = commonInstance.csStoryLoaderView().getView(context);
            }
            if (v != null) {
                relativeParams = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                relativeParams.gravity = Gravity.CENTER;
                v.setLayoutParams(relativeParams);
                return v;
            }
        }
        LoadProgressBar v = new LoadProgressBar(context);
        v.setColor(color);
        return v;
    }

}
