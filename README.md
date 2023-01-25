# InAppStory

A library for embedding stories into an application with customization.

## Contents

* [How to getting started](README.md#getting-started)
* [Migrations](docs/Migrations.md)
* [InAppStoryManager](docs/InAppStoryManager.md)
	* [Initialization](docs/InAppStoryManager.md#initialization)
	* [Methods](docs/InAppStoryManager.md#methods)
	* [Callbacks](docs/InAppStoryManager.md#callbacks)
* [StoriesList](docs/StoriesList.md)
	* [Initialization](docs/StoriesList.md#initialization)
	* [Methods](docs/StoriesList.md#methods)
	* [Customization](docs/StoriesList.md#customization)
	* [Callbacks](docs/StoriesList.md#callbacks)
* [AppearanceManager](docs/AppearanceManager.md)
* [Likes, Favorites, Share](docs/Likes_Favorites_Share.md)
* [Tags and placeholders](docs/Tags_Placeholders.md)
	* [Tags](docs/Tags_Placeholders.md#tags)
	* [Placeholders](docs/Tags_Placeholders.md#placeholders)
	* [Image placeholders](docs/Tags_Placeholders.md#image-placeholders)
* [Work with sound](docs/Sound.md)
* [Onboardings](docs/Onboardings.md)
* [Single Stories](docs/Single_Stories.md)
* [UgcStoriesList](docs/UgcStoriesList.md)
	* [Initialization](docs/UgcStoriesList.md#initialization)
	* [Methods](docs/UgcStoriesList.md#methods)
	* [Customization and Callbacks](docs/UgcStoriesList.md#customization-and-callbacks)
* [Stories Reader Goods Widget](docs/Goods.md)
* [Home Screen Widget](docs/Home_Screen_Widget.md)
* [Stories Widget Events](docs/Stories_Widgets_Events.md)
* [FAQ](docs/FAQ.md)
* [Samples](https://github.com/inappstory/Android-Example)

## Requirements

The minimum SDK version is 19 (Android 4.4).

The library is intended for Phone and Tablet projects (not intended for Android TV or Android Wear applications).

## Adding to the project

Add jitpack maven repo to the root `build.gradle` in the `repositories` section :
```gradle
	allprojects {
	    repositories {
	        ...
	        maven { url 'https://jitpack.io' }
	    }
	}
```

In the project `build.gradle` (app level) in the `dependencies` section add:
```gradle
	implementation 'com.github.inappstory:android-sdk:1.12.1'
```

Also for correct work in `dependencies` you need to add:
```gradle
	implementation 'androidx.recyclerview:recyclerview:1.2.1'
	implementation 'androidx.webkit:webkit:1.4.0'
```

## ProGuard

If your project uses `ProGuard` obfuscation, add following rules to proguard config file:

```gradle
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

## Getting started

### SDK Initialization

SDK can be initialized from any point with `Context` access (`Application`, `Activity`, `Fragment`, etc.)

```js
	new InAppStoryManager.Builder()
     		.apiKey(apiKey) //Non-null String
      		.context(context) //Context
      		.userId(userId) //Non-null String
	    	.create();
```
>**Attention!**  
>Methods create() and userId() can generate DataException if SDK was not initialized. Strictly recommend to catch DataException for additional info.

**Context and userId - is not optional parameters. UserId can't be longer than 255 characters.** Api key is a SDK authorization key. It can be set through `Builder` or in `values/constants.xml`
```xml
	<string name="csApiKey">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</string>
```

You can also specify another SDK settings for `InAppStoryManager.Builder`

After initialization you can use `InAppStoryManager` class via `InAppStoryManager.getInstance()`.

### Add StoriesList and load stories

`StoriesList` is extends RecyclerView class and can be added like any `View` class. For example - via xml

```xml
	<com.inappstory.sdk.stories.ui.list.StoriesList
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
	    android:id="@+id/stories_list"/>
```

After SDK initialization you can load stories in `StoriesList`

```js
	storiesList.loadStories(); 
```
This method also can be used to reload list (for example in PtR case)

>**Attention!**  
>This method can generate DataException if SDK was not initialized. Strictly recommend to catch `DataException` for additional info.

For more information you can read [the full SDK guide](README.md#contents), [FAQ](docs/FAQ.md) or check [Samples](https://github.com/inappstory/Android-Example).
