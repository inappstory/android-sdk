Change Log
========== 

Version *1.20.10 (960)*
----------------------------
* Fixed: bug with game preloading

Version *1.20.9 (959)*
----------------------------
* Added: game js exception logs
* Fixed: LeakedCloseableViolation
* Fixed: negative offset for timelines
* Fixed: tablet outside click
* Fixed: vibration bug

Version *1.20.8 (958)*
----------------------------
* Added: user sign feature

Version *1.20.7 (957)*
----------------------------
* Fixed: NPE in setUserId
* Fixed: NPE in external API
* Fixed: Disable overscroll for WebViews (slider widget bug)

Version *1.20.6 (956)*
----------------------------
* Fixed: change game load status (if game loading wasn't successful)
* Fixed: check if game fragment is attached before game rendering

Version *1.20.5 (955)*
----------------------------
* Updated: check for ids (single stories, onbording feeds and games)
* Updated: resumeTimers for webViews
* Updated: default radius for story reader 
* Fixed: csReaderBackgroundColor option usage on tablets

Version *1.20.4 (954)*
----------------------------
* Added: external app version
* Added: check if webView is installed
* Updated: mark csListItemTitleColor as deprecated (will be removed in 1.21.x)
* Updated: pass session local parameters to requests
* Updated: encode user id in requests
* Fixed: bundle resources downloads (change keys)
* Fixed: video options header (emulator bug)
* Fixed: sync around ScheduleThreadPoolExecutors
* Fixed: closeOnSwipe option usage
* Fixed: story opening in fragments (container visibility)
* Fixed: game opening in fragments (from story)
* Fixed: recreate session on setUserId if deviceId is disabled

Version *1.20.3 (953)*
----------------------------
* Added: timeline color and visibility management from console
* Update: changed internal logic in slide model

Version *1.20.2 (952)*
----------------------------
* Added: open a game from another game
* Fixed: bundle resources downloads
* Fixed: feed refresh doesn't cause to clearing full stories models
* Fixed: wrong thread forceFinish
* Fixed: changePriority Story NPE
  
Version *1.19.8 (918)*
----------------------------
* Fixed: bundle resources downloads
* Fixed: feed refresh doesn't cause to clearing full stories models
* Fixed: wrong thread forceFinish
* Fixed: changePriority Story NPE

Version *1.20.1 (951)*
----------------------------
* Updated: changed ICustomGoodsWidget's getSkus and onItemClick signature (extended with widgetView parameter)
* Updated: changed ShareCallback's onBackPress signature (extended with widgetView parameter)
* Updated: added onDestroyView in ShareCallback

Version *1.19.7 (917)*
----------------------------
* Fixed: partial fix memory leaks (share callbacks)
* Added: close story reader from JS (SDK only)

Version *1.19.6 (916)*
----------------------------
* Fixed: dialog proportions and animation for tablets
* Fixed: memory leak for tablets (inner classes)
* Fixed: timeline blink

Version *1.19.4 (914)*
----------------------------
* Fixed: changed story pause logic (added local pauses)
* Fixed: prevent clicks and click handlers in slide-change time
* Fixed: tablets memory leaks
* Fixed: InAppStoryManager and InAppStoryService NPEs (Added initialized callbacks)

Version *1.19.3 (913)*
----------------------------
* Fixed: remove long synchronization
* Fixed: additional extras check in StoriesActivity
* Fixed: unsubscribe ReaderPageFragment to prevent memory leaks
* Fixed: clear unused items in favorite cell
  
Version *1.19.2 (912)*
----------------------------
* Updated: forbid to open new game reader if another was opened
* Updated: userId change with synchronization
* Updated: default story cell text break strategy
* Fixed: sharing bugs
* Fixed: check InAppStoryManager initialization status
* Fixed: goods widget for tablets
* Fixed: memory leak from timeline
* Fixed: statistic for story navigation
* Fixed: memory leak from timeline
* Fixed: UGC cell ratio
  
Version *1.19.1 (911)*
----------------------------
* Added: logs for utils repository
* Fixed: sharing bugs
* Fixed: additional check utils repository
* Fixed: unfreezeUI
* Fixed: move reader page creation
* Fixed: additional check utils repository
* Updated: extends consumer-rules.pro

Version *1.19.0 (910)*
----------------------------
* check story id and index in statistic click events
* Fixed: timeline visibility fixed
* Fixed: preload games request (exclude session id, add hasFeatureWebp)
* Fixed: fix StoriesAdapter.getIndexById (if UGC cell used)

Version *1.18.6 (866)*
----------------------------
* Fixed: check story id and slide index in statistic click events
* Fixed: offset for stories reader timeline in case of background pause
* Fixed: durations restore in case of background pause
* Fixed: StoriesAdapter.getIndexById (if UGC cell used)
* Fixed: check stories list cell before triggers cell interface (csListItemInterface, etc.) methods
  
Version *1.19.0-rc1 (900)*
----------------------------
* New: games preloading after session opening and inAppStoryManager.preloadGames();
* New: lottie animations in games feature support;
* New: bundle resources preloading for stories;
* New: VOD feature support;
* New: test-key usage in single story requests
* Update: changed bar colors for game reader (depends of opening spot);
* Update: changed stories timers logic (depends of JS code now);
* Update: new stack feed logic;
* Update: changed offsets (top and bottom) for stories
* Fixed: disableClose usage in stories loader fragment;
* Fixed: added screenshot sharing callback in case of any error;

Version *1.18.5 (865)*
----------------------------
* Update: launch and closing animation of dialogs in stories
  
Version *1.18.4 (864)*
----------------------------
* New: added external API (RN)
* Fixed: NPE in StoriesActivity
* Fixed: game progress loader width

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
