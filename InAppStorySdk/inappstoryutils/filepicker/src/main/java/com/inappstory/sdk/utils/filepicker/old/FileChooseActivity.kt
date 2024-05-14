package com.inappstory.sdk.utils.filepicker.old

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.inappstory.sdk.utils.filepicker.R
import com.inappstory.sdk.utils.filepicker.camera.CameraFlowFragment
import com.inappstory.sdk.utils.filepicker.utils.BackPressedFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


internal class FileChooseActivity : AppCompatActivity() {
    val picker: FilePickerAPI? = null

    @TargetApi(23)
    private fun askPermissions() {
        val readPermission = "android.permission.READ_EXTERNAL_STORAGE"
        val writePermission = "android.permission.WRITE_EXTERNAL_STORAGE"
        val permissions = arrayListOf<String>()
        if (ActivityCompat.checkSelfPermission(this, readPermission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(readPermission)
        }
        if (ActivityCompat.checkSelfPermission(this, writePermission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(writePermission)
        }
        if (permissions.isNotEmpty())
            requestPermissions(permissions.toTypedArray(), 200)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cs_file_choose_activity)



        if (savedInstanceState == null) {
            val bundle = Bundle().apply {
                putStringArrayList(
                    "acceptTypes",
                    intent.getStringArrayListExtra("acceptTypes")
                )
                putInt("contentType", intent.getIntExtra("contentType", 0))
                putStringArray(
                    "messageNames",
                    intent.getStringArrayExtra("messageNames")
                )
                putBoolean(
                    "allowMultiple",
                    intent.getBooleanExtra("allowMultiple", false)
                )
                putInt(
                    "filePickerFilesLimit",
                    intent.getIntExtra("filePickerFilesLimit", 10)
                )
                putLong(
                    "filePickerImageMaxSizeInBytes",
                    intent.getLongExtra("filePickerImageMaxSizeInBytes", 30000000L)
                )
                putLong(
                    "filePickerVideoMaxSizeInBytes",
                    intent.getLongExtra("filePickerVideoMaxSizeInBytes", 30000000L)
                )
                putLong(
                    "filePickerVideoMaxLengthInSeconds",
                    intent.getLongExtra("filePickerVideoMaxLengthInSeconds", 30L)
                )
                putStringArray(
                    "messages",
                    intent.getStringArrayExtra("messages")
                )
            }
            openFilePickerScreen(bundle)
        }
    }

    private fun openFragment(fragment: Fragment, tag: String) {
        try {
            val fragmentManager =
                supportFragmentManager
            val t = fragmentManager.beginTransaction()
                .replace(R.id.fragments_layout, fragment, tag)
            t.addToBackStack(null)
            t.commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
            finish()
        }
    }


    private fun addFragment(fragment: Fragment, tag: String) {
        try {
            val fragmentManager =
                supportFragmentManager
            val t = fragmentManager.beginTransaction()
                .add(R.id.fragments_layout, fragment, tag)
            t.addToBackStack(null)
            t.commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
            finish()
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragments_layout)
        if (currentFragment !is BackPressedFragment || !currentFragment.onBackPressed()) {
            if (supportFragmentManager.backStackEntryCount == 1)
                finish()
            else
                super.onBackPressed()
        }
    }

    fun sendResult(filePath: String) {
        val intent = Intent()
        intent.putExtra("files", arrayOf(filePath))
        setResult(RESULT_OK, intent)
        finish()
    }

    fun sendResultMultiple(files: Array<String>) {
        val intent = Intent()
        intent.putExtra("files", files)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun openFilePickerScreen(bundle: Bundle) {
        FilePickerFragment().apply {
            arguments = bundle
            openFragment(this, "UGC_FILE_CHOOSE")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val fragment = supportFragmentManager.findFragmentByTag("UGC_FILE_CHOOSE")
        if (fragment is FilePickerFragment) {
            fragment.requestPermissionsResult(requestCode, permissions, grantResults)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun openFileCameraScreen(bundle: Bundle) {
        CameraFlowFragment().apply {
            arguments = bundle
            addFragment(this, "UGC_CAMERA_FLOW")
        }
    }

    private val cache = FilePreviewsCache(false)

    fun loadPreview(path: String, imageView: ImageView, isVideo: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            cache.loadPreview(path, imageView, false, isVideo)
        }
    }
}