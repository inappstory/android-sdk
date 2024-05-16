package com.inappstory.sdk.utils.filepicker.file

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getLongOrNull

data class PickerFilter(val imageSize: Long, val videoSize: Long, val duration: Long)

data class UriAndType(val uri: Uri, val type: String)

abstract class FilePickerAPI {
    abstract fun openCamera(context: Context)

    data class FileData(
        val name: String,
        val duration: Long? = null,
        val date: Long,
        val type: String,
        val unavailableByDuration: Boolean,
        val unavailableBySize: Boolean,
    )

    abstract fun getImagesPath(
        context: Context,
        pickerFilter: PickerFilter,
        mimeTypes: List<String>
    ): List<FileData>

    protected fun getImagesPath(
        context: Context,
        uri: List<UriAndType>,
        pickerFilter: PickerFilter,
        mimeTypes: List<String>
    ): List<FileData> {
        val listOfAllImages = ArrayList<FileData>()
        val durationColumn =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.MediaColumns.DURATION
            } else {
                MediaStore.Video.VideoColumns.DURATION
            }
        val imageProjection = arrayOf(
            MediaStore.MediaColumns.TITLE,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED
        )
        val videoProjection = arrayOf(
            MediaStore.MediaColumns.TITLE,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            durationColumn,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED
        )
        uri.forEach {
            val mergeCursor = context.contentResolver.query(
                it.uri,
                if (it.type == "video") videoProjection else imageProjection,
                null,
                null,
                null
            )

            val fileFilterSize =
                if (it.type == "video") pickerFilter.videoSize else pickerFilter.imageSize
            val columnIndexDate: Int =
                mergeCursor!!.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)
            val columnIndexSize: Int =
                mergeCursor.getColumnIndex(MediaStore.MediaColumns.SIZE)

            val columnIndexData: Int =
                mergeCursor.getColumnIndex(MediaStore.MediaColumns.DATA)
            val columnIndexDuration: Int = mergeCursor.getColumnIndex(durationColumn)
            val columnIndexMT: Int =
                mergeCursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
            while (mergeCursor.moveToNext()) {
                if (columnIndexMT == -1 || columnIndexData == -1 || columnIndexDate == -1) continue
                if (mimeTypes.contains(mergeCursor.getString(columnIndexMT))) {
                    val duration = mergeCursor.getLongOrNull(columnIndexDuration)
                    val size = mergeCursor.getLongOrNull(columnIndexSize)
                    listOfAllImages.add(
                        FileData(
                            name = mergeCursor.getString(columnIndexData),
                            duration = duration,
                            date = mergeCursor.getLong(columnIndexDate),
                            unavailableByDuration = (pickerFilter.duration < (duration ?: 0L)),
                            unavailableBySize = (fileFilterSize < (size ?: 0L)),
                            type = it.type
                        )
                    )
                }
            }
            mergeCursor.close()
        }
        return listOfAllImages.sortedByDescending { it.date }
    }
}