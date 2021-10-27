# InAppStory

A library for embedding stories into an application with customization.

## Requirements

The minimum SDK version is 19 (Android 4.4).

The library is intended for Phone and Tablet projects (not intended for Android TV or Android Wear applications).

## Adding to the project

Add jitpack maven repo to the root `build.gradle` in the `repositories` section :
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

In the project `build.gradle` (app level) in the `dependencies` section add:
```
implementation 'com.github.inappstory:android-sdk:1.3.17'
```

Also for correct work in `dependencies` you need to add:
```
implementation 'androidx.recyclerview:recyclerview:1.1.0'
implementation 'androidx.webkit:webkit:1.4.0'
```

### Additional restrictions

If your project uses proguard obfuscation, then in the `proguard-rules.pro` file you need to write this:
```
-keepattributes *Annotation*

-keepclassmembers class * {
    @com.inappstory.sdk.eventbus.CsSubscribe <methods>;
}

-keep enum com.inappstory.sdk.eventbus.CsThreadMode { *; }

keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}

-keep public class com.inappstory.sdk.** {
    *;
}
```

## Initialization in the project

**For further work in the file `res/values/constants.xml` you need to add the string:**
```
<string name="csApiKey">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</string>
```

To initialize the library in the `Application`, `Activity`, `View` class (or any other with access to the `Context` object), the  `InAppStoryManager.Builder()` class is used. The class contains the following parameters (and setters of the same name):

| Variable           | Type                | Required | Default| Description                                                             |
|--------------------|---------------------|----------|--------|-------------------------------------------------------------------------|
| context            | Context             | yes      |        | Application context                                                     |
| userId             | String              | no       |        | Unique text user identifier (id, login, etc ...) that refers to stories |
| apiKey             | String              | no       |        | By default, the key is set in the csApiKey string. You can change it on runtime, using this parameter. Non-empty csApiKey in `res/values/constants.xml` still needed |
| testKey            | String              | no       |        | Test integration key for testing stories on the device                  |
| closeOnSwipe       | Boolean             | no       | true   | Flag that is responsible for closing stories by swiping down            |
| closeOnOverscroll  | Boolean             | no       | true   | Flag that is responsible for closing stories by swiping left on the last story or right on the first story |
| cacheSize			 | Integer             | no       | 0	   | Defines amount of space which SDK can use for caching files (`CacheSize.SMALL = 15mb; CacheSize.MEDIUM = 110mb; CacheSize.LARGE = 210mb;`)|
| tags               | ArrayList<String>   | no       |        | Tags for targeting stories                                              |
| placeholders       | Map<String, String> | no       |        | Placeholders for replacing special variables in the story content       |

>**Attention!**
>If you pass *testKey*, then the library will display the stories only in the **"Moderation"** status

An example of initializing an `InAppStoryManager`:
```
new InAppStoryManager.Builder()
    .context(context)
    .closeOnSwipe(true)
    .closeOnOverscroll(true)
    .userId(userId)
    .apiKey(apiKey)
    .testKey(testKey)
    .tags(tags)
    .placeholders(placeholders)
    .create();
```
    
Add the following code to the layout where you need to display stories list:
```
<com.inappstory.sdk.stories.ui.list.StoriesList
    android:layout_width="match_parent"
    android:id="@+id/storiesList"
    app:cs_listIsFavorite="false"
    android:layout_height="wrap_content"/>
```
The cs_listIsFavorite attribute is responsible for whether we add a regular list or a list of favorites (true - favorites, false - full list).

Or add via code:
```
StoriesList storiesList = new StoriesList(context);
addView(storiesList);
```

To load list items from the `storiesList` object after initializing `InAppStoryManager`, call the `loadStories()` method. The method can also be used to reload the list.

Also, the `InAppStoryManager` class contains a static method `destroy` for cleaning up:
```
InAppStoryManager.destroy();
```
    
Also `InAppStoryManager` contains methods:
| Method                                                      | Description                                                  |
|-------------------------------------------------------------|--------------------------------------------------------------|
| `setTags(ArrayList<String> tags)`                           | Set tags list                                                |
| `addTags(ArrayList<String> tags)`                           | Add tags to the tags list                                    |
| `removeTags(ArrayList<String> tags)`                        | Remove tags from the tags list                               |
| `setPlaceholders(@NonNull Map<String, String> placeholders)`| Set placeholders list                                        |
| `Map<String, String> getPlaceholders()`                     | Get placeholders list                                        |
| `setPlaceholder(String key, String value)`                  | Set placeholder to the placeholders list. If you pass null, then the placeholder will be remove|
| `clearCache()`                                              | Clear all cached images, videos, game files                  |


If the application supports multiple accounts, then you can implement switch user ID. To change user ID, you should use:
```
InAppStoryManager.getInstance().setUserId(userId)
```

