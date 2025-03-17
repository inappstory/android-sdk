package com.inappstory.sdk.games.domain.reader;

public interface IGameReaderViewModelJS {

    void logMethod(String payload);

    int pausePlaybackOtherApp();

    void openUrl(String data);

    void vibrate(int[] vibratePattern);

    void gameInstanceSetLocalData(String data, boolean sendToServer);

    String gameInstanceGetLocalData();

    void jsEvent(
            String name,
            String data
    );

    void gameShouldForegroundCallback(String data);

    void gameLoaded();

    void gameLoadFailed(String reason, boolean canTryReload);

    void reloadGameReader();

    void initUserAccelerationSensor(String options);

    void startUserAccelerationSensor();

    void stopUserAccelerationSensor();

    void sendApiRequest(String data);

    void setAudioManagerMode(String mode);

    void gameComplete(String data, String eventData, String urlOrOptions);

    void gameStatisticEvent(String name, String data);

    void showGoodsWidget(String id, String skus);

    void share(String id, String data);

    void openFilePicker(String data);

    boolean hasFilePicker();


}
