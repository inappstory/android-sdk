package com.inappstory.sdk.utils.filepicker.file

import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class FilePreviewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var isActive = false
    @JvmField
    var path: String? = null
}