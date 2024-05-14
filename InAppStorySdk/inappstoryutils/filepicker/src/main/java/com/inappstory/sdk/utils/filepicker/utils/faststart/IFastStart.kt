package com.inappstory.sdk.utils.filepicker.utils.faststart

import java.io.IOException

interface IFastStart {
    @kotlin.jvm.Throws(IOException::class)
    fun fastStart() : Boolean

}