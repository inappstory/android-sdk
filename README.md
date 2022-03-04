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
	implementation 'com.github.inappstory:android-sdk:1.5.2'
```

Also for correct work in `dependencies` you need to add:
```
	implementation 'androidx.recyclerview:recyclerview:1.2.1'
	implementation 'androidx.webkit:webkit:1.4.0'
```

## ProGuard

If your project uses `ProGuard` obfuscation, add following rules to proguard config file:

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

## Getting started

### SDK Initialization

SDK can be initialized from any point with `Context` access (`Application`, `Activity`, `Fragment`, etc.)

```
	new InAppStoryManager.Builder()
	    	.apiKey(apiKey) //String
		.context(context) //Context
		.userId(userId) //String
	    	.create();
```
>**Attention!**  
>Method `create()` can generate `DataException` if SDK was not initialized. Strictly recommend to catch `DataException` for additional info.

Context and userId - is not optional parameters. Api key is a SDK authorization key. It can be set through `Builder` or in `values/constants.xml`
```
	<string name="csApiKey">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</string>
```

You can also specify another SDK settings for `InAppStoryManager.Builder`

After initialization you can use `InAppStoryManager` class via `InAppStoryManager.getInstance()`.

### Add StoriesList and load stories

`StoriesList` is extends RecyclerView class and can be added like any `View` class. For example - via xml

```
	<com.inappstory.sdk.stories.ui.list.StoriesList
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
	    android:id="@+id/stories_list"/>
```

After SDK initialization you can load stories in `StoriesList`

```
	storiesList.loadStories(); 
```
This method also can be used to reload list (for example in PtR case)

>**Attention!**  
>This method can generate DataException if SDK was not initialized. Strictly recommend to catch `DataException` for additional info.

For more information you can read [the full SDK guide](https://github.com/paperrose/InAppStorySdkKt/blob/master/docs/Index.md), [FAQ](https://github.com/paperrose/InAppStorySdkKt/blob/master/docs/FAQ.md) or check [Samples](https://github.com/inappstory/Android-Example).
