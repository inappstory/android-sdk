package com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.EmptyNetworkErrorCallback;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionDTOCallbackAdapter;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.utils.network.NetworkClient;
import com.inappstory.sdk.core.utils.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.utils.network.models.Response;
import com.inappstory.sdk.game.reader.GameLaunchData;
import com.inappstory.sdk.stories.uidomain.reader.page.IStoriesReaderPageViewModel;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.WebPageConvertCallback;
import com.inappstory.sdk.stories.utils.WebPageConverter;
import com.inappstory.sdk.utils.SingleTimeLiveEvent;

import java.lang.reflect.Type;

public class StoriesWebViewDisplayViewModel implements IStoriesWebViewDisplayViewModel {
    public StoriesWebViewDisplayViewModel(IStoriesReaderPageViewModel pageViewModel) {
        this.pageViewModel = pageViewModel;
    }

    private MutableLiveData<SlideContentState> currentSlideContentState = new MutableLiveData<>();
    private MutableLiveData<StoryDisplayState> storyDisplayState = new MutableLiveData<>();
    private SingleTimeLiveEvent<String> evaluateJSCalls = new SingleTimeLiveEvent<>();
    private SingleTimeLiveEvent<String> loadUrlCalls = new SingleTimeLiveEvent<>();

    private IStoriesReaderPageViewModel pageViewModel;


    @Override
    public LiveData<String> evaluateJSCalls() {
        return evaluateJSCalls;
    }

    @Override
    public LiveData<String> loadUrlCalls() {
        return loadUrlCalls;
    }

    @Override
    public LiveData<SlideContentState> slideContentState() {
        return currentSlideContentState;
    }

    @Override
    public int storyId() {
        return pageViewModel.getState().storyId();
    }

    @Override
    public void freezeUI() {

    }

    @Override
    public void unfreezeUI() {

    }

    StoriesDisplayClickSide lastClickSide = StoriesDisplayClickSide.NOTHING;

    @Override
    public void setLastClickSide(StoriesDisplayClickSide side) {
        lastClickSide = side;
    }

    @Override
    public boolean isUIFrozen() {
        return false;
    }

    @Override
    public StoryDisplayState getStoryDisplayState() {
        return storyDisplayState.getValue();
    }

    @Override
    public void setStoryDisplayState(StoryDisplayState state) {
        StoryDisplayState localState = storyDisplayState.getValue() == null ?
                state.firstLoading() : state;
        storyDisplayState.postValue(localState);
        setSlideContentState(localState);
    }

    @Override
    public void slideClick(String payload) {
        if (payload == null || payload.isEmpty() || payload.equals("test")) {
            if (lastClickSide == StoriesDisplayClickSide.RIGHT) {
                pageViewModel.openNextSlide();
            } else if (lastClickSide == StoriesDisplayClickSide.LEFT) {
                pageViewModel.openPrevSlide();
            } else {
                //TODO - log error
            }
        } else if (payload.equals("forbidden")) {
            if (lastClickSide == StoriesDisplayClickSide.LEFT) {
                pageViewModel.openPrevSlide();
            }
        } else {
            pageViewModel.clickWithPayload(payload);
        }
        lastClickSide = StoriesDisplayClickSide.NOTHING;
    }

    @Override
    public void slideLoadError(int index) {

    }

    @Override
    public void changeIndex(int index) {

    }

    @Override
    public void showStorySlide(int id, int index) {

    }

    @Override
    public void sendApiRequest(String data) {

    }

    @Override
    public void openGameReaderWithoutGameCenter(GameLaunchData launchData) {

    }

    @Override
    public void openGameReaderFromGameCenter(String gameInstanceId) {

    }

    @Override
    public void setAudioManagerMode(String mode) {

    }

    @Override
    public void storyShowNext() {

    }

    @Override
    public void storyShowPrev() {

    }

    @Override
    public void resetTimers() {

    }

    @Override
    public void nextSlide() {

    }

    @Override
    public void restartSlideWithDuration(long duration) {

    }

    @Override
    public void storyShowTextInput(String id, String data) {

    }

