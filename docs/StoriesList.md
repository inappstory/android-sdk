## StoriesList
### Initialization

`StoriesList` can be added like any `View` class. For example - via xml

```
	<com.inappstory.sdk.stories.ui.list.StoriesList
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
            app:cs_listIsFavorite="false"
	    android:id="@+id/stories_list"/>
```

Or via code:
```
    StoriesList storiesList = new StoriesList(context);
    addView(storiesList);
```

The `cs_listIsFavorite` attribute is responsible for whether we add a regular list or a list of favorites (true - favorites, false - full list).

### Methods
After SDK initialization you can load stories in `StoriesList`

```
	storiesList.loadStories(); 
```
This method also can be used to reload list (for example in PtR case)

>**Attention!**  
>This method can generate DataException if SDK was not initialized. Strictly recommend to catch `DataException` for additional info.

`StoriesList` is extends `androidx.recyclerview.widget.RecyclerView`. If necessary, you can use all the methods that are in the `RecyclerView` (setting the `layoutManager`, getting the `adapter`, etc.).

### Customization

The appearance of the stories list, as well as some elements of the story reader, is configured through the `AppearanceManager` class. It must be set globally for the library, or separately for the list before calling `loadStories()`.
For a global setting, you must call the static method of the class:
```
    AppearanceManager.setCommonInstance(globalAppearanceManager);
```

To set the list you should call the instance method of the `StoriesList` class:
```
    storiesList.setAppearanceManager(appearanceManager);
```

Next `AppearanceManager` parameters can be set for list appearance:
| Variable                         | Type                | Default| Description                                                             |
|----------------------------------|---------------------|--------|-------------------------------------------------------------------------|
| csHasFavorite                    | Boolean             | false  | Flag that is responsible for connecting the functionality of favorite stories |
| csListItemRadius                 | Integer             | 16dp   | radius for list cell in pixels                                          |
| csListItemWidth                  | Integer             | null   | the width of the list cell in pixels                                    |
| csListItemHeight                 | Integer             | null   | the height of the list cell in pixels                                   |
| csListItemTitleSize              | Integer             | 14sp       | size of the title                                                       |
| csListItemTitleColor             | Integer             | Color.WHITE | title color                                                        |
| csListItemBorderColor            | Integer             | Color.BLACK | the border color for the unopened cell                             |
| csCustomFont                     | Typeface            | null   | the primary regular font, default for the title of the story in the cell. |
| csListItemMargin                 | Integer             | 4dp    | indent between cells                                                    |
| csCoverQuality                   | Integer             | 0      | quality for stories list covers. If not set - sdk uses medium image quality (`QUALITY_MEDIUM = 1; QUALITY_HIGH = 2;`)|

Also, there are several interfaces in the `AppearanceManager`.

#### IStoriesListItem

`IStoriesListItem csListItemInterface` - used for full customization of list items.

>**Attention!**  
>If this interface is specified, other parameters, affecting the appearance of the list cell, will be ignored.

```
    interface IStoriesListItem {
        View getView(); // here you need to pass View - the appearance of the cell
        View getVideoView(); // here you need to pass the View - the appearance of the cell in case the cells use the cover video
        void setTitle(View itemView, String title, Integer titleColor); // itemView is the current cell, in the required View we use the story header. The titleColor parameter can be null
        void setImage(View itemView, String imagePath, int backgroundColor); // itemView - the current cell, in the required View show the story's cover (imagePath - path for local file) or background color if it is absent. For video cover imagePath returns poster frame 
        void setOpened(View itemView, boolean isOpened); // itemView is the current cell, change it as needed if it is opened
        void setHasAudio(View itemView, boolean hasAudio); // itemView - the current cell, change it as needed if this story has audio inside
        void setVideo(View itemView, String videoPath); // itemView is the current cell, in the required View we show the video cover (videoPath - path for local file). To work with video cells, it is recommended to use a class from the VideoPlayer library as a container for displaying video and the loadVideo(String videoPath) method to launch. The VideoPlayer class inherits from TextureView
    }
```

