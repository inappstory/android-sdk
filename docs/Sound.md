
## Work with sound

The method `InAppStoryManager.getInstance().soundOn(boolean isSoundOn)` flag is responsible for on/off sound playback in stories (`true` - sound is on, `false` – sound is off). 
```
  InAppStoryManager.getInstance().soundOn(true);
``` 

By default after `InAppStoryManager` initialization sound is turned off. It can be changed in the `constants.xml`
```
  <bool name="defaultMuted">true</bool>
```

