package com.inappstory.sdk.utils.filepicker

import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.UseManagerInstanceCallback
import com.inappstory.sdk.modulesconnector.utils.ModuleInitializer

class FilePickerCore : ModuleInitializer {
    override fun initialize() {
        InAppStoryManager.useInstance(object : UseManagerInstanceCallback() {
            @Throws(Exception::class)
            override fun use(manager: InAppStoryManager) {
                manager.setFilePicker(FilePicker())
            }
        })
    }
}