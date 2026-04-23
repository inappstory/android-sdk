package com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents;

import com.inappstory.sdk.refactoring.core.utils.observers.ISTEDataType;

public enum StoriesSTEDataType implements ISTEDataType {
    CALL_TO_ACTION,
    SLIDE_IN_CACHE,
    JS_SEND_API_REQUEST,
    JS_SEND_API_RESPONSE,
    OPEN_STORY,
    CLOSE_READER,
    OPEN_GAME,
    AUTO_SLIDE_END,
    FREEZE_UI,
    UNFREEZE_UI,
    RENDER_READY,
    LOAD_SLIDE,
    UPDATE_TIMELINE,
    START_SLIDE,
    RESTART_SLIDE,
    SET_SOUND_STATUS,
    PAUSE_SLIDE,
    RESUME_SLIDE,
    STOP_SLIDE
}