To change the `apiKey` parameter, you will need to reinitialize the `InAppStoryManager` (see the initialization example). This will remove the old instance.
To change the `testKey` parameter, you can use the method:
```
InAppStoryManager.getInstance().setTestKey(String testKey).
```

## Parameters

### StoriesList

The class inherits from `androidx.recyclerview.widget.RecyclerView`. If necessary, you can use all the methods that are in the `RecyclerView` (setting the `layoutManager`, getting the `adapter`, etc.).
The appearance of the stories list, as well as some elements of the story reader, is configured through the `AppearanceManager` class. It must be set globally for the library, or separately for the list before calling `loadStories()`.
For a global setting, you must call the static method of the class:
```
AppearanceManager.setInstance(globalAppearanceManager);
```
    
To set the list you should call the instance method of the `StoriesList` class:
```
storiesList.setAppearanceManager(appearanceManager);
```

If the method for the list is not specified, then the settings from the global `AppearanceManager` will be used. If it's not specified, then a `DataException` will be thrown.

The `AppearanceManager` contains the following parameters (and their corresponding setters):
| Variable                         | Type                | Default| Description                                                             |
|----------------------------------|---------------------|--------|-------------------------------------------------------------------------|
| csHasLike            			   | Boolean             | false  | Flag that is responsible for connecting the like / dislike functionality|
| csHasShare           			   | Boolean             | false  | Flag that is responsible for connecting the sharing functionality       |       
| csHasFavorite        			   | Boolean             | false  | Flag that is responsible for connecting the functionality of favorite stories |
| csListItemWidth                  | Integer             | null   | the width of the list cell in pixels                                    |
| csListItemHeight                 | Integer             | null   | the height of the list cell in pixels                                   |
| csListItemTitleSize              | Integer             |        | size of the title                                                       |
| csListItemTitleColor             | Integer             | Color.WHITE | title color                                                        |
| csListItemBorderColor            | Integer             | Color.BLACK | the border color for the unopened cell                             |
| csCustomFont                     | Typeface            |        | the primary regular font, default for the title of the story in the cell. For reader dialogs must be set in  the global `AppearanceManager` |
| csCustomBoldFont                 | Typeface            |        | the primary bold font, set for reader dialogs and only in the global `AppearanceManager` |
| csCustomItalicFont               | Typeface            |        | the primary italic font, set for reader dialogs and only in the global `AppearanceManager` |
| csCustomBoldItalicFont           | Typeface            |        | the primary bold italic font, set for reader dialogs and only in the global `AppearanceManager` |
| csCustomSecondaryFont            | Typeface            |        | the secondary regular font, set for reader dialogs and only in the global `AppearanceManager` |
| csCustomSecondaryBoldFont        | Typeface            |        | the secondary bold font, set for reader dialogs and only in the global `AppearanceManager` |
| csCustomSecondaryItalicFont      | Typeface            |        | the secondary italic font, set for reader dialogs and only in the global `AppearanceManager` |
| csCustomSecondaryBoldItalicFont  | Typeface            |        | the secondary bold italic font, set for reader dialogs and only in the global `AppearanceManager` |
| csListItemMargin                 | Integer             | 4dp    | indent between cells                                                    |
| csNavBarColor                    | Integer             | 0      | color of navigation bar.			                                    |
| csNightNavBarColor               | Integer             | 0      | color of navigation bar in dark mode. If 0 - we use csNavBarColor		|
| csClosePosition                  | Integer             | 2      | place, where we display the close button of the story reader (`TOP_LEFT = 1; TOP_RIGHT = 2; BOTTOM_LEFT = 3; BOTTOM_RIGHT = 4;`)|
| csStoryReaderAnimation           | Integer             | 2      | animation of scrolling through stories in the story reader (`ANIMATION_DEPTH = 1; ANIMATION_CUBE = 2;`)|
| csIsDraggable                    | Boolean             | true   | a flag that is responsible for the ability to close the story reader by drag'n'drop. This flag is set only for the global `AppearanceManager`|
| csLikeIcon	                   | Integer(id)         | R.drawable.ic_stories_status_like		| icon for like button in reader.		|
| csDislikeIcon                    | Integer(id)         | R.drawable.ic_stories_status_dislike		| icon for dislike button in reader.	|
| csFavoriteIcon                   | Integer(id)         | R.drawable.ic_stories_status_favorite	| icon for favorite button in reader.	|
| csShareIcon                      | Integer(id)         | R.drawable.ic_share_status				| icon for share button in reader.		|
| csCloseIcon                      | Integer(id)         | R.drawable.ic_stories_close				| icon for close button in reader.		|
| csRefreshIcon                    | Integer(id)         | R.drawable.ic_refresh					| icon for refresh button in reader.	|
| csSoundIcon                      | Integer(id)         | R.drawable.ic_stories_status_sound		| icon for sound button in reader.		|
| csCoverQuality                   | Integer             | 0 	  | quality for stories list covers. If not set - sdk uses medium image quality (`QUALITY_MEDIUM = 1; QUALITY_HIGH = 2;`)|
| csTimerGradientEnable 		   | Boolean             | true   | Flag that is responsible for show dark gradient behind timer in reader  |


