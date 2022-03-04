## FAQ

#### 1) Cell reshaping: rectangle, circle 

In order to define a rectangular cell - in the `AppearanceManager` you can use `csListItemWidth(int width)`, `csListItemHeight(int height)`, `csListItemRadius(int radius)`. If you need mor customization - you need to use `csListItemInterface`. 

#### 2) Custom font 

To customize the font of the cell, use `csCustomFont(Typeface font)` in the `AppearanceManager`. There is no font customization in the story reader, the font for stories is automatically downloaded from the backend server.


#### 3) Changing the position of the timer / cross 

The `AppearanceManager` uses `csClosePosition`.

#### 4) Changing the loader in the story reader

The global `AppearanceManager` uses customization via `csLoaderView`.

#### 5) Defining the handler for buttons

Use the `InAppStoryManager.getInstance().setUrlClickCallback(InAppStoryManager.UrlClickCallback callback)` method. Also in the callback, it may be necessary to add the closure of the story reader via:
```
InAppStoryManager.closeStoryReader();
```

#### 6) Changing user's account in the application

Use the `InAppStoryManager.getInstance().setUserId(String userId)` method.
All loaded StoriesList instances will be reloaded (calling `storiesList.loadStories()` is automatical).

#### 7) Adding PTR

In the callback of PTR method, add `storiesList.loadStories()`.

#### 8) Changing tags

Use methods: 
```
InAppStoryManager.getInstance().setTags(ArrayList<String> tags);
InAppStoryManager.getInstance().addTags(ArrayList<String> tags);
InAppStoryManager.getInstance().removeTags(ArrayList<String> tags);
```

#### 9) Favorites

When initializing `AppearanceManager` use the `csHasFavorite(true)` property. In the case of customizing the appearance of the list cells through `IStoriesListItem csListItemInterface`, you must also customize the appearance of the favorites cell using the `IGetFavoriteListItem csFavoriteListItemInterface` interface. In addition, to interact with the favorites cell, add the `storiesList.setOnFavoriteItemClick(StoriesList.OnFavoriteItemClick callback)` handler. When displaying a list of favorites in xml-layout with a list, you must add the `cs_listIsFavorite` attribute.

#### 10) Opening stories from push notifications

In the push notification handler function, add a call to a single story using the `InAppStoryManager.getInstance().showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback)` function.

#### 11) Onboarding

Use the call to `InAppStoryManager.getInstance().showOnboardingStories(List<String> tags, Context context, AppearanceManager manager)`.

#### 12) Like / dislike.

When initializing `AppearanceManager` use the `csHasLike(true)` property.

#### 13) Sharing

When initializing `AppearanceManager`  use the `csHasShare(true)` property.  It is also possible to customize the `InAppStoryManager.getInstance().setShareCallback(ShareCallback shareCallback)` handler.

#### 14) Turn on / off sound by default

In the file `constants.xml` in the `defaultMuted` variable set a value. If `true`, then the sound will be off by default, if `false` - on.

#### 15) Turning sound on / off at runtime

Change the value of the `InAppStoryManager.getInstance().soundOn` flag. 
Example:
```
InAppStoryManager.getInstance().soundOn(true);
```
