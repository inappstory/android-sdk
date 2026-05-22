package com.inappstory.sdk.core.network.content;

public class RequestFields {
    public static final String BANNER_FIELDS = null;
    public static final String BANNER_EXPAND = "slides,layout_template_variables";

    public static final String BANNER_PLACE_FIELDS = null;
    public static final String BANNER_PLACE_EXPAND = "banners.slides,banners.layout_template_variables";

    public static final String IAM_FIELDS = "id,hasLimit,appearance,events,frequency_limit,campaign_name,has_swipe_up,disable_close,display_from,type,display_to";
    public static final String IAM_EXPAND = "layout_template_variables,slides";

    public static final String LIST_IAM_FIELDS = "messages.id,messages.hasLimit,messages.appearance,messages.events,messages.frequency_limit,messages.campaign_name,messages.has_swipe_up,messages.disable_close,messages.display_from,messages.type,messages.display_to";
    public static final String LIST_IAM_EXPAND = "messages.layout_template_variables,messages.slides";

    public static final String STORY_FIELDS = null;
    public static final String STORY_EXPAND = "slides,layout_template_variables";

    public static final String LIST_STORY_FIELDS = null;
    public static final String LIST_STORY_EXPAND = "slides";

    public static final String FEED_STORY_FIELDS = null;
    public static final String FEED_STORY_EXPAND = "stories.slides";

    public static final String STORY_COVER_FIELDS = "id, background_color, image";
    public static final String STORY_COVER_EXPAND = null;
}