The example of set parameters:
```
appearanceManager
    .csListItemBorderColor(Color.RED)
    .csListItemMargin(0)
    .csClosePosition(AppearanceManager.BOTTOM_RIGHT)
    .csListItemTitleColor(Color.BLUE)
    .csListItemTitleSize(Sizes.dpToPxExt(20))
```

Also, there are several interfaces in the `AppearanceManager`.

#### IStoriesListItem

`IStoriesListItem csListItemInterface` - used for full customization of list items.

```
interface IStoriesListItem {
    View getView(); // here you need to pass View - the appearance of the cell
    View getVideoView(); // here you need to pass the View - the appearance of the cell in case the cells use the cover video
    void setTitle(View itemView, String title, Integer titleColor); // itemView is the current cell, in the required View we use the story header. The titleColor parameter can be null
    void setImage(View itemView, String url, int backgroundColor); // itemView - the current cell, in the required View show the story's cover or background color if it is absent
    void setOpened(View itemView, boolean isOpened); // itemView is the current cell, change it as needed if it is opened
    void setHasAudio(View itemView, boolean hasAudio); // itemView - the current cell, change it as needed if this story has audio inside
    void setVideo(View itemView, String videoUrl, String url, int backgroundColor); // itemView is the current cell, in the required View we show the video cover story (videoUrl), video poster (url) or background color if it is absent. To work with video cells, it is recommended to use a class from the VideoPlayer library as a container for displaying video and the loadVideo(String videoUrl) method to launch. This class provides for caching video covers. The VideoPlayer class inherits from TextureView
}
```
If this interface is specified, other parameters, affecting the appearance of the list cell, will be ignored.
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
            itemView.findViewById(R.id.image).setBackgroundColor(backgroundColor);
        }

        @Override
        public void setVideo(View itemView, String videoUrl, String url, int backgroundColor) {
            itemView.findViewById(R.id.image).setBackgroundColor(backgroundColor);
            ((VideoPlayer)itemView.findViewById(R.id.video)).loadVideo(videoUrl);
        }

        @Override
        public void setOpened(View itemView, boolean isOpened) {

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
    View getFavoriteItem(List<FavoriteImage> favoriteImages, int count);
    void bindFavoriteItem(View favCell, List<FavoriteImage> favoriteImages, int count);
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
        public View getFavoriteItem(List<FavoriteImage> favImages, int count) {
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_story_list_item_favorite, null, false);
            bindFavoriteItem(v, favImages, count);
            return v;
        }

        @Override
        public void bindFavoriteItem(View v, List<FavoriteImage> favImages, int count) {
            AppCompatTextView title = v.findViewById(R.id.title);
            title.setText("My favorites");
            RelativeLayout container = v.findViewById(R.id.container);
            container.removeAllViews();
            AppCompatImageView image1 = new AppCompatImageView(MainActivity.this);
            if (!favImages.isEmpty()) {
                image1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
                if (favImages.get(0).getImage() != null) {
                    ImageLoader.getInstance().displayImage(favImages.get(0).getImage().getUrl(), -1, image1);
                } else {
                    image1.setBackgroundColor(favImages.get(0).getBackgroundColor());
                }
                container.addView(image1);
            } else {
                container.setBackgroundColor(Color.RED);
            }
        }
    });
```

Also, to interact with the favorite cell (for example, to open a new window with a list of favorite stories), you need to add a handler:
```
storiesList.setOnFavoriteItemClick(new StoriesList.OnFavoriteItemClick() {
    @Override
    public void onClick() {
        doAction();
    }
});
```
#### ILoaderView

`ILoaderView iLoaderView` - used to substitute your own loader instead of the default loader
```
public interface ILoaderView {
    View getView();
}
```

This interface must be set for the global `AppearanceManager`.
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

#### StoryTouchListener

`StoryTouchListener csStoryTouchListener` - used to add processing for clicking on cells in story lists (for example, for animation)
```
public interface StoryTouchListener {
    void touchDown(View view, int position); // View - list cell, position - position in the list 
    void touchUp(View view, int position);
}
```

### Dimensions

Dimensions of the dialog box for displaying stories on a tablet
```
<dimen name="cs_tablet_width">400dp</dimen>
<dimen name="cs_tablet_height">600dp</dimen>
```

### Icons

The library uses 7 buttons with icons: sound, refresh, close, like, dislike, share and favorite. For refresh and close buttons, the files `ic_refresh.xml`, `ic_close.xml` are used. Icons can be set both in vector form (for devices with Android 5.0 and higher), and as png/webp files for basic resolutions (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi). 
The sound, like, dislike, share, and favorite buttons are defined by the `ic_stories_status_sound.xml`, `ic_stories_status_like.xml`, `ic_stories_status_dislike.xml`, `ic_share_status.xml` and `ic_stories_status_favorite.xml`. The sound, like, dislike and favorite layout is represented as a selector. State_activated (true / false) is used to display the status.

## SDK integration

### Events (1.3.x)

The library interacts using the event model, which is provided by the internal `CsEventBus` module (a shortcut version of the [EventBus](http://greenrobot.org/ru-eventbus/) library). You can subscribe your own objects to the events generated by the library.
Example:
```
@CsSubscribe(threadMode = CsThreadMode.MAIN)
public void onStoriesLoadedEvent(StoriesLoaded event) {
    Toast.makeText(getActivity(), ""+event.getCount(), Toast.LENGTH_SHORT).show();
}
 
