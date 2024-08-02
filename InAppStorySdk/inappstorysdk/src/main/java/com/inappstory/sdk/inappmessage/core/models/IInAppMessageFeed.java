package com.inappstory.sdk.inappmessage.core.models;

import java.util.List;

public interface IInAppMessageFeed<T> {
    List<T> messages();
}
