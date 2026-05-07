package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.ValueCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.inappstory.sdk.refactoring.shared.ui.IASWebView;
import com.inappstory.sdk.refactoring.stories.ui.contracts.IStoriesContentView;
import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderPageViewModel;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.StringsUtils;

public class StoryReaderPageContent extends IASWebView implements IStoriesContentView {

    private StoryReaderPageViewModel viewModel;

    public void viewModel(StoryReaderPageViewModel viewModel) {
        this.viewModel = viewModel;
        addJavascriptInterface(viewModel, "Android");
    }

    public void touchIsLocked(boolean touchIsLocked) {
        this.touchIsLocked = touchIsLocked;
    }

    private boolean touchIsLocked = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (touchIsLocked) return false;

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float coordinate = motionEvent.getX();
                if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    int sz = (!Sizes.isTablet(getContext()) ?
                            Sizes.getScreenSize(getContext()).x :
                            Sizes.dpToPxExt(400, getContext()));
                    coordinate = sz - coordinate;
                }
                if (viewModel != null) viewModel.updateLatestClickCoordinates(coordinate);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        boolean c = super.dispatchTouchEvent(motionEvent);
        return c;
    }

    public StoryReaderPageContent(
            @NonNull Context context
    ) {
        super(context);
        initPage();
    }

    public StoryReaderPageContent(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
        initPage();
    }

    public StoryReaderPageContent(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        initPage();
    }

    private void initPage() {
        setBackgroundColor(Color.BLACK);
    }

    @Override
    public void gameComplete(String data) {
        if (data != null)
            loadUrl("javascript:game_complete('" + StringsUtils.escapeSingleQuotes(data) + "')");
        else
            loadUrl("javascript:game_complete()");
    }

    @Override
    public void setClientVariables(String extraOptions) {
        loadUrl(
                "javascript:window.set_sdk_client_variables('" +
                        StringsUtils.getEscapedString(
                                StringsUtils.escapeSingleQuotes(extraOptions)
                        )
                        + "')"
        );
    }

    @Override
    public void handleBackPress(String extraOptions) {
        evaluateJavascript("handleBackpress();", null);
    }

    @Override
    public void cartUpdatedResultSuccess(String successCB, String requestId, String cardStr) {
        loadUrl(
                "javascript:window." + successCB + "('" + requestId + "', '" +
                        StringsUtils.escapeSingleQuotes(
                                cardStr
                                        .replaceAll("\\\\n", "\\\\\n")
                                        .replaceAll("\\\\r", "\\\\\r")
                                        .replaceAll("\\\\t", "\\\\\t")
                        ) + "')"
        );
    }

    @Override
    public void cartUpdatedResultError(String errorCB, String requestId, String reason) {
        loadUrl(
                "javascript:window." + errorCB + "('" + requestId + "', '" +
                        StringsUtils.escapeSingleQuotes(reason
                                .replaceAll("\\\\n", "\\\\\n")
                                .replaceAll("\\\\r", "\\\\\r")
                                .replaceAll("\\\\t", "\\\\\t")) + "')"
        );
    }

    @Override
    public void clearSlide(int index) {
        evaluateJavascript("(function(){clear_slide(" + index + ");})()", null);
    }

    private int latestSlideHash = 0;

    @Override
    public void layoutAndSlide(String layout, String slide) {
        if (slide == null) return;
        if (latestSlideHash == slide.hashCode())
            return;
        latestSlideHash = slide.hashCode();
        if (firstLoading) {
            firstLoading = false;
            String content = setDir(
                    injectUnselectableStyle(
                            layout
                                    .replace("//_ratio = 0.66666666666,", "")
                                    .replace("{{%content}}", slide)
                    ),
                    getContext()
            );
            loadSlide(content);
        } else {
            replaceSlide(oldEscape(slide));
        }
    }

    private String oldEscape(String raw) {
        String escaped = raw
                .replaceAll("\"", "\\\\\"")
                .replaceAll("\n", " ")
                .replaceAll("\r", " ");
        return escaped;
    }

    private void loadSlide(String content) {
        post(new Runnable() {
            @Override
            public void run() {
                loadDataWithBaseURL(
                        "file:///data/",
                        content,
                        "text/html; charset=utf-8",
                        "UTF-8",
                        null
                );
            }
        });
    }

    private boolean firstLoading = true;

    private void replaceSlide(String newContent) {
        evaluateJavascript(
                "(function(){show_slide(\"" + newContent + "\");})()",
                null
        );
    }

    @Override
    public void startSlide(final boolean soundOn) {
        String funAfterCheck =
                soundOn ?
                        "story_slide_start('{\"muted\": false}');" :
                        "story_slide_start('{\"muted\": true}');";
        loadUrl(
                "javascript:(function(){" +
                        "if ('story_slide_start' in window) " +
                        "{" +
                        " window." + funAfterCheck +
                        "}" +
                        "})()"
        );
    }

    @Override
    public void restartSlide(boolean soundOn) {
        String funAfterCheck =
                soundOn ?
                        "story_slide_restart('{\"muted\": false}');" :
                        "story_slide_restart('{\"muted\": true}');";
        loadUrl(
                "javascript:(function(){" +
                        "if ('story_slide_restart' in window) " +
                        "{" +
                        " window." + funAfterCheck +
                        "}" +
                        "})()"
        );
    }

    @Override
    public void pauseSlide() {
        loadUrl(
                "javascript:(function(){" +
                        "if ('story_slide_pause' in window) " +
                        "{" +
                        " window.story_slide_pause(); " +
                        "}" +
                        "})()"
        );
    }

    @Override
    public void resumeSlide() {
        loadUrl(
                "javascript:(function() {" +
                        "if ('story_slide_resume' in window) " +
                        "{" +
                        " window.story_slide_resume(); " +
                        "}" +
                        "})()"
        );
    }

    @Override
    public void evaluateJavascript(@NonNull String script, @Nullable ValueCallback<String> resultCallback) {
        post(new Runnable() {
            @Override
            public void run() {
                StoryReaderPageContent.super.evaluateJavascript(script, resultCallback);
            }
        });
    }

    @Override
    public void loadUrl(final String url) {
        post(new Runnable() {
            @Override
            public void run() {
                StoryReaderPageContent.super.loadUrl(url);
            }
        });
    }

    @Override
    public void stopSlide(boolean prepareForRestart) {
        String funAfterCheck = prepareForRestart ?
                "story_slide_stop('{\"prepareForRestart\": true}'); " :
                "story_slide_stop('{\"prepareForRestart\": false}'); ";
        loadUrl(
                "javascript:(function(){" +
                        "if ('story_slide_stop' in window) " +
                        "{" +
                        " window." + funAfterCheck +
                        "}" +
                        "})()"
        );
    }

    @Override
    public void autoSlideEnd() {
        loadUrl(
                "javascript:(function(){" +
                        "if ('story_slide_timer_end' in window) " +
                        "{" +
                        " window.story_slide_timer_end(); " +
                        "}" +
                        "})()"
        );
    }

    @Override
    public void swipeUp() {
        loadUrl("javascript:window.story_slide_swipe_up()");
    }

    @Override
    public void loadJsApiResponse(String result, String cb) {
        evaluateJavascript(cb + "('" + StringsUtils.escapeSingleQuotes(result) + "');", null);
    }

    @Override
    public void changeSoundStatus(boolean soundOn) {
        if (soundOn) {
            loadUrl("javascript:(function(){story_slide_enable_audio();})()");
        } else {
            loadUrl("javascript:(function(){story_slide_disable_audio();})()");
        }
    }

    @Override
    public void cancelDialog(String id) {
        loadUrl("javascript:(function(){story_send_text_input_result(\"" + id + "\", \"\");})()");
    }

    @Override
    public void sendDialog(String id, String data) {
        data = data.replaceAll("\n", "<br>");
        loadUrl("javascript:story_send_text_input_result(\"" + id + "\", \"" + data + "\")");
    }

    @Override
    public void shareComplete(String id, boolean success) {
        if (id == null) return;
        loadUrl("javascript:(function(){share_complete(\"" + id + "\", " + success + ");})()");
    }

    @Override
    public void screenshotShare(String shareId) {
        evaluateJavascript("share_slide_screenshot(\"" + shareId + "\");", null);
    }

    @Override
    public void goodsWidgetComplete(String widgetId) {
        evaluateJavascript("goods_widget_complete(\"" + widgetId + "\");", null);
    }
}
