## Likes, Share, Favorites

### Likes/Dislikes
To include like functionality in you app use `csHasLike(true)` property in `AppearanceManager` class. It can be set separately for current list or in common instance.

To customize like/dislike icons - use `csLikeIcon` and `csDislikeIcon` properties.

If you need to perform some action in the application after buttons clicks - use next callback:

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

### Share
To include share functionality in you app use `csHasShare(true)` property in `AppearanceManager` class. It can be set separately for current list or in common instance.

To customize share icon - use `csShareIcon` property.

If you need to perform some action in the application after buttons clicks - use next callback:
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

You can also customize default share behaviour (for example - to create your custom share dialog) with next handler:
```
InAppStoryManager.getInstance().setShareCallback(new InAppStoryManager.ShareCallback() {
    @Override
    public void onShare(String url, String title, String description, String shareId) {
        doAction(url, title, description);
    }
});
```

### Favorites
To include favorite functionality in you app use `csHasFavorite(true)` property in `AppearanceManager` class. It turns on favorite cell in lists and favorite button in reader and can be set separately for current list or in common instance.

To customize favorite icon - use `csFavoriteIcon` property.

If you need to perform some action in the application after buttons clicks - use next callback:
```
InAppStoryManager.getInstance().setFavoriteStoryCallback(FavoriteStoryCallback favoriteStoryCallback); 
//equivalent to 'FavoriteStory' event

public interface FavoriteStoryCallback {
        void favoriteStory(int id,
                       String title,
                       String tags,
                       int slidesCount,
                       int index,
                       boolean value);
}
```

If you want to show only favorited stories in list - add `StoriesList` like this:
```
<com.inappstory.sdk.stories.ui.list.StoriesList
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
      	    app:cs_listIsFavorite="true"
	    android:id="@+id/stories_list"/>
      
```
You can also customize favorite cell in list with `csFavoriteListItemInterface` property in `AppearanceManager`
More about this you can read [here](https://github.com/paperrose/InAppStorySdkKt/blob/master/docs/StoriesList.md#igetfavoritelistitem).

To interact with the favorite cell (for example, to open a new window with a list of favorite stories), you need to add a handler:

```
    storiesList.setOnFavoriteItemClick(new StoriesList.OnFavoriteItemClick() {
        @Override
        public void onClick() {
            doAction();
        }
    });
```
