package com.inappstory.sdk.core.network.content;

public class RequestFields {
    public static final String BANNER_FIELDS = null;
    public static final String BANNER_EXPAND = "slides,layout_template_variables";

    public static final String BANNER_PLACE_FIELDS = null;
    public static final String BANNER_PLACE_EXPAND = "banners.slides,banners.layout_template_variables";

    public static final String IAM_FIELDS = "id,hasLimit,appearance,events,frequency_limit,campaign_name,has_swipe_up,disable_close,display_from,type,display_to";
    public static final String IAM_EXPAND = "layout_template_variables,slides";

    public static final String LIST_IAM_FIELDS = "messages.id,messages.hasLimit,messages.appearance,messages.events,messages.frequency_limit,messages.campaign_name,messages.has_swipe_up,messages.disable_close,messages.display_from,messages.type,messages.display_to,messages.slides";
    public static final String LIST_IAM_EXPAND = "messages.layout_template_variables,messages.slides";

    public static final String STORY_FIELDS = null;
    public static final String STORY_EXPAND = "slides,layout_template_variables";

    public static final String LIST_STORY_FIELDS = "hide_timeline,share_functional,favorite_functional,has_audio,like_functional,disable_close,is_opened,game_instance,deeplink,hide_in_reader,favorite,slides_count,like,has_swipe_up,image,background_color,payload,video_cover,stat_title,title_color,title,id,slides.index,slides.timeline,slides.duration";
    public static final String LIST_STORY_EXPAND = "slides";

    public static final String FEED_STORY_FIELDS = "hasFavorite,cover,stories.hide_timeline,stories.share_functional,stories.favorite_functional,stories.has_audio,stories.like_functional,stories.disable_close,stories.is_opened,stories.game_instance,stories.deeplink,stories.hide_in_reader,stories.favorite,stories.slides_count,stories.like,stories.has_swipe_up,stories.image,stories.background_color,stories.payload,stories.video_cover,stories.stat_title,stories.title_color,stories.title,stories.id,stories.slides.index,stories.slides.timeline,stories.slides.duration";
    public static final String FEED_STORY_EXPAND = "stories.slides";

    public static final String STORY_COVER_FIELDS = "id, background_color, image";
    public static final String STORY_COVER_EXPAND = null;
}
