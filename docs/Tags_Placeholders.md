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