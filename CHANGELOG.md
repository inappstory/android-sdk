Change Log
==========

Version *1.18.3 (863)*
----------------------------
* New: added unfreezeUI JS method
* Update: change default nav bar color to black
* Fixed: tablet stories sizes

Version *1.18.2 (862)*
----------------------------

* New: added file picker support;
* New: added setLang(locale: Locale) and Builder's lang(locale: Locale) option in InAppStoryManager
* Removed: runtime download from webViewClient
* Fixed: bug in isDeviceIdEnabled usage
* Fixed: bug in stackFeed and common feed sync opened statuses
* Fixed: pause stories reader when goods widget is opened in tablet version
* Fixed: disable drag in tablets
* Fixed: NPE in ReaderManager.newStoryTask
* Fixed: ClassCastException in DefaultOpenStoriesReader.onOpen

Version *1.18.1 (861)*
----------------------------

* Update: game loader with infinite progress (changed behaviour)
* Fixed: ConcurrentModificationException if session opens with error
* Fixed: OOB for favorite cell
* Fixed: first slide reopening

Version *1.17.17 (817)*
----------------------------

* Fixed: ConcurrentModificationException if session opens with error
* Fixed: OOB for favorite cell

Version *1.18.0 (860)*
----------------------------

* New: new story and game reader display option. Now they can be shown in fragments instead of activities.
* New: interface IStoriesListItemWithStoryData. It can be used instead of IStoriesListItem
* New: letter limit in model
* New: InAppStoryManager.closeStoryReader(forceClose: Boolean, forceCloseCallback: ForceCloseReaderCallback).
* New: isDeviceIDEnabled and gameDemoMode options in InAppStoryManager.Builder.
* New: InAppStoryManager.isStoryReaderOpened() and InAppStoryManager.isGameReaderOpened().
* New: stack feed feature.
* New: method showStoryOnce() to InAppStoryManager.
* Update: InAppStoryManager initialization has changed. Now it has to be initialized only in Application class through method InAppStoryManager.initSdk(context: Context). Then, from any class (Application, Activity, Fragment, etc.) you need to call InAppStoryManager.Builder(). ... .create() (without passing context)
* Update: change stackFeed logic
* Update: signature of ListCallback's methods storiesLoaded and storiesUpdated. Now they have third argument data: List<StoryData>?
* Update: GameReaderCallback interface. It was extended with method gameOpenError(data: GameStoryData?, gameId: String?)
* Update: Contextless methods from Sizes class marked as deprecated and will be removed in next version. If you used them to conv ert sizes from dp to px, you can replace them to methods with context (f.e.: Sizes.dpToPxExt(sizeInDp: Int) -> Sizes.dpToPxExt(sizeInDp: Int, context: Context))
* Fixed: setMinimumFontSize in WebViews
* Fixed: change resources cache keys (sha1 and url dependency)


Version *1.18.0-rc5 (855)*
----------------------------

* New: eventGame, gameLoadFailed in JS
* New: reloadGameReader in JS
* New: closeStoryReader with forceClose option
* New: showStoryOnce
* New: isDeviceIDEnabled and public gameDemoMode settings
* Update: change escape string algorithm to more optimal
* Update: change stackFeed logic
* Fixed: setMinimumFontSize in WebViews
* Fixed: change resources cache keys (sha1 and url dependency)

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
