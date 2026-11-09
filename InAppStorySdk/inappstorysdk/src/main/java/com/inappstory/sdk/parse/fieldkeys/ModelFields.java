package com.inappstory.sdk.parse.fieldkeys;

public class ModelFields {

    public static class BackgroundStructure {
        public static final String color = "color";
    }

    public static class Banner {
        public static final String id = "id";
        public static final String hasLimit = "has_limit";
        public static final String frequencyLimit = "frequency_limit";
        public static final String displayTo = "display_to";
        public static final String displayFrom = "display_from";
        public static final String hasSwipeUp = "has_swipe_up";
        public static final String disableClose = "disable_close";
        public static final String appearance = "appearance";
        public static final String slides = "slides";
        public static final String layoutTemplateVariables = "layout_template_variables";
        public static final String customAttributes = "custom_attributes";
    }

    public static class BannerPlaceModel {
        public static final String banners = "banners";
        public static final String id = "id";
    }

    public static class BannerSlide {
        public static final String slideIndex = "id";
        public static final String slidePayload = "event_payload";
        public static final String duration = "duration";
        public static final String html = "html";
        public static final String resources = "resources";
        public static final String placeholders = "img_placeholders_resources";
    }

    public static class BannerStatisticV1Task {
        public static final String bannerId = "bannerId";
        public static final String event = "event";
        public static final String eventId = "ei";
        public static final String iterationId = "ii";
        public static final String slideIndex = "si";
        public static final String slideTotal = "st";
        public static final String durationMs = "d";
        public static final String widgetId = "wi";
        public static final String widgetLabel = "wl";
        public static final String widgetValue = "wv";
        public static final String widgetAnswer = "wa";
        public static final String widgetAnswerLabel = "wal";
        public static final String widgetAnswerScore = "was";
    }

    public static class BorderStructure {
        public static final String radius = "radius";
        public static final String width = "width";
        public static final String color = "color";
    }

    public static class ButtonStructure {
        public static final String background = "background";
        public static final String border = "border";
        public static final String text = "text";
        public static final String padding = "padding";
    }

    public static class CenterStructure {
        public static final String x = "x";
        public static final String y = "y";
    }

    public static class ConfigV2 {
        public static final String factor = "factor";
        public static final String main = "main";
    }

    public static class ContentResource {
        public static final String url = "url";
        public static final String key = "key";
        public static final String type = "type";
        public static final String purpose = "purpose";
        public static final String filename = "filename";
        public static final String index = "slide_index";
        public static final String rangeStart = "range_start";
        public static final String rangeEnd = "range_end";
    }

    public static class DialogStructure {
        public static final String size = "size";
        public static final String configV2 = "configV2";
    }

    public static class ExceptionLog {
        public static final String timestamp = "timestamp";
        public static final String id = "id";
        public static final String session = "session";
        public static final String message = "message";
        public static final String file = "file";
        public static final String line = "line";
        public static final String cause = "cause";
        public static final String stacktrace = "stacktrace";
    }

    public static class Feed {
        public static final String feedId = "id";
        public static final String hasFavorite = "hasFavorite";
        public static final String stories = "stories";
        public static final String feedCover = "cover";
    }

    public static class GameAcceleratorOptions {
        public static final String frequency = "frequency";
    }

    public static class GameAddToCachePayload {
        public static final String id = "id";
        public static final String cb = "cb";
        public static final String webResource = "resource";
    }

    public static class GameArchiveItem {
        public static final String item = "item";
        public static final String sha1 = "sha1";
    }

    public static class GameCenterData {
        public static final String id = "id";
        public static final String splashScreen = "splashScreen";
        public static final String splashLandscapeScreen = "splashLandscapeScreen";
        public static final String splashAnimation = "splashLottieScreen";
        public static final String resources = "resources";
        public static final String options = "screenOptions";
        public static final String gameLaunchConditions = "launchConditions";
        public static final String archiveItems = "archiveItems";
        public static final String layoutDirection = "languageDirection";
        public static final String loggerLevel = "loggerLevel";
        public static final String url = "downloadUrl";
        public static final String initCode = "initCode";
        public static final String canTryReloadCount = "canTryReloadCount";
        public static final String archiveSize = "archiveSize";
        public static final String archiveSha1 = "archiveSha1";
        public static final String instanceUserData = "instanceUserData";
        public static final String archiveUncompressedSize = "archiveUncompressedSize";
    }

