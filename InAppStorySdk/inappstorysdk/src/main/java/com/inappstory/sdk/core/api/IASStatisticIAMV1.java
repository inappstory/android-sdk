package com.inappstory.sdk.core.api;

import com.inappstory.sdk.stories.api.models.SlideLayerKey;

public interface IASStatisticIAMV1 extends StatDisabled {

    void sendWidgetEvent(
            String widgetName,
            String widgetData,
            int iamId,
            int slideIndex,
            int slidesTotal,
            long duration,
            String iterationId
    );

    void addScrollEvent(SlideLayerKey key, float value);

    void sendSlideScrollEvents(String iterationId);

    void sendOpenEvent(
            int iamId,
            int slideIndex,
            int slidesTotal,
            String iterationId,
            boolean useIterationId
    );

    void sendCloseEvent(
            int iamId,
            int slideIndex,
            int slidesTotal,
            long duration,
            String slideAnalytics,
            String iterationId
    );
}
