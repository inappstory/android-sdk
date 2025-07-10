package com.inappstory.sdk.core.api;

public interface IASStatisticBannerV1 extends StatDisabled {

    void sendWidgetEvent(
            String widgetName,
            String widgetData,
            int bannerId,
            int slideIndex,
            int slidesTotal,
            long duration,
            String iterationId
    );

    void sendOpenEvent(
            int bannerId,
            int slideIndex,
            int slidesTotal,
            String iterationId
    );

    void sendCloseEvent(
            int bannerId,
            int slideIndex,
            int slidesTotal,
            long duration,
            String iterationId
    );
}
