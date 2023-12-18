Change Log
==========

Version *1.17.7 (807)*
----------------------------

* Fixed: memory usage optimization
* Fixed: crash in game reader (insets for tablets)
* Fixed: set orientation for old games
* Fixed: crash in game reader (onDestroy calls after returning from background)

Version *1.16.11 (761)*
----------------------------

* Fixed: crash in game reader (insets for tablets)

Version *1.16.10 (760)*
----------------------------

* Fixed: crash in game reader (onDestroy calls after returning from background)

Version *1.17.6 (806)*
----------------------------

* Fixed: link caching (use QS)
* Fixed: crash with durations array
* Fixed: crash with null values in stories cache
* Fixed: images decompression

  
Version *1.16.9 (759)*
----------------------------

* Fixed: link caching (use QS)
* Fixed: crash with durations array
* Fixed: crash with null values in stories cache
* Fixed: images decompression

Version *1.17.5 (805)*
----------------------------

* Fixed: bug with StoriesReader navigation (to outside story)
* Fixed: bug with GameActivity (check InAppStoryService before usage)
* Fixed: crash from StoryTimelineManager (get duration from empty array)


Version *1.16.8 (758)*
----------------------------

* Fixed: bug with StoriesReader navigation (to outside story)
* Fixed: bug with GameActivity (check InAppStoryService before usage)
* Fixed: crash from StoryTimelineManager (get duration from empty array)


Version *1.17.4 (804)*
----------------------------

* Fixed: crash from StoryDownloadManager (cached story checking)


Version *1.16.7 (757)*
----------------------------

* Fixed: crash from StoryDownloadManager (cached story checking)


Version *1.17.3 (803)*
----------------------------

* Fixed: bug with favorites previews - check images if it was lost (removed/renamed)


Version *1.16.6 (756)*
----------------------------

* Fixed: bug with favorites previews - check images if it was lost (removed/renamed)


Version *1.17.2 (802)*
----------------------------

* New: Added support for widget "layers" in stories.
  In previous versions timers won't reset when you switch between layers.

* New: Added demo mode for games in private API
