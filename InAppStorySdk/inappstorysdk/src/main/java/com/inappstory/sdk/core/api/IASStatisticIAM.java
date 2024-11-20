package com.inappstory.sdk.core.api;

public interface IASStatisticIAM extends StatDisabled {

    void sendWidgetEvent(String widgetData,
                         int iamId,
                         int duration,
                         String iterationId);

    void sendOpenEvent(int iamId,
                       String iterationId);
    void sendCloseEvent(int iamId,
                        int duration,
                        String iterationId);
}
