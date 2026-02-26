package com.inappstory.sdk.refactoring.session.repositories;


import com.inappstory.sdk.refactoring.session.INewSessionSubscriber;
import com.inappstory.sdk.refactoring.session.ISessionParametersChangedSubscriber;
import com.inappstory.sdk.refactoring.session.data.local.SessionDTO;

public interface ISessionSubscribersHolder {
    void addSessionParametersSubscriber(ISessionParametersChangedSubscriber subscriber);
    void removeSessionParametersSubscriber(ISessionParametersChangedSubscriber subscriber);
    void addNewSessionSubscriber(INewSessionSubscriber subscriber);
    void removeNewSessionSubscriber(INewSessionSubscriber subscriber);
    void notifySessionParametersChanged();
    void notifyNewSession(SessionDTO sessionDTO);
}
