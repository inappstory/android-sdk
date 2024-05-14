package com.inappstory.sdk.utils.filepicker.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inappstory.sdk.stories.ui.video.VideoPlayer
import com.inappstory.sdk.stories.utils.Sizes
import com.inappstory.sdk.utils.filepicker.R

class VideoPreviewFragment : PreviewFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        filePath = arguments?.getString("filePath") ?: ""
        return inflater.inflate(R.layout.cs_video_preview_fragment, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<VideoPlayer>(R.id.video_preview).also {
            val x = Sizes.getScreenSize().x.coerceAtMost(
                9 * Sizes.getScreenSize().y / 16
            )
            val y = Sizes.getScreenSize().y.coerceAtMost(
                16 * Sizes.getScreenSize().x / 9
            )
            it.layoutParams.width = x
            it.layoutParams.height = y
            it.requestLayout()
            it.loadVideo(filePath)
        }
    }
}