    public static class GameConfigOptions {
        public static final String sessionId = "sessionId";
        public static final String lang = "lang";
        public static final String apiKey = "apiKey";
        public static final String appPackageId = "appPackageId";
        public static final String deviceId = "deviceId";
        public static final String userId = "userId";
        public static final String sdkVersion = "sdkVersion";
        public static final String apiBaseUrl = "apiBaseUrl";
        public static final String userAgent = "userAgent";
        public static final String placeholders = "placeholders";
        public static final String screenOrientation = "screenOrientation";
        public static final String fullScreen = "fullScreen";
        public static final String safeAreaInsets = "safeAreaInsets";
        public static final String userExtraOptions = "variables";
        public static final String gameInstanceId = "gameInstanceId";
    }

    public static class GameDataPlaceholder {
        public static final String name = "name";
        public static final String type = "type";
        public static final String value = "value";
    }

    public static class GameFinishGameOptions {
        public static final String id = "id";
    }

    public static class GameFinishOptions {
        public static final String openUrl = "openUrl";
        public static final String openStory = "openStory";
        public static final String openGameInstance = "openGameInstance";
    }

    public static class GameFinishStoryOptions {
        public static final String id = "id";
        public static final String slideIndex = "slideIndex";
    }

    public static class GameInstance {
        public static final String id = "id";
    }

    public static class GameLaunchConditions {
        public static final String minWebViewVersion = "minWebViewVersion";
    }

    public static class GameLCMinWebViewVersion {
        public static final String minVersion = "minVersion";
        public static final String message = "message";
        public static final String loggerMessage = "loggerMessage";
    }

    public static class GameLoadedConfig {
        public static final String showClose = "showClose";
        public static final String backGesture = "backGesture";
    }

    public static class GameLog {
        public static final String message = "message";
        public static final String logSession = "logSession";
        public static final String webViewVersion = "webViewVersion";
        public static final String stacktrace = "stacktrace";
        public static final String gameInstanceId = "gameInstanceId";
        public static final String type = "type";
        public static final String webViewPackage = "webViewPackage";
        public static final String gameLoaded = "gameLoaded";
        public static final String timestamp = "timestamp";
        public static final String launchTryNumber = "launchTryNumber";
    }

    public static class InGameResourceCallbackObj {
        public static final String id = "id";
        public static final String response = "response";
    }

    public static class InGameResourceDownloadResultObj {
        public static final String result = "result";
        public static final String reason = "reason";
    }

    public static class GameScreenOptions {
        public static final String screenOrientation = "screenOrientation";
        public static final String fullScreen = "fullScreen";
    }

    public static class GameSplashAnimation {
        public static final String url = "url";
        public static final String size = "size";
        public static final String sha1 = "sha1";
        public static final String durationMinimalMs = "durationMinimalMs";
    }

    public static class GameSplashScreen {
        public static final String url = "url";
        public static final String size = "size";
        public static final String sha1 = "sha1";
    }

    public static class GameSTIConfigData {
        public static final String title = "title";
        public static final String button = "button";
        public static final String input = "input";
    }

    public static class GameSTIConfigInputData {
        public static final String placeholder = "placeholder";
        public static final String type = "type";
    }

    public static class GameSTIData {
        public static final String id = "id";
        public static final String cb = "cb";
        public static final String config = "config";
    }

    public static class GameSTIResponse {
        public static final String id = "id";
        public static final String response = "response";

    }