    @Override
    public void jsSlideStarted() {
        pageViewModel.jsSlideStarted();
    }

    @Override
    public void slideResumed(double startTime) {

    }

    @Override
    public void jsSlideLoaded(int slideIndex) {
        pageViewModel.jsSlideLoaded(slideIndex);
    }

    @Override
    public void storyStatisticEvent(String name, String data, String eventData) {

    }

    @Override
    public void share(String id, String data) {

    }

    @Override
    public void pauseUI() {

    }

    @Override
    public void resumeUI() {

    }

    @Override
    public void storySendData(final String data) {
        if (!IASCore.getInstance().getSendStatistic()) return;
        final NetworkClient networkClient = IASCore.getInstance().getNetworkClient();
        if (networkClient == null) {
            return;
        }
        final int storyId = pageViewModel.getState().storyId();
        IASCore.getInstance().getSession(new IGetSessionDTOCallbackAdapter(
                new EmptyNetworkErrorCallback()
        ) {
            @Override
            public void onSuccess(SessionDTO response) {
                networkClient.enqueue(
                        networkClient.getApi().sendStoryData(
                                Integer.toString(storyId),
                                data,
                                response.getId()
                        ),
                        new NetworkCallback<Response>() {
                            @Override
                            public void onSuccess(Response response) {

                            }

                            @Override
                            public Type getType() {
                                return null;
                            }
                        }
                );
            }
        });
    }

    @Override
    public void storySetLocalData(String data, boolean sendToServer) {
        final int storyId = pageViewModel.getState().storyId();
        KeyValueStorage.saveString("story" + storyId + "__" + InAppStoryManager.getInstance().getUserId(), data);
        if (sendToServer)
            storySendData(data);
    }

    @Override
    public String storyGetLocalData() {
        String res = KeyValueStorage.getString("story" + pageViewModel.getState().storyId()
                + "__" + InAppStoryManager.getInstance().getUserId());
        return res == null ? "" : res;
    }

    @Override
    public void pauseSlide() {

    }

    @Override
    public void resumeSlide() {

    }

    @Override
    public void lockStoriesDisplayContainer() {

    }

    @Override
    public void setStateAsLoaded() {

    }

    @Override
    public void jsCallStartSlide() {
        String funAfterCheck = IASCore.getInstance().isSoundOn() ?
                "story_slide_start('{\"muted\": false}');" :
                "story_slide_start('{\"muted\": true}');";
        String call = "javascript:(function(){" +
                "if ('story_slide_start' in window) " +
                "{" +
                " window." + funAfterCheck +
                "}" +
                "})()";
        loadUrlCalls.postValue(call);
    }

    @Override
    public void jsCallStopSlide() {
        String call = "javascript:(function() {" +
                "if ('story_slide_stop' in window) " +
                "{ window.story_slide_stop(); }" +
                "})()";
        loadUrlCalls.postValue(call);
    }

    @Override
    public void jsCallPauseSlide() {
        String call = "javascript:(function() {" +
                "if ('story_slide_pause' in window) " +
                "{ window.story_slide_pause(); }" +
                "})()";
        loadUrlCalls.postValue(call);
    }

    @Override
    public void jsCallResumeSlide() {
        String call = "javascript:(function() {" +
                "if ('story_slide_resume' in window) " +
                "{ window.story_slide_resume(); }" +
                "})()";
        loadUrlCalls.postValue(call);
    }

    private void setSlideContentState(@NonNull StoryDisplayState state) {
        IStoryDTO storyDTO = pageViewModel.storyModel().getValue();
        if (storyDTO == null) return;
        Log.e("cacheSlideLoaded", "setSlideContentState " + storyDTO.getId() + " " + state.slideIndex());
        new WebPageConverter().replaceDataAndLoad(
                storyDTO,
                state.slideIndex(),
                new WebPageConvertCallback() {
                    @Override
                    public void onConvert(String content, String layout, int slideIndex) {
                        currentSlideContentState.postValue(new SlideContentState(layout, content));
                    }
                }
        );
    }
}
