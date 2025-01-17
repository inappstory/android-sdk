# InAppStory

A library for embedding stories into an application with customization.

## Requirements

The minimum SDK version is 21 (Android 5.0).

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
implementation 'com.github.inappstory:android-sdk:1.20.7'
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

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
	public *;
}

-keep class com.inappstory.sdk.** {
	*;
}
```

## Getting started

### SDK Initialization

SDK has to be initialized only in Application class through method `InAppStoryManager.initSdk(context: Context)`.
Then, from any class (Application, Activity, Fragment, etc.) you need to call `InAppStoryManager.Builder(). ... .create()`:

```kotlin
fun initInAppStorySdk(context: Context) { //Have to call from Application class and pass application context
    InAppStoryManager.initSdk(context)
}

fun createInAppStoryManager(
    apiKey: String,
    userId: String
): InAppStoryManager {
    return InAppStoryManager.Builder()
        .apiKey(apiKey)
        .userId(userId)
        .create()
}
```

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

```kotlin
storiesList.loadStories(); 
```
This method also can be used to reload list (for example in PtR case)

For more information you can read [full SDK guide](https://docs.inappstory.ru/sdk-guides/android/how-to-get-started.html) or check [Samples](https://github.com/inappstory/Android-Example).
