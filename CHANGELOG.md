Change Log
==========

Version *1.17.16 (816)*
----------------------------

* New: added InAppStoryManager.Builder.isDeviceIDEnabled setting
* New: added InAppStoryManager.Builder.gameDemoMode setting

Version *1.17.15 (815)*
----------------------------

* New: added InAppStoryManager.closeStoryReader() with force closing feature
* Fixed: change resources cache keys (sha1 and url dependency). 

Version *1.17.14 (814)*
----------------------------

* Update: get dialog input parameters from server
* Update: minimum font size for WebViews

Version *1.17.13 (813)*
----------------------------

* New: added InAppStoryManager.isStoryReaderOpened() and InAppStoryManager.isGameReaderOpened()
* Fixed: statistic for game opening (from list item)
* Fixed: game loading after refresh clicking
  
Version *1.17.12 (812)*
----------------------------

* Fixed: change finishGame and closeGame callbacks logic

Version *1.17.11 (811)*
----------------------------

* Fixed: NPEs for InAppStoryService
  
Version *1.17.10 (810)*
----------------------------

* Fixed: reopen session if UserId was changed in session opening time

Version *1.16.12 (762)*
----------------------------

* Fixed: reopen session if UserId was changed in session opening time

Version *1.17.9 (809)*
----------------------------

* Fixed: screen orientation change in game reader for Android 8.0

Version *1.17.8 (808)*
----------------------------

* Fixed: memory usage optimization
* Fixed: crash in game reader (insets for tablets)
* Fixed: set orientation for old games
* Fixed: crash in game reader (onDestroy calls after returning from background)

Version *1.17.7 (807)*
----------------------------
* Version can't be used because of critical bugs.

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

* New: added support for widget "layers" in stories.
  In previous versions timers won't reset when you switch between layers.

* New: added demo mode for games in private API