    public static class IAMStatisticV1Task {
        public static final String iamId = "iamId";
        public static final String event = "event";
        public static final String eventId = "ei";
        public static final String iterationId = "ii";
        public static final String slideIndex = "si";
        public static final String layerIndex = "li";
        public static final String slideTotal = "st";
        public static final String durationMs = "d";
        public static final String slideAnalytics = "sa";
        public static final String value = "v";
        public static final String widgetId = "wi";
        public static final String widgetLabel = "wl";
        public static final String widgetValue = "wv";
        public static final String widgetAnswer = "wa";
        public static final String widgetAnswerLabel = "wal";
        public static final String widgetAnswerScore = "was";
    }

    public static class IAMReaderSlideStatObject {
        public static final String slideIndex = "si";
        public static final String duration = "d";
    }

    public static class Image {
        public static final String url = "url";
        public static final String width = "width";
        public static final String height = "height";
        public static final String type = "type";
        public static final String expire = "expire";
    }

    public static class InAppMessage {
        public static final String id = "id";
        public static final String hasPlaceholders = "has_placeholder";
        public static final String hasLimit = "has_limit";
        public static final String frequencyLimit = "frequency_limit";
        public static final String displayTo = "display_to";
        public static final String displayFrom = "display_from";
        public static final String campaignName = "campaign_name";
        public static final String customAttributes = "custom_attributes";
        public static final String appearance = "appearance";
        public static final String layoutTemplateVariables = "layout_template_variables";
        public static final String events = "events";
        public static final String slides = "slides";
        public static final String hasSwipeUp = "has_swipe_up";
        public static final String disableClose = "disable_close";
        public static final String screenType = "type";
    }

    public static class InAppMessageEvent {
        public static final String id = "id";
        public static final String name = "name";
        public static final String priority = "priority";
    }

    public static class InAppMessageFeed {
        public static final String messages = "messages";
    }

    public static class InAppMessageLimit {
        public static final String messageId = "messageId";
        public static final String expire = "expire";
        public static final String canOpen = "canOpen";
    }

    public static class InAppMessageSlide {
        public static final String slideIndex = "slide_index";
        public static final String slidePayload = "event_payload";
        public static final String duration = "duration";
        public static final String html = "html";
        public static final String resources = "resources";
        public static final String placeholders = "img_placeholders_resources";
    }

    public static class InnerShareData {
        public static final String text = "text";
        public static final String title = "title";
        public static final String payload = "payload";
        public static final String url = "url";
    }

    public static class InputStructure {
        public static final String text = "text";
        public static final String background = "background";
        public static final String border = "border";
        public static final String padding = "padding";
        public static final String type = "type";
    }

    public static class JsApiRequestConfig {
        public static final String id = "id";
        public static final String url = "url";
        public static final String method = "method";
        public static final String headers = "headers";
        public static final String params = "params";
        public static final String data = "data";
        public static final String cb = "cb";
        public static final String profilingKey = "profiling_key";
    }

    public static class LayoutResponse {
        public static final String layout = "layout";
        public static final String lastUpdatedAt = "last_updated_at";
    }

    public static class MainV2 {
        public static final String background = "background";
        public static final String border = "border";
        public static final String padding = "padding";
        public static final String input = "input";
        public static final String button = "button";
        public static final String question = "question";
    }

    public static class OnVerticalScrollJSData {
        public static final String scrollY = "scrollY";
        public static final String scrollPercent = "scrollPercent";
        public static final String isScrollableLayer = "isScrollableLayer";
        public static final String cardId = "cardId";
        public static final String slideIndex = "slideIndex";
        public static final String layerIndex = "layerIndex";
    }

    public static class PaddingStructure {
        public static final String top = "top";
        public static final String bottom = "bottom";
        public static final String left = "left";
        public static final String right = "right";
    }

    public static class PreloadGameCenterData {
        public static final String id = "id";
        public static final String splashScreen = "splashScreen";
        public static final String splashLandscapeScreen = "splashLandscapeScreen";
        public static final String splashAnimation = "splashLottieScreen";
        public static final String resources = "resources";
        public static final String url = "downloadUrl";
        public static final String archiveSize = "archiveSize";
        public static final String archiveSha1 = "archiveSha1";
        public static final String archiveUncompressedSize = "archiveUncompressedSize";
        public static final String archiveItems = "archiveItems";
    }

