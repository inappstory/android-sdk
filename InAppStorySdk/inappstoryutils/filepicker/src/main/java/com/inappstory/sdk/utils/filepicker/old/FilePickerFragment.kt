package com.inappstory.sdk.utils.filepicker.old

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.inappstory.sdk.utils.filepicker.FilePickerMainFragment
import com.inappstory.sdk.utils.filepicker.R
import com.inappstory.sdk.utils.filepicker.utils.BackPressedFragment
import com.inappstory.sdk.utils.filepicker.utils.faststart.FastStart
import java.io.File
import java.util.UUID


internal class FilePickerFragment : BackPressedFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.cs_file_picker_fragment, null)
    }

    private lateinit var uploadButton: FloatingActionButton
    private lateinit var previews: FilePreviewsList

    var acceptTypes = arrayListOf<String>()
    val selectedFiles = arrayListOf<SelectedFile>()


    private val STORAGE_PERMISSIONS_RESULT = 888
    private val CAMERA_PERMISSIONS_RESULT = 890

    private fun checkStoragePermissions() {
        activity?.apply {
            var allGranted = true;
            val localPerms = arrayListOf<String>()
            appPerms.forEach {
                if (ContextCompat.checkSelfPermission(
                        this,
                        it
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    allGranted = false;
                    localPerms.add(it)
                }
            }
            if (!allGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(localPerms.toTypedArray(), STORAGE_PERMISSIONS_RESULT)
                }
            } else {
                if (!loaded || previews.adapter?.itemCount == 0)
                    loadPreviews(true)
            }
        }
    }

    private fun checkCameraPermissions() {
        activity?.apply {
            var allGranted = true;
            val localPerms =
                arrayListOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            localPerms.forEach {
                if (ContextCompat.checkSelfPermission(
                        this,
                        it
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    allGranted = false;
                }
            }
            if (!allGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(
                        localPerms.toTypedArray(),
                        CAMERA_PERMISSIONS_RESULT
                    )
                }
            } else {
                openCameraScreen()
            }
        }
    }

    private var loaded = false
    private var dialogShown = false

    private val appPerms = arrayListOf<String>().apply {
        if (Build.VERSION.SDK_INT >= 33) {
            add("android.permission.READ_MEDIA_IMAGES")
            add("android.permission.READ_MEDIA_VIDEO")
        } else {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uploadButton = view.findViewById(R.id.upload)
        previews = view.findViewById(R.id.previews)
        arguments?.apply {
            val messageNames = getStringArray("messageNames")
            val messageValues = getStringArray("messages")
            if (messageNames != null && messageValues != null) {
                messages.putAll(messageNames.zip(messageValues).toMap())
            }
            acceptTypes = getStringArrayList("acceptTypes") ?: arrayListOf()
            messages["button_no_gallery_access"]?.let {
                galleryAccessText = it
            }
        }
        if (acceptTypes.isEmpty()) {
            activity?.onBackPressed()
            return
        }
        uploadButton.setOnClickListener {
            if (parentFragment is FilePickerMainFragment && selectedFiles.isNotEmpty()) {
                (parentFragment as FilePickerMainFragment).sendResult(convertFiles().toTypedArray())
            }
        }
    }

    private fun convertFiles(): ArrayList<String> {
        val resultFiles = arrayListOf<String>()
        selectedFiles.forEach {
            if (it.fileType == "video") {
                val currentFile = File(it.filePath)
                currentFile.extension
                val file = File(
                    "${requireContext().filesDir}/converted",
                    "${UUID.randomUUID()}_${it.filePath}"
                )
                val fs = FastStart(currentFile.absolutePath, file.absolutePath).fastStart()
                if (fs) {
                    resultFiles.add(file.absolutePath)
                } else {
                    resultFiles.add(currentFile.absolutePath)
                }
            } else {
                resultFiles.add(it.filePath)
            }
        }
        return resultFiles
    }

    override fun onStart() {
        super.onStart()
        checkStoragePermissions()
    }

    private var galleryAccessText = "Tap to allow access to your Gallery"
    private val messages = hashMapOf<String, String>()
    private val storageDefault =
        "You need storage access to load photos and videos. Tap Settings > Permissions and turn \'Files and media\' on"
    private val videoDefault =
        "You need camera and microphone access to make photos and videos. Tap Settings > Permissions and turn 'Camera' and 'Microphone' on"


    fun requestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        var allGranted = true;
        val positiveText = messages["dialog_button_settings"]
        val negativeText = messages["dialog_button_not_now"]
        if (requestCode == STORAGE_PERMISSIONS_RESULT
            || requestCode == CAMERA_PERMISSIONS_RESULT
        ) {
            if (grantResults.isNotEmpty()) {
                permissions.forEachIndexed { index, permission ->
                    if (grantResults[index] != 0)
                        allGranted = false;
                }
            } else {
                return;
            }
            when (requestCode) {
                STORAGE_PERMISSIONS_RESULT -> {
                    if (!allGranted)
                        openSettingsDialog(
                            text = messages.getOrElse(
                                "dialog_storage_permission_warning",
                                defaultValue = { storageDefault }),
                            positiveText = positiveText,
                            negativeText = negativeText,
                        ) {
                            loadPreviews(false)
                        }
                    else
                        loadPreviews(true)
                }
                CAMERA_PERMISSIONS_RESULT -> {
                    if (!allGranted)
                        openSettingsDialog(
                            text = messages.getOrElse(
                                "dialog_video_permissions_warning",
                                defaultValue = { videoDefault }),
                            positiveText = positiveText,
                            negativeText = negativeText,
                        )
                    else
                        openCameraScreen()
                }
            }
        }
    }

    private fun loadPreviews(hasFileAccess: Boolean) {
        loaded = hasFileAccess

        val galleryFileLimitText =
            messages["warns_file_picker_files_limit"] ?: "You can select up to 10 files"
        val allowMultiple = arguments?.getBoolean("allowMultiple") ?: false
        val filePickerFilesLimit = arguments?.getInt("filePickerFilesLimit") ?: 10
        val filePickerPhotoSizeLimit =
            arguments?.getLong("filePickerImageMaxSizeInBytes") ?: 10000000L
        val filePickerVideoSizeLimit =
            arguments?.getLong("filePickerVideoMaxSizeInBytes") ?: 10000000L
        val filePickerFileDurationLimit =
            arguments?.getLong("filePickerVideoMaxLengthInSeconds") ?: 10
        val fileLimitPhotoSize = messages["title_image_max_size_limit"] ?: "File is too large"
        val fileLimitVideoSize = messages["title_video_max_size_limit"] ?: "File is too large"
        val fileLimitVideoDuration =
            messages["title_video_max_duration_limit"] ?: "File is too large"

        val translations = mapOf(
            "galleryFileLimitText" to galleryFileLimitText,
            "galleryAccessText" to galleryAccessText,
            "fileLimitPhotoSize" to fileLimitPhotoSize,
            "fileLimitVideoSize" to fileLimitVideoSize,
            "fileLimitVideoDuration" to fileLimitVideoDuration
        )
        previews.load(
            hasFileAccess = hasFileAccess,
            allowMultipleSelection = allowMultiple,
            mimeTypes = acceptTypes,
            clickCallback = object : FileClickCallback {
                override fun select(file: SelectedFile) {
                    selectedFiles.add(file)
                    //selectedFile = filePath
                    uploadButton.show()
                }

                override fun unselect(file: SelectedFile) {
                    selectedFiles.remove(file)
                    if (selectedFiles.isEmpty())
                        uploadButton.hide()
                }
            },
            cameraCallback = object : OpenCameraClickCallback {
                override fun open() {
                    checkCameraPermissions()
                    //openCameraScreen(isVideo)
                }
            },
            noAccessCallback = object : NoAccessCallback {
                override fun click() {
                    checkStoragePermissions()
                }
            },
            galleryFileMaxCount = filePickerFilesLimit,
            pickerFilter = PickerFilter(
                filePickerPhotoSizeLimit,
                filePickerVideoSizeLimit,
                1000L * filePickerFileDurationLimit
            ),
            translations = translations
        )
    }

    private fun openSettingsDialog(
        text: String,
        positiveText: String? = null,
        negativeText: String? = null,
        negativeCallback: () -> Unit = {},
    ) {
        if (dialogShown) return
        activity?.apply {
            AlertDialog.Builder(this)
                .setMessage(text)
                .setCancelable(true)
                .setPositiveButton(positiveText ?: "Settings") { dialog, which ->
                    dialog?.dismiss()
                    dialogShown = false
                    openSettingsScreen()
                }
                .setNegativeButton(negativeText ?: "Not now") { dialog, which ->
                    dialog?.dismiss()
                    dialogShown = false
                    negativeCallback.invoke()
                }
                .create()
                .show()
            dialogShown = true
        }
    }

    private fun openSettingsScreen() {
        activity?.apply {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }

    }

    private fun openCameraScreen() {
        if (parentFragment is FilePickerMainFragment) {
            loaded = false
            (parentFragment as FilePickerMainFragment).openFileCameraScreen(
                Bundle().also {
                    it.putString(
                        "cameraHint",
                        messages["title_camera_button"] ?: "Tap for photo, hold for video"
                    )
                    it.putInt("contentType", arguments?.getInt("contentType", 0) ?: 0)
                }
            )
        }
    }

}