Example:
```
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
            public void setTitle(View itemView, String title, Integer titleColor) {
                ((AppCompatTextView)itemView.findViewById(R.id.title)).setText(title);
            }
    
            @Override
            public void setImage(View itemView, String url, int backgroundColor) {
                // If there is a story with an image and without, then you may need to pre-clear the imageView using setImageResource(0)
                loadImageOrSetBackground(itemView.findViewById(R.id.image), imagePath, backgroundColor);
            }
    
            @Override
            public void setVideo(View itemView, String videoPath) {
                ((VideoPlayer)itemView.findViewById(R.id.video)).loadVideo(videoPath);
            }
    
            @Override
            public void setOpened(View itemView, boolean isOpened) {
                itemView.findViewById(R.id.border).setVisibility(isOpened ?
                                        View.INVISIBLE : View.VISIBLE);
            }
    
            @Override
            public void setHasAudio(View itemView, boolean hasAudio) {
    
            }
        });
```


#### IGetFavoriteListItem

`IGetFavoriteListItem csFavoriteListItemInterface` - used to fully customize the favorite item in the list
```
    public interface IGetFavoriteListItem {
        View getFavoriteItem();
        void bindFavoriteItem(View favCell, List<Integer> backgroundColors, int count);
        void setImages(View favCell, List<String> imagePath, List<Integer> backgroundColors, int count);
    }
```

`View favCell` in `bindFavoriteItem` method - `RelativeLayout`, which contains the View returned by `getFavoriteItem` method. If you need to access the internal View directly - you must firstly set an id for it or access it as `favCell.getChildAt(0)`.

Class `FavoriteImage` contains the methods:
| Method                     | Description                                                                               |
|----------------------------|-------------------------------------------------------------------------------------------|
| `int getId()`              | story id                                                                                  |
| `Image getImage()`         | cover story (the Image object contains the `getUrl()` method to get a link to the picture |
| `int getBackgroundColor()` | background color                                                                          |

Example:
```
    appearanceManager
        .csFavoriteListItemInterface(new IGetFavoriteListItem() {
            @Override
            public View getFavoriteItem() {
                return LayoutInflater.from(getActivity()).inflate(R.layout.item_story_custom_fav_new,
                    null, false);
            }

            @Override
            public void bindFavoriteItem(View view, List<Integer> backColors, int count) {
                AppCompatTextView title = view.findViewById(R.id.title);
                title.setText(getResources().getString(R.string.favorites));
                RelativeLayout imageViewLayout = view.findViewById(R.id.container);
                imageViewLayout.removeAllViews();
                bindFavoriteCellImages(imageViewLayout, null, backColors, count);
            }

            @Override
            public void setImages(View view, List<String> images, List<Integer> backColors, int count) {
                bindFavoriteCellImages(view.findViewById(R.id.container), images, backColors, count);
            }
        });
```

For more information about `AppearanceManager` read [this](https://github.com/paperrose/InAppStorySdkKt/blob/master/docs/AppearanceManager.md).

Also, to interact with the favorite cell (for example, to open a new window with a list of favorite stories), you need to add a handler:
```
    storiesList.setOnFavoriteItemClick(new StoriesList.OnFavoriteItemClick() {
        @Override
        public void onClick() {
            doAction();
        }
    });
```

Clicks to list cells also can be customized with next handler (for example - if you want to add click touch animations):
```
    storiesList.setStoryTouchListener(StoryTouchListener touchListener);
    
    public interface StoryTouchListener {
    	void touchDown(View view, int position);

    	void touchUp(View view, int position);
    }
```


### Callbacks
`StoriesList` actions (loading and clicks) can be obtained with `ListCallback`. It can be set with custom implementation or with `ListCallbackAdapter` (default implementation for `ListCallback` with empty methods) class
```
    storiesList.setCallback(ListCallback callback);
    
    public interface ListCallback {
        void storiesLoaded(int size); //the list of stories has been loaded, the widget
        is ready to work (triggered every time when the list is loaded, including at refresh).
        The event contains the `int getCount()` method - the number of stories
        void loadError(); //the list of stories hasn't been loaded due to error
        void itemClick(int id,
                       int listIndex,
                       String title,
                       String tags,
                       int slidesCount,
                       boolean isFavoriteList); //user click on StoriesList item.
}
```
