package com.inappstory.sdk.utils.filepicker.file

import android.content.Context
import android.provider.MediaStore

internal class PhotoVideoPickerAPI : FilePickerAPI() {
    override fun openCamera(context: Context) {}

    override fun getImagesPath(
        context: Context,
        pickerFilter: PickerFilter,
        mimeTypes: List<String>
    ): List<FileData> {
        return getImagesPath(
            context = context,
            uri = listOf(
                UriAndType(uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video"),
                UriAndType(uri = MediaStore.Video.Media.INTERNAL_CONTENT_URI, "video"),
                UriAndType(uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "photo"),
                UriAndType(uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI, "photo"),
            ),
            pickerFilter = pickerFilter,
            mimeTypes = mimeTypes
        )
    }
}