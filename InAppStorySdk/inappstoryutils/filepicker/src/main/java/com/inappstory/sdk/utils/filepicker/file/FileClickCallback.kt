package com.inappstory.sdk.utils.filepicker.file

interface FileClickCallback {
    fun select(file: SelectedFile)

    fun unselect(file: SelectedFile)
}