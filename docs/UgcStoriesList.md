## UgcStoriesList
### Initialization

Starting from version 1.12.x was added `UgcStoriesList` class to view UGC stories.
It can be used like any `View` class. For example - via xml

```xml
	<com.inappstory.sdk.stories.ui.ugclist.UgcStoriesList
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
	    android:id="@+id/ugc_stories_list"/>
```

Or via code:
```java
    val ugcStoriesList = UgcStoriesList(context);
    addView(ugcStoriesList);
```

### Methods
After SDK initialization you can load stories in `UgcStoriesList` with one of next methods

```java
    ugcStoriesList.loadStories(filter: String);  //use if you want to pass filter as json string

    ugcStoriesList.loadStories(); //use if you want load to show ugc stories without any filter

    ugcStoriesList.loadStories(filter: HashMap<String, Any?>);   //use if you want to pass filter as HashMap
```
This method also can be used to reload list (for example in PtR case)

>**Attention!**  
>This method can generate DataException if SDK was not initialized. Strictly recommend to catch `DataException` for additional info.

`UgcStoriesList` is extends `androidx.recyclerview.widget.RecyclerView`. If necessary, you can use all the methods that are in the `RecyclerView` (setting the `layoutManager`, getting the `adapter`, etc.).

### Customization and Callbacks

`UgcStoriesList` has same ways of customization and same callbacks as usual `StoriesList`. For more information you can read [this](docs/StoriesList.md#customization) and [this](docs/AppearanceManager.md)

>**Attention!**  
>Current version of `UgcStoriesList` does not support favorites, like/dislike and share features. So relevant settings in `AppearanceManager` (`csHasFavorite`, `csHasShare`, `csHasLike` and `csFavoriteListItemInterface`) - will not affect on stories list
