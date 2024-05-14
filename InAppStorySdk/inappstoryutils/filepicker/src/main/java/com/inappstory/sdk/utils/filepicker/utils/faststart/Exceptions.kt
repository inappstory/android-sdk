package com.inappstory.sdk.utils.filepicker.utils.faststart

open class QtFastStartException constructor(detailMessage: String)
    : Exception(detailMessage)

internal class MalformedFileException constructor(detailMessage: String) :
    QtFastStartException(detailMessage)

internal class UnsupportedFileException constructor(detailMessage: String) :
    QtFastStartException(detailMessage)