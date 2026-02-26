package com.inappstory.sdk.refactoring.session.repositories;


import com.inappstory.sdk.refactoring.session.INewSessionSubscriber;
import com.inappstory.sdk.refactoring.session.ISessionParametersChangedSubscriber;
import com.inappstory.sdk.refactoring.session.data.local.SessionDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SessionSubscribersHolder implements ISessionSubscribersHolder {

    Set<ISessionParametersChangedSubscriber> parametersSubscribers = new HashSet<>();
    Set<INewSessionSubscriber> newSessionSubscribers = new HashSet<>();
    private final Object subLock = new Object();

    @Override
    public void addSessionParametersSubscriber(ISessionParametersChangedSubscriber subscriber) {
        synchronized (subLock) {
            parametersSubscribers.add(subscriber);
        }
    }

    @Override
    public void removeSessionParametersSubscriber(ISessionParametersChangedSubscriber subscriber) {
        synchronized (subLock) {
            parametersSubscribers.remove(subscriber);
        }
    }

    @Override
    public void addNewSessionSubscriber(INewSessionSubscriber subscriber) {
        synchronized (subLock) {
            newSessionSubscribers.add(subscriber);
        }
    }

    @Override
    public void removeNewSessionSubscriber(INewSessionSubscriber subscriber) {
        synchronized (subLock) {
            newSessionSubscribers.add(subscriber);
        }
    }

    @Override
    public void notifySessionParametersChanged() {
        List<ISessionParametersChangedSubscriber> localSubscribers;
        synchronized (subLock) {
            localSubscribers = new ArrayList<>(parametersSubscribers);
        }
        for (ISessionParametersChangedSubscriber subscriber : localSubscribers) {
            subscriber.sessionParametersChanged();
        }
    }

    @Override
    public void notifyNewSession(SessionDTO sessionDTO) {
        List<INewSessionSubscriber> localSubscribers;
        synchronized (subLock) {
            localSubscribers = new ArrayList<>(newSessionSubscribers);
        }
        for (INewSessionSubscriber subscriber : localSubscribers) {
            subscriber.onNewSession(sessionDTO);
        }
    }
}
