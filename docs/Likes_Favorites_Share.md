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
ShareCallback shareCallback = new InAppStoryManager.ShareCallback() {
   @NonNull
    @Override
    public View getView(@NonNull Context context, 
                        @NonNull HashMap<String, Object> data, 
                        @NonNull OverlappingContainerActions actions) {
        return getViewWithSharePanel(context, shareData, actions);
    }

    @Override
    public void viewIsVisible(View view) {
	startSharePanelShowAnimation(view)		
    }


    @Override
    public boolean onBackPress(@NonNull OverlappingContainerActions actions) {
    	startSharePanelHideAnimation(actions)
        return true;
    }
}

InAppStoryManager.getInstance().setShareCallback(shareCallback);
```

`OverlappingContainerActions` is an interface that has to be used to close container for your custom share panel after successful or unsuccessful sharing. It has next signature:
```java
public interface OverlappingContainerActions {
    void closeView(HashMap<String, Object> data); //after sharing you have to pass here an map with key "shared" and sharing result as boolean (true if successful)
}
```

Variable `data` in method `getView` contains pair of key-value: 
```java
IASShareData shareData = data.get("shareData"). 
```

IASShareData is a class that contains share files (list of file paths) or share url. You can use them directly, or if you don't want to customize share logic - you can use class `IASShareManager`. It contains 2 methods: `shareToSpecificApp`, `shareDefault` which takes a parameter `BroadcastReceiver receiver`. For example - you can realize next method and use it from your custom share panel:
```java
private void share(@NonNull Context context,
                       @NonNull IASShareData data,
                       @NonNull OverlappingContainerActions actions,
                       String packageName) {
        IASShareManager shareManager = new IASShareManager();
        if (packageName != null)
            shareManager.shareToSpecificApp(
                    ShareBroadcastReceiver.class,
                    context,
                    data,
                    packageName);
        else
            shareManager.shareDefault(
                    ShareBroadcastReceiver.class,
                    context,
                    data
            );
    }
```

And `ShareBroadcastReceiver` looks like:

```java
class ShareBroadcastReceiver extends BroadcastReceiver {
	@Override
    	public void onReceive(Context context, Intent intent) {
		//Here you need to notify your share callback about successful sharing
	}
}
```

If you want to customize share logic and you want to share images (from game or slide screenshots) - you can use method `getUrisFromShareData(Context context, IASShareData shareData)` from class `IASShareManager`. For example:

```java
Intent getShareIntent(Context context, IASShareData shareData) {
	final Intent sendingIntent = new Intent();
	sendingIntent.setAction(Intent.ACTION_SEND);

	List<Uri> files = getUrisFromShareData(context, shareData);

	if (files.isEmpty()) {
		sendingIntent.setType("text/plain");
	} else {
		sendingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		sendingIntent.setType("image/*");
		if (files.size() > 1) {
			sendingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
			sendingIntent.putParcelableArrayListExtra(
				Intent.EXTRA_STREAM,
				new ArrayList<>(files)
			);
		} else {
			sendingIntent.putExtra(Intent.EXTRA_STREAM, files.get(0));
		}
	}
	return sendingIntent;
}
```

[Here](https://github.com/inappstory/Android-Example/tree/main/kotlinexamples/src/main/java/com/inappstory/kotlinexamples/share) you can look at complete example with custom sharing

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
More about this you can read [here](docs/StoriesList.md#igetfavoritelistitem).

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
    storiesList.layoutManager = GridLayoutManager(context, columnCount,RecyclerView.VERTICAL, false)
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
