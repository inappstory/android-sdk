## Single stories
    
SDK allows to open one story by its id or slug.
    
```

InAppStoryManager.getInstance().showStory(String storyId, Context context, AppearanceManager manager);
InAppStoryManager.getInstance().showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback /*optional, may be null*/);

interface IShowStoryCallback {
    void onShow(); //Calls after loading data about story from server
    void onError(); //Calls if loading fails
}
```

It may be necessary to perform some action in the application immediately after the stories is loaded. In can be done by setting next callback:

```
InAppStoryManager.getInstance().setSingleLoadCallback(SingleLoadCallback singleLoadCallback) ; 
//equivalent to 'SingleLoad' event

    
public interface SingleLoadCallback {
        void singleLoad(String storyId);
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
