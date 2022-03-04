## Migration

### From 1.5.x to 1.6.x

`setInstance` method for `AppearanceManager` now is deprecated. Use `setCommonInstance` method instead.

`csStoryTouchListener` method for `AppearanceManager` now is deprecated. Use `setStoryTouchListener` method for `StoriesList` instead.
 
If you don't want customize anything and use default implementations - you can call `loadStories()` method from `StoriesList` without setting `AppearanceManager`. 

`InAppStoryManager` can't be initialized through Builder without setting userId (You still can pass empty String, but value can't be null).

For custom stories list cell interface `IStoriesListItem` has changed. Now it returns cached file path instead of web url in `setImage` and `setVideo` methods and calls only after this resouces are cached.
Also poster url and background color were removed from `setVideo`.
```
interface IStoriesListItem {
    void setImage(View itemView, String imageFilePath, int backgroundColor);
    void setVideo(View itemView, String videoFilePath);
}
```

### From 1.4.x to 1.5.x
`CloseStoryReaderEvent` was removed from SDK. Use static method `InAppStoryManager.closeStoryReader()` instead

### From 1.3.x to 1.4.x
`targetSdkVersion` in SDK gradle file was updated from 29 to 30. It may be necessary to update `targetSdkVersion` in your project gradle file.

`InAppStoryManager` and `StoriesList` callbacks were added (you can use them instead of `CsEventBus`). For more information read [here](https://github.com/inappstory/android-sdk#inappstorymanager-callbacks).

`InAppStoryManager.closeStoryReader()` method was added to SDK. 
We recommend to use it instead of `CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.CUSTOM))`
