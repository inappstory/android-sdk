# InAppStory

A library for embedding stories into an application with customization.

## How to get started

### Requirements

From version 1.17.0 minimum SDK version is 21 (Android 5.0).
If your application supports android 4.4 (minSDK 19) - you have to use older versions.

The library is intended for Phone and Tablet projects (not intended for Android TV or Android Wear applications).

### Adding to the project

Add jitpack maven repository to the root `build.gradle` in the `repositories` section:

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
 implementation 'com.github.inappstory:android-sdk:1.21.5'
```

Also for correct work in `dependencies` you need to add:

```gradle
 implementation 'androidx.recyclerview:recyclerview:1.2.1'
 implementation 'androidx.webkit:webkit:1.4.0'
```

### ProGuard

If your project uses `ProGuard` obfuscation, and IAS SDK with version up to 1.17.x, add following rules to proguard config file:

```gradle
-keepattributes *Annotation*

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}

-keep class com.inappstory.sdk.** {
 *;
}
```

Starting from 1.18.0 there is no need to change proguard config file, all rules will be uploaded from `consumer-rules.pro`.

### Basic initialization

SDK has to be initialized only in `Application` class through method `InAppStoryManager.initSdk(context: Context)`
Then, from any class (`Application`, `Activity`, `Fragment`, etc.) you need to call `InAppStoryManager.Builder(). ... .create()`

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

::: warning
Before 1.18.0 SDK it wasn't required to call `InAppStoryManager.initSdk(context: Context)`. SDK can be initialized through `InAppStoryManager.Builder(). ... .create()` from any point in the app, but but the context is required to be passed to the builder. It still preferable to initialize SDK in `Application` class if possible and set tags, placeholders and userId later.
:::

```kotlin
fun createInAppStoryManager(
    apiKey: String,
    context: Context,
    userId: String
): InAppStoryManager {
    return InAppStoryManager.Builder()
        .apiKey(apiKey)
        .context(context)
        .userId(userId)
        .create()
}
```

- `UserId` can't be longer than 255 characters. Can be set via InAppStoryManager or later with method `setUserId`.
- `ApiKey` is an SDK authorization key. It can be set through `Builder` or
  in `values/constants.xml`.

```xml

<string name="csApiKey">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</string>
```

- `Context` can be of any type (`Activity` or `Application`), but `Application` context is preferred.

You can also specify other SDK settings for `InAppStoryManager.Builder`.

After initialization you can use `InAppStoryManager` by storing `create()` result in any variable or via `InAppStoryManager.getInstance()`.

### Add StoriesList and load stories

`StoriesList` extends `RecyclerView` class and can be added like any other `View` class instance. For example - via xml:

```xml

<com.inappstory.sdk.stories.ui.list.StoriesList android:layout_width="match_parent"
    android:layout_height="wrap_content" android:id="@+id/stories_list" />
```

After SDK initialization you can load stories in `StoriesList`.

```kotlin
fun loadStories(storiesList: StoriesList) {
    storiesList.loadStories()
}
```

For more information you can read [full SDK guide](https://docs.inappstory.ru/sdk-guides/android/how-to-get-started.html) or check [Samples](https://github.com/inappstory/Android-Example).