    public static class ProductCart {
        public static final String offers = "offers";
        public static final String price = "price";
        public static final String oldPrice = "oldPrice";
        public static final String priceCurrency = "priceCurrency";
    }

    public static class ProductCartOffer {
        public static final String offerId = "offerId";
        public static final String groupId = "groupId";
        public static final String name = "name";
        public static final String description = "description";
        public static final String url = "url";
        public static final String coverUrl = "coverUrl";
        public static final String imageUrls = "imageUrls";
        public static final String currency = "currency";
        public static final String price = "price";
        public static final String oldPrice = "oldPrice";
        public static final String adult = "adult";
        public static final String availability = "availability";
        public static final String size = "size";
        public static final String color = "color";
        public static final String quantity = "quantity";
    }

    public static class ProductCartUpdateCallbacks {
        public static final String requestId = "requestId";
        public static final String successCb = "successCb";
        public static final String errorCb = "errorCb";
    }

    public static class ProductCartUpdateJSData {
        public static final String offer = "offer";
    }

    public static class QuestionStructure {
        public static final String padding = "padding";
        public static final String text = "text";
    }

    public static class SafeAreaInsets {
        public static final String top = "top";
        public static final String bottom = "bottom";
        public static final String left = "left";
        public static final String right = "right";
    }

    public static class Session {
        public static final String id = "id";
    }

    public static class SessionAsset {
        public static final String url = "url";
        public static final String size = "size";
        public static final String sha1 = "sha1";
        public static final String type = "type";
        public static final String replaceKey = "key";
        public static final String filename = "filename";
        public static final String format = "format";
        public static final String mimeType = "mimeType";
        public static final String purpose = "purpose";
    }

    public static class SessionEditor {
        public static final String url = "url";
        public static final String urlTemplate = "urlTemplate";
        public static final String versionTemplate = "versionTemplate";
        public static final String versionsMap = "versionsMap";
        public static final String config = "config";
        public static final String messages = "messages";
    }

    public static class SessionResponse {
        public static final String session = "session";
        public static final String previewAspectRatio = "preview_aspect_ratio";
        public static final String sessionAssets = "cache";
        public static final String layoutUpdatedAt = "layout_updated_at";
        public static final String isAllowProfiling = "is_allow_profiling";
        public static final String isGameReaderDefaultFullscreen = "is_game_reader_default_fullscreen";
        public static final String isAllowStatV1 = "is_allow_statistic_v1";
        public static final String isAllowStatV2 = "is_allow_statistic_v2";
        public static final String isAllowCrash = "is_allow_crash";
        public static final String debugSessionKey = "debug_session_key";
        public static final String serverTimestamp = "server_timestamp";
        public static final String preloadGame = "preloadGame";
        public static final String placeholders = "placeholders";
        public static final String imagePlaceholders = "image_placeholders";
        public static final String isAllowUgc = "is_allow_ugc";
    }

    public static class ShareObject {
        public static final String url = "url";
        public static final String title = "title";
        public static final String description = "description";
    }

    public static class ShowSlideJSPayload {
        public static final String index = "index";
        public static final String cardId = "cardId";
    }

    public static class SizeStructure {
        public static final String width = "width";
        public static final String height = "height";
        public static final String left = "left";
        public static final String center = "center";
    }

    public static class SlideInCacheData {
        public static final String index = "index";
        public static final String status = "status";
    }

    public static class SlideLeftJSPayload {
        public static final String index = "index";
        public static final String cardId = "cardId";
    }

    public static class SlideLinkObject {
        public static final String elementId = "elementId";
        public static final String type = "type";
        public static final String link = "link";
    }

    public static class SlidePayload {
        public static final String eventType = "event_type";
        public static final String slideIndex = "slide_index";
        public static final String payload = "payload";
    }

