## Tags and Placeholders

### Tags
All tags can be set in `InAppStoryManager` initialization process through `InAppStoryManager.Builder()`

```java
  new InAppStoryManager.Builder()
      .apiKey(apiKey) //String
      .context(context) //Context
      .userId(userId) //String
      .tags(tags) //ArrayList<String>
      .create();
```

Besides that tags can be changed in runtime after `InAppStoryManager` initialization. 

```js
InAppStoryManager.getInstance().setTags(ArrayList<String> tags)     // Set new or completely replace current tags list

InAppStoryManager.getInstance().addTags(ArrayList<String> tags)     // Add tags to current tags list

InAppStoryManager.getInstance().removeTags(ArrayList<String> tags)     // Remove passed tags from current tags list
```

In that case you may need to reload `StoriesList` through `storiesList.loadStories()` if data was already loaded before.

```js
InAppStoryManager.getInstance().setTags(<new_tags_ArrayList>) 
storiesList.loadStories()
```

### Placeholders
Like tags - all placeholders can be set in `InAppStoryManager` initialization

```java
  new InAppStoryManager.Builder()
      .apiKey(apiKey) //String
      .context(context) //Context
      .userId(userId) //String
      .placeholders(placeholders) //Map<String, String>
      .create();
```

or can be changed in runtime after `InAppStoryManager` initialization

```js
InAppStoryManager.getInstance().setPlaceholders(Map<String, String> placeholders)     // Set new or completely replace current placeholders list

InAppStoryManager.getInstance().setPlaceholder(String key, String value)     // Set placeholder to the current placeholders list. If you pass null, then the placeholder will be removed
```

In that case you may need to reload `StoriesList` through `storiesList.loadStories()` if data was already loaded before.

```js
InAppStoryManager.getInstance().setPlaceholders(<new_placeholders_Map>) 
storiesList.loadStories()
```

### Image placeholders

This feature was added from 1.10.0 

Image placeholders can be set in `InAppStoryManager` initialization

```java
  new InAppStoryManager.Builder()
      .apiKey(apiKey) //String
      .context(context) //Context
      .userId(userId) //String
      .imagePlaceholders(imagePlaceholders) //Map<String, ImagePlaceholderValue>
      .create();
```

or can be changed in runtime after `InAppStoryManager` initialization

```js
InAppStoryManager.getInstance().setImagePlaceholders(@NonNull Map<String, ImagePlaceholderValue> placeholders)  // Set new or completely replace current image placeholders list

InAppStoryManager.getInstance().setImagePlaceholder(@NonNull String key, ImagePlaceholderValue value)  // Set placeholder to the current placeholders list. If you pass null, then the placeholder will be removed
```

Here `ImagePlaceholderValue` is a class with private constructor and it's instance can be created through method `createByUrl(String url)`. Here `url` is a remote link with http(s) scheme to image 

```js
ImagePlaceholderValue placeholder = ImagePlaceholderValue.createByUrl("url string"). 
```

Image placeholders are used only in Stories reader, so you don't need to reload list after new changes.