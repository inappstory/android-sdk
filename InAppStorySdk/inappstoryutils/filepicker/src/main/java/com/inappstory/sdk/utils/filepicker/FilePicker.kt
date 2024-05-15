package com.inappstory.sdk.utils.filepicker

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.inappstory.sdk.modulesconnector.utils.filepicker.IFilePicker
import com.inappstory.sdk.modulesconnector.utils.filepicker.OnFilesChooseCallback
import com.inappstory.sdk.utils.filepicker.utils.BackPressedFragment
import java.util.ArrayList

class FilePicker : IFilePicker {

    private val settings: HashMap<String, Any> = hashMapOf()

    override fun setPickerSettings(settings: HashMap<String, Any>) {
        this.settings.putAll(settings)
    }

    override fun onBackPressed(): Boolean {
        val fm = FilePickerCore.parentFragmentManager ?: return false
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

    override fun close() {
        FilePickerCore.cancel()
    }

    private fun convertSettings(): Bundle {
        return Bundle().apply {
            putStringArrayList(
                "acceptTypes",
                settings["acceptTypes"] as ArrayList<String>?
            )
            putInt(
                "contentType",
                settings["contentType"] as Int? ?: 0
            )
            putStringArray(
                "messageNames",
                settings["messageNames"] as Array<String>?
            )
            putBoolean(
                "allowMultiple",
                settings["allowMultiple"] as Boolean? ?: false
            )
            putInt(
                "filePickerFilesLimit",
                settings["filePickerFilesLimit"] as Int? ?: 10
            )
            putLong(
                "filePickerImageMaxSizeInBytes",
                settings["filePickerImageMaxSizeInBytes"] as Long? ?: 30000000L
            )
            putLong(
                "filePickerVideoMaxSizeInBytes",
                settings["filePickerVideoMaxSizeInBytes"] as Long? ?: 30000000L
            )
            putLong(
                "filePickerVideoMaxLengthInSeconds",
                settings["filePickerVideoMaxLengthInSeconds"] as Long? ?: 30L
            )
            putStringArray(
                "messages",
                settings["messages"] as Array<String>?
            )
        }

    }

    override fun show(
        context: Context,
        fragmentManager: FragmentManager,
        callback: OnFilesChooseCallback
    ) {
        FilePickerCore.parentFragmentManager = fragmentManager;
        FilePickerCore.filesChooseCallback = callback
        FilePickerMainFragment().apply {
            arguments = convertSettings()
            try {
                val t = fragmentManager.beginTransaction()
                    .add(R.id.fragments_layout, this, tag)
                t.addToBackStack(null)
                t.commitAllowingStateLoss()
            } catch (e: IllegalStateException) {
                callback.onError(e.message.orEmpty())
                FilePickerCore.filesChooseCallback = null
            }
        }
    }
}