@CsSubscribe
public void handleSomethingElse(SomeOtherEvent event) {
    doSomethingWith(event);
}
```
 
All event subscribers should register in accordance with their lifecycle and, if possible, disconnect from `CsEventBus`.
Example:
```
@Override
public void onStart() {
    super.onStart();
    CsEventBus.getDefault().register(this);
}
 
@Override
public void onStop() {
    CsEventBus.getDefault().unregister(this);
    super.onStop();
}
```

The method `post(Event event)`is used to send events to the SDK.
2 events can be sent to the SDK:
```
CloseStoryReaderEvent - used to close the story reader (for example, when you override a click on buttons, sharing, etc.)
SoundOnOffEvent - called after changing the sound on / off flag (InAppStoryManager.getInstance().SoundOn). If the reader is closed, there is no need to call the event.
```

Example:
```
CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.CUSTOM));
```

Below are the events that you can subscribe to:

1) `StoriesLoaded` - the list of stories has been loaded, the widget is ready to work (triggered every time when the list is loaded, including at refresh). The event contains the `int getCount()` method - the number of stories

All other events contain 4 methods:
```
int getId() - get id of stories
String getTitle() - get the title of stories
ArrayList<String> getTags() - get story tags
int getSlidesCount() - number of slides
``` 

2) `ClickOnStory` - click on stories in the list (both in the regular list and in the favorites). Additional method `int getSource()`, can return `ClickOnStory.LIST`, `ClickOnStory.FAVORITE` values.

3) `ShowStory` - showing the reader with stories (after click or swiping in the regular stories list, in the favorites stories list , single story or onboarding story). Additional method `int getSource()`, can return `ShowStory.SINGLE`, `ShowStory.ONBOARDING`, `ShowStory.LIST`, `ShowStory.FAVORITE` values.

The following events (4-13) contain the `int getIndex()` method - from which slide the event was triggered. 

4) `CloseStory` - closing stories. Additional methods: 
- `int getAction()`, can return the values `CloseStory.AUTO`, `CloseStory.CLICK`, `CloseStory.SWIPE`, `CloseStory.CUSTOM`
- `int getSource()`, can return the values `ShowStory.SINGLE,` `ShowStory.ONBOARDING`, `ShowStory.LIST`, `ShowStory.FAVORITE`

5) `CallToAction` - click on the button in the story or swipe up on the SwipeUp widget. Additional methods:
- `String getLink()`, returns a link embedded in a button
- `int getType()`, can return values `CallToAction.SWIPE` = 1, `CallToAction.BUTTON` = 0
This event replaces the `ClickOnButton` deprecated event .

6) `ShowSlide` - showing a slide.

7) `ClickOnShareStory` - click on the share button.

8) `StartGame` - click on the start game button

9) `CloseGame` - manual close game (by close button and etc).

10) `FinishGame` - at the end of the game (with automatic closing). It also contains a `getResult()` method that returns a json string with the result of the game. 

The next 3 events also contain the method `boolean getValue()` - what state the button is in (`true` - activated) 

11) `LikeStory` - click on the like button

12) `DislikeStory` - click on the dislike button

13) `FavoriteStory` - click on the button for adding stories to favorites

There are the following events for working with onboarding and single stories:

14) `OnboardingLoad` - sent when the onboarding list is loaded. Contains a `getCount` method that returns the number of onboarding stories and `isEmpty` - a flag whether an empty list was returned on request or not.

15) `OnboardingLoadError` - sent when loading the onboarding list in case of an error 

16) `SingleLoad` - sent when loading single story by id (by `InAppStoryManager.getInstance().showStory` method)

17) `SingleLoadError` - sent when loading single story by id in case of some error 

There are 2 events for tracking errors:

1) `StoriesErrorEvent` - occurs when some kind of error comes from the server. Contains 7 different types depending on the place of origin. Has a `getType` method for getting the type of error. The types of errors:
```
OPEN_SESSION = 0;
LOAD_LIST = 1;
LOAD_SINGLE = 2;
LOAD_ONBOARD = 3;
READER = 4;
EMPTY_LINK = 5;
CACHE = 6;
``` 

2)`NoConnectionEvent` - when trying to download without internet. Has a `getType` method to get the type of error.
    

### Work with sound

The `InAppStoryManager.getInstance().soundOn` flag is responsible for on/off sound playback in stories (`true` - sound is on, `false` – sound is off). The default value of the flag is written in the `constants.xml` file in the `defaultMuted` variable (by default `true` - the sound is off) and can be reloaded. Please note that the `soundOn` value is set as `!soundMuted` (it will be `false` by default). 
Also, the `InAppStoryManager.getInstance().soundOn` flag is public, so you can (for example, after initializing `InAppStoryManager`) set its value directly.
Example:
```
InAppStoryManager.getInstance().soundOn = true;
```

If the value changes while the reader is open, you have to send the `SoundOnOffEvent` event.
```
CsEventBus.getDefault().post(new SoundOnOffEvent());
```    

If the reader is closed, sending an event is not required.

### Onboarding stories and open sigle story

The library supports work with onboarding stories. 
The function for loading onboarding stories is follows:
```
InAppStoryManager.getInstance().showOnboardingStories(Context context, AppearanceManager manager);
InAppStoryManager.getInstance().showOnboardingStories(List<String> tags, Context context, AppearanceManager manager);
```

Functions are passed, context, display manager (used to determine the position of the close button and animation in the reader) and list of tags for second.
It may be necessary to perform some action in the application immediately after the onboarding stories is loaded (or if they could not appear on screen, since all of them were already displayed earlier or some kind of error occurred). In this case, you need to subscribe to the following `CsEventBus` events:

```
OnboardingLoad - sent when the onboarding list is uploaded.
OnboardingLoadError - sent when loading the onboarding list in case of an error. 
```    

In addition, it is possible to open one story by its id or slug.
```
InAppStoryManager.getInstance().showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback /*optional, may be null*/);

