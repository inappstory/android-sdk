package com.inappstory.sdk.utils.filepicker

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.inappstory.sdk.modulesconnector.utils.filepicker.IFilePicker
import com.inappstory.sdk.modulesconnector.utils.filepicker.OnFilesChooseCallback
import com.inappstory.sdk.network.JsonParser
import com.inappstory.sdk.stories.api.models.Session
import com.inappstory.sdk.utils.filepicker.utils.BackPressedFragment
import java.util.ArrayList

class FilePicker : IFilePicker {

    override fun setPickerSettings(settings: String?) {
        FilePickerVM.filePickerSettings =
            JsonParser.fromJson(
                settings,
                FilePickerSettings::class.java
            )
    }

    override fun onBackPressed(): Boolean {
        val fm = FilePickerVM.parentFragmentManager ?: return false
        val parentFragment = fm.findFragmentById(R.id.fragments_layout) ?: return false
        val childFragment =
            parentFragment.childFragmentManager.findFragmentById(R.id.fragments_layout)
        if (childFragment !is BackPressedFragment || !childFragment.onBackPressed()) {
            if (parentFragment.childFragmentManager.backStackEntryCount > 1)
                parentFragment.childFragmentManager.popBackStack()
            else
                close()
        }
        return true
    }

    override fun show(
        context: Context,
        fragmentManager: FragmentManager,
        containerId: Int,
        callback: OnFilesChooseCallback
    ) {
        val args = convertSettings()
        FilePickerVM.parentFragmentManager = fragmentManager;
        FilePickerVM.filesChooseCallback = callback
        if (args == null) {
            callback.onError(
                FilePickerVM.filePickerSettings?.cb,
                FilePickerVM.filePickerSettings?.id,
                "Wrong accept types"
            )
            return
        }
        FilePickerMainFragment().apply {
            arguments = args
            try {
                val t = fragmentManager.beginTransaction()
                    .add(containerId, this, "FilePicker")
                t.addToBackStack(null)
                t.commitAllowingStateLoss()
            } catch (e: IllegalStateException) {
                callback.onError(
                    FilePickerVM.filePickerSettings?.cb,
                    FilePickerVM.filePickerSettings?.id,
                    e.message.orEmpty()
                )
                FilePickerVM.filesChooseCallback = null
            }
        }
    }

    override fun close() {
        FilePickerVM.cancel()
    }

    private fun convertSettings(): Bundle? {
        val settings = FilePickerVM.filePickerSettings
        val acceptTypes = settings?.accept?.split(",") as ArrayList<String>?
        var hasVideo = false
        var hasPhoto = false
        acceptTypes?.forEach {
            if (it.startsWith("image")) hasPhoto = true
            if (it.startsWith("video")) hasVideo = true
        }
        if (!(hasVideo || hasPhoto)) return null;
        val config = settings?.config ?: FilePickerConfig()
        val messages = config.messages ?: hashMapOf()
        return Bundle().apply {
            putStringArrayList(
                "acceptTypes",
                settings?.accept?.split(",") as ArrayList<String>?
            )
            putInt(
                "contentType",
                when {
                    hasVideo && hasPhoto -> 0 //Mix
                    hasVideo -> 2 //Video
                    else -> 1 //Photo
                }
            )
            val (keys, values) = messages.toList().unzip()
            putStringArray(
                "messageNames",
                keys.toTypedArray()
            )
            putStringArray(
                "messages",
                values.toTypedArray()
            )

            putBoolean(
                "allowMultiple",
                settings?.multiple ?: false
            )
            putInt(
                "filePickerFilesLimit",
                settings?.config?.filePickerFilesLimit ?: 10
            )
            putLong(
                "filePickerImageMaxSizeInBytes",
                settings?.config?.filePickerImageMaxSizeInBytes ?: 30000000L
            )
            putLong(
                "filePickerVideoMaxSizeInBytes",
                settings?.config?.filePickerVideoMaxSizeInBytes ?: 30000000L
            )
            putLong(
                "filePickerVideoMaxLengthInSeconds",
                settings?.config?.filePickerVideoMaxLengthInSeconds ?: 30L
            )

        }

    }


}