package com.inappstory.sdk.utils.filepicker.utils

import androidx.fragment.app.Fragment

open class BackPressedFragment : Fragment() {
    fun onBackPressed(): Boolean {
        return if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
            true
        } else {
            false
        }
    }
}