interface IShowStoryCallback {
    void onShow(); //Calls after loading data about story from server
    void onError(); //Calls if loading fails
}
```

Also in case of a successful / unsuccessful attempt to load stories, events are raised that the developer can subscribe to change the states of any external elements in the application:
```
SingleLoad - sent when loading a single story by id (by `InAppStoryManager.getInstance().showStory` method). 
SingleLoadError - sent when loading a single story by id in case of some error. 
```

The function allows you to load all stories, including those that are not in the stories list returned to the user. 

### Handlers

The handler for clicks on buttons in stories is set in the `InAppStoryManager` through the method:
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
If you need to close the reader when the handler is triggered, you need to add a call to the `CloseStoryReaderEvent` event in `onUrlClick`:
```
InAppStoryManager.getInstance().setUrlClickCallback(new InAppStoryManager.UrlClickCallback() {
    @Override
    public void onUrlClick(String link) {
        CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.CUSTOM));
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

You can also override the handler for clicking on the sharing button as follows:
```
InAppStoryManager.getInstance().shareCallback = new InAppStoryManager.ShareCallback() {
    @Override
    public void onShare(String url, String title, String description, String shareId) {
        doAction(url, title, description);
    }
};
```

You also can use callbacks instead of events. There are 2 types of callbacks - for InAppStoryManager and for StoriesList.
#### InAppStoryManager callbacks:
1) 
```
InAppStoryManager.getInstance().setShowStoryCallback(ShowStoryCallback showStoryCallback); 
//equivalent to 'ShowStory' event

public interface ShowStoryCallback {
        void showStory(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   SourceType source);
}
```

2) 
```
InAppStoryManager.getInstance().setCloseStoryCallback(CloseStoryCallback closeStoryCallback); 
//equivalent to 'CloseStory' event

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

3) 
```
InAppStoryManager.getInstance().setCallToActionCallback(CallToActionCallback callToActionCallback); 
//equivalent to 'CallToAction' event

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

4) 
```
InAppStoryManager.getInstance().setShowSlideCallback(ShowSlideCallback showSlideCallback); 
//equivalent to 'ShowSlide' event

public interface ShowSlideCallback {
        void showSlide(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   int index);
}
```

5) 
```
InAppStoryManager.getInstance().setClickOnShareStoryCallback(ClickOnShareStoryCallback clickOnShareStoryCallback); 
//equivalent to 'ClickOnShareStory' event

public interface ClickOnShareStoryCallback {
        void shareClick(int id,
                    String title,
                    String tags,
                    int slidesCount,
                    int index);
}
```

