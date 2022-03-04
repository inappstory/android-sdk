## AppearanceManager

The appearance of the stories list, story reader, game reader and input dialogs is configured through the `AppearanceManager` class. It can be set for the whole SDK, or separately:
1) For storiesList before calling `loadStories()`
2) In onboardings or single calls.

For a common setting, you must call the static method of the class:
```
    AppearanceManager.setCommonInstance(appearanceManager);
```

To set appearanceManager `StoriesList` you should call:
```
    storiesList.setAppearanceManager(appearanceManager);
```

If `AppearanceManager` for the list is not specified, then the settings from the common `AppearanceManager` will be used.


Some parameters in `AppearanceManager` can be set only for common instance:

| Variable                         | Type                |Default |Purpose| Description                                                             |
|----------------------------------|---------------------|--------|-------|-------------------------------------------------------------------------|
| csCustomFont                     | Typeface            | null   |Inputs | the primary regular font in inputs |
| csCustomBoldFont                 | Typeface            | null   |Inputs | the primary bold font in inputs |
| csCustomItalicFont               | Typeface            | null   |Inputs | the primary italic font in inputs |
| csCustomBoldItalicFont           | Typeface            | null   |Inputs | the primary bold italic font in inputs |
| csCustomSecondaryFont            | Typeface            | null   |Inputs | the secondary regular font in inputs |
| csCustomSecondaryBoldFont        | Typeface            | null   |Inputs | the secondary bold font in inputs |
| csCustomSecondaryItalicFont      | Typeface            | null   |Inputs | the secondary italic font in inputs |
| csCustomSecondaryBoldItalicFont  | Typeface            | null   |Inputs | the secondary bold italic font in inputs |
| csIsDraggable                    | Boolean             | true   |Reader | a flag that is responsible for the ability to close the story reader by drag'n'drop |

Another parameters can be set separately for list/onboardings/single:

| Variable                         | Type                |Default |Purpose| Description                                                             |
|----------------------------------|---------------------|--------|-------|-------------------------------------------------------------------------|
| csCustomFont                     | Typeface            | null   |List   | the primary regular font for list cells. It can be set separately only for list cells, but not for inputs |
| csHasLike            			   | Boolean             | false  |Reader | Flag that is responsible for connecting the like / dislike functionality|
| csHasShare           			   | Boolean             | false  |Reader | Flag that is responsible for connecting the sharing functionality       |       
| csHasFavorite        			   | Boolean             | false  |Reader | Flag that is responsible for connecting the functionality of favorite stories |
| csCloseOnSwipe                   | Boolean             | true   |Reader | Flag that is responsible for closing stories by swiping down            |
| csCloseOnOverscroll              | Boolean             | true   |Reader | Flag that is responsible for closing stories by swiping left on the last story or right on the first story |
| csListItemWidth                  | Integer             | null   |List | width of the list cell in pixels                                    |
| csListItemHeight                 | Integer             | null   |List | height of the list cell in pixels                                   |
| csListItemRadius                 | Integer             | 16sp   |List | radius of the list cell in pixels                                                       |
| csListItemTitleSize              | Integer             | 14sp   |List | size of the title                                                       |
| csListItemTitleColor             | Integer             | Color.WHITE |List | title color                                                        |
| csListItemBorderColor            | Integer             | Color.BLACK |List | the border color for the unopened cell                             |
| csListItemMargin                 | Integer             | 4dp    |List | indent between cells                                                    |
| csNavBarColor                    | Integer             | 0      |Reader | color of navigation bar.			                                    |
| csNightNavBarColor               | Integer             | 0      |Reader | color of navigation bar in dark mode. If 0 - we use csNavBarColor		|
| csClosePosition                  | Integer             | 2      |Reader | place, where we display the close button of the story reader (`TOP_LEFT = 1; TOP_RIGHT = 2; BOTTOM_LEFT = 3; BOTTOM_RIGHT = 4;`)|
| csStoryReaderAnimation           | Integer             | 2      |Reader | animation of scrolling through stories in the story reader (`ANIMATION_DEPTH = 1; ANIMATION_CUBE = 2;`)|
| csLikeIcon	                   | Integer(id)         | R.drawable.ic_stories_status_like		|Reader | icon for like button in reader.		|
| csDislikeIcon                    | Integer(id)         | R.drawable.ic_stories_status_dislike		|Reader | icon for dislike button in reader.	|
| csFavoriteIcon                   | Integer(id)         | R.drawable.ic_stories_status_favorite	|Reader | icon for favorite button in reader.	|
| csShareIcon                      | Integer(id)         | R.drawable.ic_share_status				|Reader | icon for share button in reader.		|
| csCloseIcon                      | Integer(id)         | R.drawable.ic_stories_close				|Reader | icon for close button in reader.		|
| csRefreshIcon                    | Integer(id)         | R.drawable.ic_refresh					|Reader | icon for refresh button in reader.	|
| csSoundIcon                      | Integer(id)         | R.drawable.ic_stories_status_sound		|Reader | icon for sound button in reader.		|
| csCoverQuality                   | Integer             | 0 	  |List | quality for stories list covers. If not set - sdk uses medium image quality (`QUALITY_MEDIUM = 1; QUALITY_HIGH = 2;`)|
| csTimerGradientEnable 		   | Boolean             | false   |Reader | Flag that is responsible for show dark gradient behind timer in reader  |

All setters returns `AppearanceManager` instance, and can be set like this:

```
appearanceManager
    .csListItemBorderColor(Color.RED)
    .csListItemMargin(0)
    .csClosePosition(AppearanceManager.BOTTOM_RIGHT)
    .csListItemTitleColor(Color.BLUE)
    .csListItemTitleSize(Sizes.dpToPxExt(20))
```

Also, there are several interfaces in the `AppearanceManager`.

Next two uses to fully customize list cells:

```
appearanceManager
    .csListItemInterface(IStoriesListItem listItemInterface)
```

and:

```
appearanceManager
    .csFavoriteListItemInterface(IGetFavoriteListItem favoriteListItemInterface);
```

This two interfaces can be set separately for each list. More information about this interfaces you can read [here](https://github.com/paperrose/InAppStorySdkKt/blob/master/docs/StoriesList.md#istorieslistitem)

Also `AppearanceManager` has allow you to customize loaders in story reader and game reader with next two interfaces.
This interface must be set for the common `AppearanceManager`.

#### ILoaderView

`ILoaderView iLoaderView` - used to substitute your own loader instead of the default loader
```
public interface ILoaderView {
    View getView();
}
```

Example:
```
globalAppearanceManager.csLoaderView(new ILoaderView() {
    @Override
    public View getView() {
        RelativeLayout v = new RelativeLayout(MainActivity.this);
        v.addView(new View(MainActivity.this) {{
            setLayoutParams(new RelativeLayout.LayoutParams(Sizes.dpToPxExt(48), Sizes.dpToPxExt(48)));
            setBackgroundColor(Color.GREEN);
        }});
        return v;
    }
});
```

#### IGameLoaderView

`IGameLoaderView iGameLoaderView` - used to substitute your own loader instead of the default one on the games screen. This interface must be set for the global `AppearanceManager`.
```
public interface IGameLoaderView {
    View getView(); // When inheriting from an interface, View must return itself
    void setProgress(int progress, int max); // Progress values - from 0 to 100, 100 is transmitted as max 
}
```
