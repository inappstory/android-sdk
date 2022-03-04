## InAppStoryManager
Main SDK class. Must be initialized before loading stories from any point.

### Initialization

InAppStoryManager can be initialized from any point with `Context` access (`Application`, `Activity`, `Fragment`, etc.) through `Builder` pattern
```
  new InAppStoryManager.Builder()
      .apiKey(apiKey) //String
      .context(context) //Context
      .userId(userId) //String
      .tags(tags) //ArrayList<String>
      .placeholders(placeholders) //Map<String, String>
      .cacheSize(cacheSize) //int, has defined constants
      .testKey(testKey) //String
      .create();
```

>**Attention!**  
>Method `create()` can generate `DataException` if SDK was not initialized. Strictly recommend to catch `DataException` for additional info.

Context and userId - is not optional parameters. UserId can't be longer than 255 characters. Api key is a SDK authorization key. It can be set through `Builder` or in `values/constants.xml`
```
	<string name="csApiKey">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</string>
```

Other `Builder` parameters are optional.

Besides userId for user you can specify tags and placeholders. `Tags` used for targeting stories in `StoriesList` or onboardings. `Placeholders` - for replacing special variables in the story content.

You can also set amount of space which SDK can use for caching files (images, games, videos) with `cacheSize` parameter. In can be set with one of constants.
```
	CacheSize.SMALL = 15mb; 
	CacheSize.MEDIUM = 110mb; 
	CacheSize.LARGE = 210mb;
```

Parameter `testKey` allows you to test stories in moderation status. 

After initialization you can use `InAppStoryManager` class and its methods via `InAppStoryManager.getInstance()`.

### Methods

`InAppStoryManager` class contains static and non-static methods

#### Static methods

| Method                                        | Return type		| Description							|
|-----------------------------------------------|-----------------------|---------------------------------------------------------------|
| `closeStoryReader()`				| void			| Use to force close stories reader (for example in button click callbacks) |
| `destroy()`			                | void			| Use to clear InAppStoryManager if you want to stop it's background work |
| `logout()`                       		| void			| Same as `destroy()`						|
| `getLibraryVersion()`				| Pair<String, Integer>	| returns version name and version code				|
| `isNull()`      				| boolean		| use to check if InAppStoryManager is not created		|

#### Non-static methods
InAppStoryManager is a singleton. You can use it's non-static methods like this:
```
	InAppStoryManager.getInstance().<method>
```

| Method                                        	| Description							|
|-------------------------------------------------------|---------------------------------------------------------------|
| `setTags(ArrayList<String> tags)`			| Set or replace tags list 					|
| `addTags(ArrayList<String> tags)`			| Add tags to current tags list 				|
| `removeTags(ArrayList<String> tags)`			| Remove passed tags from current tags list			|
| `setPlaceholders(Map<String, String> placeholders)`	| Set or replace placeholders list			|
| `setPlaceholder(String key, String value)`		| Set placeholder to the placeholders list. If you pass null, then the placeholder will be removed|
| `Map<String, String> getPlaceholders()`		| returns current placeholder list				|
| `setTestKey(@NonNull String testKey)`			| Set testKey to test stories in moderation status		|
| `setUserId(@NonNull String userId)`			| Change current userId. UserId can't be null or longer than 255 characters	|
| `showOnboardingStories(Context context, AppearanceManager manager)`	| load and show reader with onboarding stories. Pass context from screen where you want to show reader. AppearanceManager can be null (common AppearanceManager will be used in this case) |
| `showOnboardingStories(List<String> tags, Context context, AppearanceManager manager)`	| same as previous, but you can specify tags for onboardings list |
| `showStory(String storyId, Context context, AppearanceManager manager)`	| load and show story in stories reader by it's id.  |
| `clearCache()`      					| use to check if InAppStoryManager is not created		|

>**Attention!**  
>Method `setUserId(@NonNull String userId)` automatically refresh all storiesList instances in application if userId is changed. It may lead to events generation or callback responses. 

Besides this methods there are some callbacks setters

### Callbacks

#### Enums used in methods:
```
public enum SourceType {
    SINGLE, ONBOARDING, LIST, FAVORITE
}    

public enum CloseReader {
    AUTO, CLICK, SWIPE, CUSTOM
}

public enum ClickAction {
    BUTTON, SWIPE, GAME
}
```

#### Handlers that overrides default behaviour

1) When you click on a button in a story, or on story with a deeplink in `storiesList`
```
InAppStoryManager.getInstance().setUrlClickCallback(UrlClickCallback callback);
```