6) 
```
InAppStoryManager.getInstance().setLikeDislikeStoryCallback(LikeDislikeStoryCallback likeDislikeStoryCallback); 
//equivalent to 'LikeStory' and 'DislikeStory' event

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

7) 
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

8) 
```
InAppStoryManager.getInstance().setSingleLoadCallback(SingleLoadCallback singleLoadCallback) ; 
//equivalent to 'SingleLoad' event

    
public interface SingleLoadCallback {
        void singleLoad(String storyId);
}
```

9) 
```
InAppStoryManager.getInstance().setOnboardingLoadCallback(OnboardingLoadCallback onboardingLoadCallback); 
//equivalent to 'OnboardingLoad' event

public interface OnboardingLoadCallback {
        void onboardingLoad(int count);
}
```

10) 
```
InAppStoryManager.getInstance().setErrorCallback(ErrorCallback errorCallback); 
//equivalent to events that send different errors
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

11) 
```
InAppStoryManager.getInstance().setGameCallback(GameCallback gameCallback); 
//equivalent to 'StartGame', 'CloseGame' and 'FinishGame' events
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
Enums used in methods:
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

#### StoriesList callback:

```
storiesList.setCallback(ListCallback callback);
//equivalent to 'StoriesLoaded' and  'ClickOnStory' events
//can be set with custom implementation or with GameCallbackAdapter class

public interface ListCallback {
    void storiesLoaded(int size);
    void loadError();
    void itemClick(int id, 
                   int listIndex,
                   String title, 
                   String tags, 
                   int slidesCount, 
                   boolean isFavoriteList);
}
```

### Stories Goods Widget 

In stories you can add goods widget. It can be represented as horizontal list of items (default implementation with RecyclerView) or you can fully customize it.  
If you want to use widget you should set csCustomGoodsWidget interface in global `AppearanceManager`. 
Example:
```
public class GoodsItemData {
    public GoodsItemData(String sku, 
                         String title, 
                         String description, 
                         String image, 
                         String price, 
                         String oldPrice, 
                         Object raw);
}

public interface GetGoodsDataCallback {
    void onSuccess(ArrayList<GoodsItemData> data);
    void onError();
    void onClose(); //Use if you want to close goods widget.
}

globalAppearanceManager.csCustomGoodsWidget(new ICustomGoodsWidget() {
    @Override
    public View getWidgetView() {
        ...
    }

    @Override
    public ICustomGoodsItem getItem() {
        ...
    }

    @Override
    public IGoodsWidgetAppearance getWidgetAppearance() {
        ...
    }

    @Override
    public RecyclerView.ItemDecoration getDecoration() {
        ...
    }

    @Override
    public void getSkus(ArrayList<String> skus, GetGoodsDataCallback callback) {
        //In this method you should always call 
        //callback.onSuccess(ArrayList<GoodsItemData> data)
        //or callback.onError();
        ...
    }

    @Override
    public void onItemClick(GoodsItemData sku) {
        ...
        //This action does not close stories reader and game reader. 
        //If you want to close readers, you should call `InAppStoryManager.closeStoryReader()` for closing all readers and widget
    }
});
```
If you want use default implementation (RecyclerView) than method `getWidgetView()` should return null. In that case you override other methods like `getItem()`, `getWidgetAppearance()`, `getDecoration()`, `onItemClick()` as you need. Otherwise that methods won't be called and could return null values. 
Method `getItem()` returns next interface:
```
public interface ICustomGoodsItem {
    @NonNull
    View getView();

