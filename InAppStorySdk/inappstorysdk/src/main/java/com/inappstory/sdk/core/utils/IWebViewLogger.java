package com.inappstory.sdk.core.utils;

import android.webkit.ConsoleMessage;

public interface IWebViewLogger {
    void logConsole(ConsoleMessage consoleMessage);
    void logMethod(String payload);
    void logJSCall(String payload);
}