The `UrlClickCallback` interface contains the `onUrlClick(String url)` method, which must be overrided.
Example:
```
InAppStoryManager.getInstance().setUrlClickCallback(new InAppStoryManager.UrlClickCallback() {
    @Override
    public void onUrlClick(String link) {
        Toast.makeText(context, link, Toast.LENGTH_LONG).show();
    }
});
```
If you need to close the reader when the handler is triggered, you need to call static method ` InAppStoryManager.closeStoryReader()` in `onUrlClick`:
```
InAppStoryManager.getInstance().setUrlClickCallback(new InAppStoryManager.UrlClickCallback() {
    @Override
    public void onUrlClick(String link) {
        InAppStoryManager.closeStoryReader();
    }
});
```
The SDK has a default link handler:
```
Intent i = new Intent(Intent.ACTION_VIEW);
i.setData(Uri.parse(object.getLink().getTarget()));
startActivity(i);
```

It is not used during overriding, so if you want to keep the processing of links that are not required by the application in their default form, then you need to take them into account when overriding.

2) When you click on `Share` button in stories reader or when you click on `Share widget` in story or game 
```
InAppStoryManager.getInstance().setShareCallback(new InAppStoryManager.ShareCallback() {
    @Override
    public void onShare(String url, String title, String description, String shareId) {
        doAction(url, title, description);
    }
});
```

#### Notifications from stories reader
1) When you open story in reader (open Stories reader or swipe between its pages)
```
InAppStoryManager.getInstance().setShowStoryCallback(ShowStoryCallback showStoryCallback); 


public interface ShowStoryCallback {
        void showStory(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   SourceType source);
}
```

2) When you close stories reader
```
InAppStoryManager.getInstance().setCloseStoryCallback(CloseStoryCallback closeStoryCallback);

public interface CloseStoryCallback {

        void closeStory(int id,
                    String title,
                    String tags,
                    int slidesCount,
                    int index,
                    CloseReader action,
                    SourceType source);
}
```

3) When you click on a button in a story, or on story with a deeplink in storiesList. Same as `setUrlClickCallback` but with additional story info.
```
InAppStoryManager.getInstance().setCallToActionCallback(CallToActionCallback callToActionCallback); 

public interface CallToActionCallback {
        void callToAction(int id,
                      String title,
                      String tags,
                      int slidesCount,
                      int index,
                      String link,
                      ClickAction action);
}
```

4) When current visible slide loaded in reader
```
InAppStoryManager.getInstance().setShowSlideCallback(ShowSlideCallback showSlideCallback); 

public interface ShowSlideCallback {
        void showSlide(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   int index);
}
```

5) When you click on `Share` button in stories reader. Does not override default share behaviour.
```
InAppStoryManager.getInstance().setClickOnShareStoryCallback(ClickOnShareStoryCallback clickOnShareStoryCallback); 

public interface ClickOnShareStoryCallback {
        void shareClick(int id,
                    String title,
                    String tags,
                    int slidesCount,
                    int index);
}
```

6) When you click on `Like` or `Dislike` buttons in stories reader.
```
InAppStoryManager.getInstance().setLikeDislikeStoryCallback(LikeDislikeStoryCallback likeDislikeStoryCallback); 

public interface LikeDislikeStoryCallback {
        void likeStory(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   int index,
                   boolean value);

        void dislikeStory(int id,
                      String title,
                      String tags,
                      int slidesCount,
                      int index,
                      boolean value);
}
```

7) When you click on `Favorite` button in stories reader.
```
InAppStoryManager.getInstance().setFavoriteStoryCallback(FavoriteStoryCallback favoriteStoryCallback); 

public interface FavoriteStoryCallback {
        void favoriteStory(int id,
                       String title,
                       String tags,
                       int slidesCount,
                       int index,
                       boolean value);
}
```

#### Notifications from InAppStoryManager methods calls

1) When you call `showStory` and successfully load single story info from server.
```
InAppStoryManager.getInstance().setSingleLoadCallback(SingleLoadCallback singleLoadCallback); 

public interface SingleLoadCallback {
        void singleLoad(String storyId);
}
```

2) When you call `showOnboardingStories` and successfully load stories info from server.
```
InAppStoryManager.getInstance().setOnboardingLoadCallback(OnboardingLoadCallback onboardingLoadCallback); 

public interface OnboardingLoadCallback {
        void onboardingLoad(int count);
}
```

#### Catching load errors in SDK.
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

#### Notifications from Game reader
```
InAppStoryManager.getInstance().setGameCallback(GameCallback gameCallback); 
//can be set with custom implementation or with GameCallbackAdapter class

public interface GameCallback {
        void startGame(int id,
                       String title,
                       String tags,
                       int slidesCount,
                       int index);

        void finishGame(int id,
                        String title,
                        String tags,
                        int slidesCount,
                        int index,
                        String result);

        void closeGame(int id,
                       String title,
                       String tags,
                       int slidesCount,
                       int index);
}
```

