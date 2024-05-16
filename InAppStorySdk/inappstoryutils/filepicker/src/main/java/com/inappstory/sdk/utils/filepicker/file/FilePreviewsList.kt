package com.inappstory.sdk.utils.filepicker.file

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope

class FilePreviewsList @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    init {
        layoutManager = GridLayoutManager(
            context, 3,
            GridLayoutManager.VERTICAL,
            false
        )
    }
    var lifecycleScope: CoroutineScope? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifecycleScope = ViewTreeLifecycleOwner.get(this)?.lifecycleScope
    }

    fun load(
        hasFileAccess: Boolean,
        allowMultipleSelection: Boolean,
        mimeTypes: List<String>,
        clickCallback: FileClickCallback,
        cameraCallback: OpenCameraClickCallback,
        noAccessCallback: NoAccessCallback,
        galleryFileMaxCount: Int,
        translations: Map<String, String>,
        pickerFilter: PickerFilter
    ) {

        val adapter = FilePreviewsAdapter(
            context = context,
            coroutineScope = lifecycleScope,
            hasFileAccess = hasFileAccess,
            allowMultipleSelection = allowMultipleSelection,
            mimeTypes = mimeTypes,
            clickCallback = clickCallback,
            cameraCallback = cameraCallback,
            noAccessCallback = noAccessCallback,
            galleryFileMaxCount = galleryFileMaxCount,
            pickerFilter = pickerFilter,
            translations = translations
        )
        adapter.setHasStableIds(true)
        setAdapter(adapter)
    }
}