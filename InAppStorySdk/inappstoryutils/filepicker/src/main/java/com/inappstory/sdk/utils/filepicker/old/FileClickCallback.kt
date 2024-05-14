package com.inappstory.sdk.utils.filepicker.old

interface FileClickCallback {
    fun select(file: SelectedFile)

    fun unselect(file: SelectedFile)
}