    void bindView(View view, GoodsItemData data);
}
```
Here is an example for this method:
```
@Override
public ICustomGoodsItem getItem() {
    return new ICustomGoodsItem() {
        @NonNull
        @Override
        public View getView() {
            return LayoutInflater.from(getContext()).inflate(R.layout.custom_goods_list_item,
                   null, false);
        }

        @Override
        public void bindView(View view, GoodsItemData goodsItemData) {
            ((TextView) view.findViewById(R.id.title)).setText(goodsItemData.title);
            loadImage((ImageView)view.findViewById(R.id.image), goodsItemData.image);
        }
    };
}
```
Also you can use method `getWidgetAppearance()` if you want customize other parts of widget (background line, close button). Also for simple usage you can override GoodsWidgetAppearanceAdapter() instead of interface. Method returns next interface:
```
public interface IGoodsWidgetAppearance {
    int getBackgroundHeight();
    int getBackgroundColor();
    int getDimColor();
    Drawable getCloseButtonImage();
    int getCloseButtonColor();
}
```
Here is an example for this method:
```
@Override
public ICustomGoodsItem get() {
    return new GoodsWidgetAppearanceAdapter() {
        @Override
        public int getBackgroundColor() {
            return Color.BLUE;
        }
    };
}
```
In method `getSkus()` you get ids for goods items. When you get data for this items from your application, you should create array of GoodsItemData items and call getGoodsDataCallback.onSuccess(ArrayList<GoodsItemData> data). Also in case of any error in retreiving data for items, you should call getGoodsDataCallback.onError().
Here is an example for this method:

@Override
public void getSkus(ArrayList<String> skus, GetGoodsDataCallback callback) {
    ArrayList<GoodsItemData> goodsItemData = new ArrayList<>();
    HashMap<String, CustomGoodsItem> goods = getGoodsItemsFromServer(skus)
    for (String sku : skus) {
        CustomGoodsItem item = goods.get(sku)
        GoodsItemData data = new GoodsItemData(sku, item.title, item.description, item.imageLink, item.price, item.oldPrice, item);
        //last variable can be used in case if you want to represent any additional fields in custom cell with `ICustomGoodsItem.bindView()`
        //or get your object in `onItemClick()`
        goodsItemData.add(data);
    }
    callback.onSuccess(goodsItemData);
}
```
If you want to fully customize your widget, you should override `getWidgetView()` to return NonNull view. In that case all binding logic should be in `getSkus()` method. For example:
```
globalAppearanceManager.csCustomGoodsWidget(new ICustomGoodsWidget() {
                RelativeLayout container;

                @Override
                public View getWidgetView() {
                    container =
                            (RelativeLayout) View.inflate(context,
                                    R.layout.custom_goods_widget, null);
                    return container;
                }

                @Override
                public ICustomGoodsItem getItem() {
                    return null;
                }

                @Override
                public RecyclerView.ItemDecoration getDecoration() {
                    return null;
                }

                @Override
                public void getSkus(ArrayList<String> skus, GetGoodsDataCallback getGoodsDataCallback) {
                    if (container != null && skus != null) {
                        getGoodsDataCallback.onSuccess(new ArrayList<>());
                        for (String sku : skus) {
                            TextView textView = new TextView(context);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            textView.setLayoutParams(lp);
                            textView.setText(sku);
                            textView.setOnClickListener(v1 -> {
                                Toast.makeText(context, textView.getText(), Toast.LENGTH_LONG).show();
                            });
                            ((LinearLayout) container.findViewById(R.id.container)).addView(textView);

                        }
                        container.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getGoodsDataCallback.onClose();
                            }
                        });
                    }
                }

                @Override
                public void onItemClick(GoodsItemData goodsItemData) {

                }
            });
```

### Home Screen Widget

When creating a widget, it is possible to add a list of stories. This will display the first 4 elements of the list.
To do this, you need to set the properties of the list using the method:
```
AppearanceManager.csWidgetAppearance(
    Context context, //context, it is best to pass the widget context, required parameter  
    Class widgetClass //widget class (WidgetName.class), required parameter 
    Integer itemCornerRadius //radius of corners of list cells, optional parameter 
)
```

The list is a `GridView`, so when marking up the widget, you need to add the corresponding element .
Example:
```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:gravity="center_horizontal"
    android:orientation="vertical">
    ...
    <GridView
        android:id="@+id/storiesGrid"
        android:layout_width="320dp"
        android:layout_height="90dp"
        android:layout_margin="8dp"
        android:horizontalSpacing="6dp"
        android:numColumns="4"
        android:verticalSpacing="6dp" />
    ...
</LinearLayout>
```

In the manifest file of the widget, you must set a filter for events:
```
<receiver
    android:name=".MyWidget"
    android:label="MyWidget">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
        <action android:name="ias_w.UPDATE_WIDGETS"/> //comes when there is a need to load the list from the server 
        <action android:name="ias_w.UPDATE_SUCCESS_WIDGETS"/> //comes in case of successful receipt of a non-empty list of stories from the server 
        <action android:name="ias_w.UPDATE_NO_CONNECTION"/> //comes in if when trying to get the list from the server failed to connect to the Internet 
        <action android:name="ias_w.UPDATE_EMPTY_WIDGETS"/> //comes in case of receiving an empty list of stories from the server 
        <action android:name="ias_w.UPDATE_AUTH"/> //comes in if the user is not authorized in the InAppStorySDK 
        <action android:name="ias_w.CLICK_ITEM"/> //comes when clicking on a list item of the story widget 
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/widget_metadata"/>                 
</receiver>
```

The corresponding event constants are defined as follows:
```
public static final String UPDATE = "ias_w.UPDATE_WIDGETS";
public static final String CLICK_ITEM = "ias_w.CLICK_ITEM";
public static final String POSITION = "item_position";
public static final String ID = "item_id";
public static final String UPDATE_SUCCESS = "ias_w.UPDATE_SUCCESS_WIDGETS";
public static final String UPDATE_EMPTY = "ias_w.UPDATE_EMPTY_WIDGETS";
public static final String UPDATE_NO_CONNECTION = "ias_w.UPDATE_NO_CONNECTION";
public static final String UPDATE_AUTH = "ias_w.UPDATE_AUTH";
```

