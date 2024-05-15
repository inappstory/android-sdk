package com.inappstory.sdk.utils.filepicker

import android.net.Uri
import androidx.fragment.app.FragmentManager
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.UseManagerInstanceCallback
import com.inappstory.sdk.modulesconnector.utils.ModuleInitializer
import com.inappstory.sdk.modulesconnector.utils.filepicker.OnFilesChooseCallback

class FilePickerCore : ModuleInitializer {
    companion object FilePickerVM {
        var filesChooseCallback: OnFilesChooseCallback? = null
        var parentFragmentManager: FragmentManager? = null

        fun close() {
            parentFragmentManager?.popBackStack()
        }

        fun cancel() {
            parentFragmentManager?.popBackStack()
            filesChooseCallback?.onCancel()
            filesChooseCallback = null
        }

        fun closeWithError(error: String) {
            parentFragmentManager?.popBackStack()
            filesChooseCallback?.onError(error)
            filesChooseCallback = null
        }

        fun closeWithResult(filesWithTypes: Array<String>) {
            parentFragmentManager?.popBackStack()
            filesChooseCallback?.onChoose(filesWithTypes)
            filesChooseCallback = null
        }
    }

    override fun initialize() {
        InAppStoryManager.useInstance(object : UseManagerInstanceCallback() {
            @Throws(Exception::class)
            override fun use(manager: InAppStoryManager) {
                manager.setFilePicker(FilePicker())
            }
        })
    }
}