    public static class Story {
        public static final String id = "id";
        public static final String title = "title";
        public static final String titleColor = "title_color";
        public static final String statTitle = "stat_title";
        public static final String isFullscreen = "is_full_screen";
        public static final String backgroundColor = "background_color";
        public static final String hasSwipeUp = "has_swipe_up";
        public static final String like = "like";
        public static final String slidesCount = "slides_count";
        public static final String favorite = "favorite";
        public static final String hideInReader = "hide_in_reader";
        public static final String deeplink = "deeplink";
        public static final String isOpened = "is_opened";
        public static final String gameInstance = "game_instance";
        public static final String slides = "slides";
        public static final String image = "image";
        public static final String videoUrl = "video_cover";
        public static final String layoutTemplateVariables = "layout_template_variables";
        public static final String customAttributes = "custom_attributes";
        public static final String ugcPayload = "payload";
        public static final String timelineIsHidden = "hide_timeline";
        public static final String hasShare = "share_functional";
        public static final String hasFavorite = "favorite_functional";
        public static final String hasAudio = "has_audio";
        public static final String hasLike = "like_functional";
        public static final String disableClose = "disable_close";
    }

    public static class StoryLink {
        public static final String type = "type";
        public static final String target = "target";
    }

    public static class StoryLoadedData {
        public static final String id = "id";
        public static final String index = "index";
    }

    public static class StoryPlaceholder {
        public static final String name = "name";
        public static final String defaultVal = "default_value";
    }

    public static class StorySlide {
        public static final String slideIndex = "index";
        public static final String slideTimeline = "timeline";
        public static final String slidePayload = "event_payload";
        public static final String duration = "duration";
        public static final String html = "html";
        public static final String resources = "resources";
        public static final String placeholders = "img_placeholders_resources";
        public static final String isScreenshotShare = "screenshot_share";
    }

    public static class StorySlideTimeline {
        public static final String foregroundColor = "foreground";
        public static final String backgroundColor = "background";
        public static final String isTimelineHidden = "is_timeline_hidden";
    }

    public static class StoryStatisticV2Task {
        public static final String event = "event";
        public static final String timestamp = "ts";
        public static final String userId = "u";
        public static final String sessionId = "s";
        public static final String isFake = "isFake";
        public static final String storyId = "i";
        public static final String feedId = "f";
        public static final String whence = "w";
        public static final String target = "t";
        public static final String cause = "c";
        public static final String slideIndex = "si";
        public static final String slideTotal = "st";
        public static final String durationMs = "d";
        public static final String value = "v";
        public static final String widgetId = "wi";
        public static final String widgetLabel = "wl";
        public static final String widgetValue = "wv";
        public static final String widgetAnswer = "wa";
        public static final String widgetAnswerLabel = "wal";
        public static final String widgetAnswerScore = "was";
        public static final String layoutIndex = "li";
        public static final String mode = "m";
        public static final String fullData = "fullData";
    }

    public static class TextStructure {
        public static final String size = "size";
        public static final String lineHeight = "lineHeight";
        public static final String align = "align";
        public static final String color = "color";
        public static final String value = "value";
        public static final String placeholder = "placeholder";
        public static final String family = "family";
        public static final String weight = "weight";
        public static final String style = "style";
        public static final String limit = "limit";
        public static final String maxLines = "maxLines";
    }

    public static class UGCVersionToSDKBuild {
        public static final String minBuild = "minBuild";
        public static final String editor = "editor";
    }

    public static class UpdateTimelineData {
        public static final String action = "action";
        public static final String currentTime = "currentTime";
        public static final String duration = "duration";
        public static final String slideIndex = "slideIndex";
        public static final String showLoader = "showLoader";
        public static final String showError = "showError";
    }

    public static class UrlObject {
        public static final String url = "url";
    }

    public static class WebResource {
        public static final String key = "key";
        public static final String url = "url";
        public static final String sha1 = "sha1";
        public static final String size = "size";
    }

    public static class WriteClipboardData {
        public static final String type = "type";
        public static final String textValue = "textValue";
    }
}
