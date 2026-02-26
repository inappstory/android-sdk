package com.inappstory.sdk.refactoring.session;

import com.inappstory.sdk.refactoring.session.data.local.SessionDTO;

public interface INewSessionSubscriber {
    void onNewSession(SessionDTO sessionDTO);
}
