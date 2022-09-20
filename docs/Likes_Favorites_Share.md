## Likes, Share, Favorites

### Likes/Dislikes
To include like functionality in you app use `csHasLike(true)` property in `AppearanceManager` class. It can be set separately for current list or in common instance.

To customize like/dislike icons - use `csLikeIcon` and `csDislikeIcon` properties.

If you need to perform some action in the application after buttons clicks - use next callback:

```java
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
```java
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
```java
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
```java
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
```xml
<com.inappstory.sdk.stories.ui.list.StoriesList
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
      	    app:cs_listIsFavorite="true"
	    android:id="@+id/stories_list"/>
      
```
You can also customize favorite cell in list with `csFavoriteListItemInterface` property in `AppearanceManager`
More about this you can read [here](https://github.com/inappstory/android-sdk/blob/main/docs/StoriesList.md#igetfavoritelistitem).

To interact with the favorite cell (for example, to open a new window with a list of favorite stories), you need to add a handler:

```java
    storiesList.setOnFavoriteItemClick(new StoriesList.OnFavoriteItemClick() {
        @Override
        public void onClick() {
            doAction();
        }
    });
```

Also you can remove favorites through `InAppStoryManager` methods. 
Next method allows you to remove story from favorite by story id

```java
    InAppStoryManager.getInstance().removeFromFavorite(int storyId)
```
To get story id you can implement custom interface `IStoriesListItem` for stories list item and get it from method `setId`.
For example:
```java
    appearanceManager
        .csListItemInterface(new IStoriesListItem() {
            @Override
            public View getView() {
                return LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.custom_story_list_item, null, false);
            }
    
            @Override
            public View getVideoView() {
                return LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.custom_story_list_video_item, null, false);
            }
    
            @Override
            public void setId(View itemView, int storyId) {
                 itemView.findViewById(R.id.removeStoryButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (InAppStoryManager.getInstance() != null) {
                            InAppStoryManager.getInstance().removeFromFavorite(int storyId)
                        }
                    }
                 })
            }
    
            @Override
            public void setTitle(View itemView, String title, Integer titleColor) { 
            }
    
            @Override
            public void setImage(View itemView, String url, int backgroundColor) {
            }
    
            @Override
            public void setVideo(View itemView, String videoPath) {
            }
    
            @Override
            public void setOpened(View itemView, boolean isOpened) {
            }
    
            @Override
            public void setHasAudio(View itemView, boolean hasAudio) {
    
            }
        });
```

Also you can remove from favorites all stories together with next method:

```java
    InAppStoryManager.getInstance().removeAllFavorites()
```

Also you can customize favorites list form.
For example if you want to show stories as grid with 2 columns, you can do next:
```
    val itemWidthInPx = Sizes.dpToPxExt(120) //here place your stories cell width. By default cell width is 120 dp
    val itemPaddingInPx = Sizes.dpToPxExt(12)  //here place padding between stories or edges
    val screenWidth = Sizes.getScreenSize().x
    val columnCount = 2
    val itemPaddingInPx =
        Math.max((screenWidth - columnCount * itemWidthInPx) / (columnCount + 1), 0)
    storiesList.layoutManager = new GridLayoutManager (
            ctx, columnCount,
    RecyclerView.VERTICAL, false
    )
    storiesList.addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val itemCount = parent.adapter?.itemCount ?: 0
            val lp = view.layoutParams as GridLayoutManager.LayoutParams
            val bottomIndex = itemCount - columnCount + (itemCount % columnCount)
            outRect.left = itemPaddingInPx
            outRect.right = 0
            outRect.top =
                if (position < count) itemPaddingInPx else itemPaddingInPx
            outRect.bottom = if (position >= bottomIndex) itemPaddingInPx else 0
        }
    })
```