In the `onReceive` method of the widget, you can subscribe to them.
Example:
```
@Override
public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equalsIgnoreCase(UPDATE_SUCCESS)) {
        createSuccessData(context);
    } else if (intent.getAction().equalsIgnoreCase(UPDATE)) {
        try {
            StoriesWidgetService.loadData(context);
        } catch (DataException e) {
            e.printStackTrace();
        }
    } else if (intent.getAction().equalsIgnoreCase(UPDATE_EMPTY)) {
        createEmptyWidget();
    } else if (intent.getAction().equalsIgnoreCase(UPDATE_AUTH)) {
        createAuthWidget();
    } else if (intent.getAction().equalsIgnoreCase(UPDATE_NO_CONNECTION)) {
        createNoConnectionWidget();
    } else if (intent.getAction().equalsIgnoreCase(CLICK_ITEM)) {
        int itemId = intent.getIntExtra(StoriesWidgetService.ID, -1);
        int itemPos = intent.getIntExtra(StoriesWidgetService.POSITION, -1);
        if (itemPos != -1) {
            Toast.makeText(context, "Clicked on item " + itemPos + ", id " + itemId, Toast.LENGTH_LONG).show();
        }
    }
    super.onReceive(context, intent);
}
```

Exmaple `createSuccessData()` function:
```
void createSuccessData(final Context context) {
    ComponentName thisAppWidget = new ComponentName(
            context.getPackageName(), getClass().getName());
    final AppWidgetManager appWidgetManager = AppWidgetManager
            .getInstance(context);
    final int appWidgetIds[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
    for (int i = 0; i < appWidgetIds.length; ++i) {

        Intent intent = new Intent(context, StoriesWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.cs_widget_stories_list);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        rv.setRemoteAdapter(appWidgetIds[i], R.id.storiesGrid, intent);
        setClick(rv, context, appWidgetIds[i]);
        appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.storiesGrid);
    }
}

void setClick(RemoteViews rv, Context context, int appWidgetId) {
    Intent listClickIntent = new Intent(context, MyWidget.class);
    listClickIntent.setAction(CLICK_ITEM);
    PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0, listClickIntent, 0);
    rv.setPendingIntentTemplate(R.id.storiesGrid, listClickPIntent);
}
```

The `StoriesWidgetService.loadData (Context context)` method is used directly to load the list. It can be called, for example, from the `onUpdate` or `onEnabled` method of the widget. 
Example:
```
@Override
public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                     int[] appWidgetIds) {
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            try {
                StoriesWidgetService.loadData(context);
            } catch (DataException e) {
                e.printStackTrace();
            }
        }
    }, 500);
    updateData(appWidgetManager, context, appWidgetIds);
    super.onUpdate(context, appWidgetManager, appWidgetIds);
}
```

By default, the cells of the list of the widget are square, 70x70. It is specified in the `cs_widget_grid_item.xml` file. To change, you need to reload this file, while maintaining the identifiers and type of the container, title, image elements. The container element sets the proportions of the cells, so the cell size must be determined in it.
Example:
```
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:gravity="center_horizontal"
    android:orientation="vertical">

    <RelativeLayout
        android:id="@+id/container"
        android:layout_width="70dp"
        android:clickable="true"
        android:layout_height="70dp">

        <ImageView
            android:id="@+id/image"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:clickable="false"
            android:gravity="center"
            android:scaleType="fitCenter" />
        <TextView
            android:id="@+id/title"
            android:layout_width="match_parent"
            android:maxWidth="55dp"
            android:clickable="false"
            android:padding="8dp"
            android:textSize="10sp"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:layout_alignParentBottom="true"
            android:maxLines="3"
            android:textColor="@color/white" />
    </RelativeLayout>
</FrameLayout>
```

### FAQ

#### 1) Cell reshaping: rectangle, circle 

In order to define a rectangular cell - in the `AppearanceManager` you can use `csListItemWidth (int width)`, `csListItemHeight (int height)`. If you need a round cell, you need to use customization via `csListItemInterface`. 

#### 2) Custom font 

To customize the font of the cell, use `csCustomFont(Typeface font)` in the `AppearanceManager`. There is no font customization in the story reader, the font for stories is automatically downloaded from the backend server.


#### 3) Changing the position of the timer / cross 

The `AppearanceManager` uses `csClosePosition`.

#### 4) Changing the loader in the story reader

The global `AppearanceManager` uses customization via `csLoaderView`.

#### 5) Defining the handler for buttons

Use the `InAppStoryManager.getInstance().setUrlClickCallback(InAppStoryManager.UrlClickCallback callback)` method. Also in the callback, it may be necessary to add the closure of the story reader via:
```
CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.CUSTOM)).
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
InAppStoryManager.getInstance().soundOn = true;
```
If the value changes while the reader is open, you must also send the `SoundOnOffEvent` event.
```
CsEventBus.getDefault().post(new SoundOnOffEvent());
``` 

### Samples
You can find more basic code samples in [this repository](https://github.com/inappstory/Android-Example)
