package com.inappstory.sdk.utils.filepicker.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.inappstory.sdk.stories.utils.Sizes
import com.inappstory.sdk.utils.filepicker.R
import com.inappstory.sdk.utils.filepicker.old.FileChooseActivity

class PhotoPreviewFragment : PreviewFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        filePath = arguments?.getString("filePath") ?: ""
        return inflater.inflate(R.layout.cs_photo_preview_fragment, null)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.photo_preview).also {
            val x = Sizes.getScreenSize().x.coerceAtMost(
                9 * Sizes.getScreenSize().y / 16
            )
            val y = Sizes.getScreenSize().y.coerceAtMost(
                16 * Sizes.getScreenSize().x / 9
            )
            it.layoutParams.width = x
            it.layoutParams.height = y
            it.requestLayout()
            (requireActivity() as FileChooseActivity).loadPreview(
                path = filePath,
                isVideo = false,
                imageView = it
            )
        }
    }


}