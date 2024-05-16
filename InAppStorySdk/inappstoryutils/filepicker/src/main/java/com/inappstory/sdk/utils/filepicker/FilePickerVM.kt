package com.inappstory.sdk.utils.filepicker

import androidx.fragment.app.FragmentManager
import com.inappstory.sdk.modulesconnector.utils.filepicker.OnFilesChooseCallback

object FilePickerVM {
    var filesChooseCallback: OnFilesChooseCallback? = null
    var parentFragmentManager: FragmentManager? = null
    var filePickerSettings: FilePickerSettings? = null

    fun close() {
        parentFragmentManager?.popBackStack()
    }

    fun cancel() {
        parentFragmentManager?.popBackStack()
        filesChooseCallback?.onCancel(
            filePickerSettings?.cb,
            filePickerSettings?.id
        )
        filesChooseCallback = null
    }

    fun closeWithError(error: String) {
        parentFragmentManager?.popBackStack()
        filesChooseCallback?.onError(
            filePickerSettings?.cb,
            filePickerSettings?.id,
            error
        )
        filesChooseCallback = null
    }

    fun closeWithResult(filesWithTypes: Array<String>) {
        parentFragmentManager?.popBackStack()
        filesChooseCallback?.onChoose(
            filePickerSettings?.cb,
            filePickerSettings?.id,
            filesWithTypes
        )
        filesChooseCallback = null
    }
}
