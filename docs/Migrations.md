## Migration

### From 1.8.x to 1.9.x
Added UGC Editor feature. [Here](https://github.com/inappstory/ugc-android-sdk#readme) is described its usage.

### From 1.6.x or 1.7.x to 1.8.x
Added new feeds feature to `StoriesList` and onboarding stories now has public `feed` parameter. [Here](https://github.com/inappstory/android-sdk/blob/main/docs/StoriesList.md#stories-feed) is described its usage.

Callback for `StoriesList` and its adapter has beed changed (added `feed` parameter to methods). 

```java
interface ListCallback {
    void storiesLoaded(int size, String feed);

    void loadError(String feed);

    void itemClick(int id,
                   int listIndex,
                   String title,
                   String tags,
                   int slidesCount,
                   boolean isFavoriteList,
                   String feed);
}
```

Next callbacks and their adapters for `InAppstoryManager` also have changed (added `feed` parameter to methods)

```java
public interface ErrorCallback {
    void loadListError(String feed);
    void loadOnboardingError(String feed);
    void loadSingleError();
    void cacheError();
    void readerError();
    void emptyLinkError();
    void sessionError();
    void noConnection();
}

public interface OnboardingLoadCallback {
    void onboardingLoad(int count, String feed);
}
```

Also `StoriesLoaded`, `OnboardingLoad` and `StoriesErrorEvent` events now has `getFeed()` method. 

### From 1.5.x to 1.6.x

`setInstance` method for `AppearanceManager` now is deprecated. Use `setCommonInstance` method instead.

`csStoryTouchListener` method for `AppearanceManager` now is deprecated. Use `setStoryTouchListener` method for `StoriesList` instead.
 
If you don't want customize anything and use default implementations - you can call `loadStories()` method from `StoriesList` without setting `AppearanceManager`. 

`InAppStoryManager` can't be initialized through Builder without setting userId (You still can pass empty String, but value can't be null).

For custom stories list cell interface `IStoriesListItem` has changed. Now it returns cached file path instead of web url in `setImage` and `setVideo` methods and calls only after this resouces are cached.
Also poster url and background color were removed from `setVideo`.
```java
interface IStoriesListItem {
    void setImage(View itemView, String imageFilePath, int backgroundColor);
    void setVideo(View itemView, String videoFilePath);
}
```


### 1.5.4 and later
`ClickAction` DEEPLINK value was added. Here describes its usage in [setCallToActionCallback](https://github.com/inappstory/android-sdk/blob/main/docs/InAppStoryManager.md#notifications-from-stories-reader)

### From 1.4.x to 1.5.x
`CloseStoryReaderEvent` was removed from SDK. Use static method `InAppStoryManager.closeStoryReader()` instead


### From 1.3.x to 1.4.x
`targetSdkVersion` in SDK gradle file was updated from 29 to 30. It may be necessary to update `targetSdkVersion` in your project gradle file.

`InAppStoryManager` and `StoriesList` callbacks were added (you should use them instead of `CsEventBus`). For more information read [here](https://github.com/inappstory/android-sdk#inappstorymanager-callbacks).

`InAppStoryManager.closeStoryReader()` method was added to SDK. 
We recommend to use it instead of `CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.CUSTOM))`
