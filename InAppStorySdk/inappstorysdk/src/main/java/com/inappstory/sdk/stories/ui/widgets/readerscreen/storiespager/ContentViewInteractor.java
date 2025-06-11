package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.content.Context;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.ui.screens.IReaderSlideViewModel;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderSlideViewModel;

public interface ContentViewInteractor {
    void setBackgroundColor(int color);
    void loadSlide(String content);
    void replaceSlide(String newContent);

    void startSlide(IASCore core);
    void pauseSlide();
    void resumeSlide();
    void restartSlide(IASCore core);
    void stopSlide();

    void swipeUp();
    void clearSlide(int index);
    void loadJsApiResponse(String result, String cb);
    Context getActivityContext();
    void changeSoundStatus(IASCore core);
    void cancelDialog(String id);
    void sendDialog(String id, String data);
    void destroyView();
    float getCoordinate();
    void shareComplete(String stId, boolean success);
    void freezeUI();
    void unfreezeUI();
    void checkIfClientIsSet();
    void screenshotShare(String id);
    void goodsWidgetComplete(String widgetId);

    void slideViewModel(IReaderSlideViewModel slideViewModel);
}
