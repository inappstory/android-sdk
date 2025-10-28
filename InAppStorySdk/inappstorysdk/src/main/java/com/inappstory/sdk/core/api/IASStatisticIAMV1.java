package com.inappstory.sdk.core.api;

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
