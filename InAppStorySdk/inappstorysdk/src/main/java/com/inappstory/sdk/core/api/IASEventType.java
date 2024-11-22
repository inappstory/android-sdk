package com.inappstory.sdk.core.api;

public interface IASEventType {
    IASEventTypeGroup eventGroup();

    enum COMMON implements IASEventType {
        CALL_TO_ACTION,
        SHARE,
        ERROR;

        @Override
        public IASEventTypeGroup eventGroup() {
            return IASEventTypeGroup.COMMON;
        }
    }

    enum STORY_ONBOARDING implements IASEventType {
        LOAD;

        @Override
        public IASEventTypeGroup eventGroup() {
            return IASEventTypeGroup.STORY_ONBOARDING;
        }
    }

    enum STORY_SINGLE implements IASEventType {
        LOAD;

        @Override
        public IASEventTypeGroup eventGroup() {
            return IASEventTypeGroup.STORY_SINGLE;
        }
    }


    enum STORY_READER implements IASEventType {
        CLICK_SHARE,
        SHOW_STORY,
        SHOW_SLIDE,
        LIKE_DISLIKE,
        FAVORITE,
        STORY_WIDGET,
        CLOSE_STORY;

        @Override
        public IASEventTypeGroup eventGroup() {
            return IASEventTypeGroup.STORY_READER;
        }
    }

    enum IN_APP_MESSAGE implements IASEventType {
        IN_APP_MESSAGE_LOAD;

        @Override
        public IASEventTypeGroup eventGroup() {
            return IASEventTypeGroup.IN_APP_MESSAGE;
        }
    }

    enum IN_APP_MESSAGE_READER implements IASEventType {
        SHOW_IN_APP_MESSAGE,
        IN_APP_MESSAGE_WIDGET,
        CLOSE_IN_APP_MESSAGE;

        @Override
        public IASEventTypeGroup eventGroup() {
            return IASEventTypeGroup.IN_APP_MESSAGE_READER;
        }
    }

    enum GAME_READER implements IASEventType {
        GAME_READER;

        @Override
        public IASEventTypeGroup eventGroup() {
            return IASEventTypeGroup.GAME_READER;
        }
    }
}
