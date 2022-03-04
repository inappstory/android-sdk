## Onboarding stories

The library supports work with onboarding stories. 
The function for loading onboarding stories is follows:
```
InAppStoryManager.getInstance().showOnboardingStories(Context context, AppearanceManager manager);
InAppStoryManager.getInstance().showOnboardingStories(List<String> tags, Context context, AppearanceManager manager);
```

Functions are passed, context, display manager (used to determine the position of the close button and animation in the reader) and list of tags for second.
It may be necessary to perform some action in the application immediately after the onboarding stories is loaded (or if they could not appear on screen, since all of them were already displayed earlier or some kind of error occurred). In can be done by setting next callback:

```
InAppStoryManager.getInstance().setOnboardingLoadCallback(OnboardingLoadCallback onboardingLoadCallback); 

public interface OnboardingLoadCallback {
        void onboardingLoad(int count);
}
```

In case of loading error you can get it from ErrorCallback:

```
InAppStoryManager.getInstance().setErrorCallback(ErrorCallback errorCallback); 
//can be set with custom implementation or with ErrorCallbackAdapter class

public interface ErrorCallback {
        void loadListError();
        void loadOnboardingError();
        void loadSingleError();
        void cacheError();
        void readerError();
        void emptyLinkError();
        void sessionError();
        void noConnection();
}
```
