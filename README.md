# InAppStory

A library for embedding stories into an application with customization.

## Contents

* [How to getting started](https://github.com/inappstory/android-sdk/blob/main/README.md#getting-started)
* [Migrations](https://github.com/inappstory/android-sdk/blob/main/docs/Migrations.md)
* [InAppStoryManager](https://github.com/inappstory/android-sdk/blob/main/docs/InAppStoryManager.md)
	* [Initialization](https://github.com/inappstory/android-sdk/blob/main/docs/InAppStoryManager.md#initialization)
	* [Methods](https://github.com/inappstory/android-sdk/blob/main/docs/InAppStoryManager.md#methods)
	* [Callbacks](https://github.com/inappstory/android-sdk/blob/main/docs/InAppStoryManager.md#callbacks)
* [StoriesList](https://github.com/inappstory/android-sdk/blob/main/docs/StoriesList.md)
	* [Initialization](https://github.com/inappstory/android-sdk/blob/main/docs/StoriesList.md#initialization)
	* [Methods](https://github.com/inappstory/android-sdk/blob/main/docs/StoriesList.md#methods)
	* [Customization](https://github.com/inappstory/android-sdk/blob/main/docs/StoriesList.md#customization)
	* [Callbacks](https://github.com/inappstory/android-sdk/blob/main/docs/StoriesList.md#callbacks)

* [AppearanceManager](https://github.com/inappstory/android-sdk/blob/main/docs/AppearanceManager.md)
* [Likes, Favorites, Share](https://github.com/inappstory/android-sdk/blob/main/docs/Likes_Favorites_Share.md)
* [Tags and placeholders](https://github.com/inappstory/android-sdk/blob/main/docs/Tags_Placeholders.md)
	* [Tags](https://github.com/inappstory/android-sdk/blob/main/docs/Tags_Placeholders.md#tags)
	* [Placeholders](https://github.com/inappstory/android-sdk/blob/main/docs/Tags_Placeholders.md#placeholders)
* [Work with sound](https://github.com/inappstory/android-sdk/blob/main/docs/Sound.md)
* [Onboardings](https://github.com/inappstory/android-sdk/blob/main/docs/Onboardings.md)
* [Single Stories](https://github.com/inappstory/android-sdk/blob/main/docs/Single_Stories.md)
* [Stories Reader Goods Widget](https://github.com/inappstory/android-sdk/blob/main/docs/Goods.md)
* [Home Screen Widget](https://github.com/inappstory/android-sdk/blob/main/docs/Home_Screen_Widget.md)
* [Events](https://github.com/inappstory/android-sdk/blob/main/docs/CsEventBus.md)
* [FAQ](https://github.com/inappstory/android-sdk/blob/main/docs/FAQ.md)
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
	implementation 'com.github.inappstory:android-sdk:1.6.0'
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
	    	.apiKey(apiKey) //String
		.context(context) //Context
		.userId(userId) //String
	    	.create();
```
>**Attention!**  
>Method `create()` can generate `DataException` if SDK was not initialized. Strictly recommend to catch `DataException` for additional info.

Context and userId - is not optional parameters. Api key is a SDK authorization key. It can be set through `Builder` or in `values/constants.xml`
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

For more information you can read [the full SDK guide](https://github.com/inappstory/android-sdk/blob/main/README.md#contents), [FAQ](https://github.com/inappstory/android-sdk/blob/main/docs/FAQ.md) or check [Samples](https://github.com/inappstory/Android-Example).
