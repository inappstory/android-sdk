package com.inappstory.sdk.utils.filepicker.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.Surface.ROTATION_0
import android.widget.TextView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.inappstory.sdk.stories.utils.Sizes
import com.inappstory.sdk.utils.filepicker.R
import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.Executor
import kotlin.math.roundToInt


class CameraXFragment : Fragment(), ImageCapture.OnImageSavedCallback {
    private lateinit var imageCapture: ImageCapture
    private lateinit var preview: Preview
    private lateinit var videoCapture: VideoCapture<Recorder>
    private var recording: Recording? = null
    private lateinit var cameraExecutor: Executor


    private lateinit var cameraButton: CameraButton
    private lateinit var changeCameraButton: FloatingActionButton
    private lateinit var previewView: PreviewView
    private lateinit var videoProgress: CircularProgressIndicator
    private lateinit var cameraText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cs_camera_x_fragment, null)
    }


    private var videoIsStarted: Boolean = false
    private var limitInMillis = 30000L
    private var limitVideoInBytes = 30000000L

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = view.findViewById<PreviewView?>(R.id.previewView).apply {
            val x = Sizes.getScreenSize().x.coerceAtMost(
                9 * Sizes.getScreenSize().y / 16
            )
            val y = Sizes.getScreenSize().y.coerceAtMost(
                16 * Sizes.getScreenSize().x / 9
            )
            layoutParams.width = x
            layoutParams.height = y
            requestLayout()
        }
        val contentType = arguments?.getInt("contentType", 0) ?: 0
        val cameraHint = arguments?.getString("cameraHint")
        cameraButton = view.findViewById(R.id.cameraButton)
        cameraButton.contentType = contentType
        videoProgress = view.findViewById(R.id.videoProgress)
        changeCameraButton = view.findViewById(R.id.changeCam)
        cameraText = view.findViewById(R.id.cameraText)
        cameraHint?.let {
            cameraText.text = it
        }
        if (contentType != 0) cameraText.visibility = View.GONE
        context?.let { ctx ->
            cameraExecutor = ContextCompat.getMainExecutor(ctx)
            cameraButton.actions = object : CameraButton.OnAction {
                override fun onClick() {
                    when (contentType) {
                        0, 1 -> takePhoto(ctx)
                        2 -> if (videoIsStarted) {
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                stopVideo()
                            }
                        } else {
                            prepareAndStart(ctx)
                        }
                    }
                }

                override fun onLongPressDown() {
                    when (contentType) {
                        0, 2 -> prepareAndStart(ctx)
                    }
                }

                override fun onLongPressUp() {
                    if (videoIsStarted) {
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            stopVideo()
                        }
                    }
                }
            }
            changeCameraButton.setOnClickListener {
                flipCamera(ctx)
            }
        }
    }

    override fun onPause() {
        if (this::cameraProvider.isInitialized) {
            cameraProvider.unbindAll()
        }
        super.onPause()
    }

    override fun onResume() {
        startCameraPreview(requireContext())
        super.onResume()
    }

    private var currentCamera = 0

    val ORIENTATIONS = mapOf(
        Surface.ROTATION_0 to 90,
        Surface.ROTATION_90 to 0,
        Surface.ROTATION_180 to 270,
        Surface.ROTATION_270 to 180
    )

    private fun getJpegOrientation(currentRotation: Int, lDeviceOrientation: Int = 0): Int {
        var deviceOrientation = ORIENTATIONS[lDeviceOrientation]!!
        val facingFront = currentCamera == 0
        if (facingFront) deviceOrientation = -deviceOrientation
        return (((currentRotation + deviceOrientation + 270) % 360) + 360) % 360
    }


    private val cameraSelectors =
        listOf(CameraSelector.DEFAULT_BACK_CAMERA, CameraSelector.DEFAULT_FRONT_CAMERA)

    private fun flipCamera(context: Context) {
        currentCamera = (currentCamera + 1) % 2
        if (this::cameraProvider.isInitialized) {
            cameraProvider.unbindAll()
        }
        startCameraPreview(context)
    }

    private lateinit var cameraProvider: ProcessCameraProvider

    private fun hideShowElements(hide: Boolean) {
        if (hide) {
            changeCameraButton.hide()
            cameraText.animate().alpha(0f)
        } else {
            changeCameraButton.show()
            cameraText.animate().alpha(1f)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun startCameraPreview(context: Context) {
        ProcessCameraProvider.getInstance(context).let { cameraProviderFuture ->
            cameraProviderFuture.addListener(
                {
                    cameraProvider = cameraProviderFuture.get()
                    preview = Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .setTargetRotation(
                            //   getJpegOrientation(
                            view?.display?.rotation ?: ROTATION_0
                        )
                        // )
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                    imageCapture = ImageCapture.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .setTargetRotation(
                            // getJpegOrientation(
                            view?.display?.rotation ?: ROTATION_0
                            //  )
                        )//)
                        .build()
                    val cameraSelector = cameraSelectors[currentCamera]
                    val recorder = Recorder.Builder()
                        .setQualitySelector(
                            QualitySelector.from(
                                Quality.HD,
                                FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                            )
                        )
                        .build()

                    videoCapture = VideoCapture.withOutput(recorder).apply {
                        targetRotation = //getJpegOrientation(
                            view?.display?.rotation ?: ROTATION_0
                        //)
                    }
                    try {
                        cameraProvider.bindToLifecycle(
                            this, cameraSelector, preview, imageCapture, videoCapture
                        )
                    } catch (exc: Exception) {
                    }
                },
                cameraExecutor
            )
        }
    }

    var job: Job? = null
    private fun prepareAndStart(context: Context) {
        if (videoIsStarted) return
        val fileOutput = FileOutputOptions.Builder(
            File(
                context.filesDir,
                "ugc_video_${System.currentTimeMillis()}.mp4"
            )
        )
            .setFileSizeLimit(limitVideoInBytes)
            .build()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            recording = videoCapture.output
                .prepareRecording(context, fileOutput)
                .withAudioEnabled()
                .start(cameraExecutor) {
                    when (it) {
                        is VideoRecordEvent.Start -> {
                            hideShowElements(hide = true)
                            videoIsStarted = true
                            job = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                val startTime = System.currentTimeMillis()
                                while (currentTime < limitInMillis) {
                                    updateProgress()
                                    delay(100)
                                    currentTime = System.currentTimeMillis() - startTime
                                }
                                if (videoIsStarted) {
                                    stopVideo()
                                }
                            }
                        }
                        is VideoRecordEvent.Finalize -> {
                            try {

                                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    if (videoIsStarted) {
                                        stopVideo()
                                    }
                                    withContext(Dispatchers.Main) {
                                        openPreview(
                                            isVideo = true,
                                            filePath = it.outputResults.outputUri.path ?: ""
                                        )
                                    }
                                }
                            } catch (e: IllegalStateException) {

                            }
                        }
                    }
                }
            return
        }
    }

    var currentTime = -1L

    private suspend fun updateProgress() {
        withContext(Dispatchers.Main) {
            if (videoIsStarted) {
                val progress =
                    (100 * currentTime.toFloat() / limitInMillis.toFloat()).roundToInt()
                videoProgress.progress = progress
                videoProgress.visibility = View.VISIBLE
            }
        }
    }

    private suspend fun stopVideo() {
        job?.cancel()
        job = null
        currentTime = -1
        videoIsStarted = false
        recording?.stop()
        withContext(Dispatchers.Main) {
            //hideShowElements(hide = false)
            cameraButton.stop()
            videoProgress.visibility = View.INVISIBLE
        }
    }


    private fun takePhoto(context: Context) {
        val outputFileOptionsBuilder =
            ImageCapture.OutputFileOptions.Builder(
                File(
                    context.filesDir,
                    "ugc_photo_${System.currentTimeMillis()}.jpg"
                )
            ).apply {
                if (currentCamera == 1) {
                    val metadata = ImageCapture.Metadata()
                    metadata.isReversedHorizontal = true
                    this.setMetadata(metadata)
                }
            }

        imageCapture.takePicture(outputFileOptionsBuilder.build(), cameraExecutor, this)
    }

    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            openPreview(isVideo = false, filePath = outputFileResults.savedUri?.path ?: "")
        }
    }

    private suspend fun openPreview(isVideo: Boolean, filePath: String) {
        if (this::cameraProvider.isInitialized) {
            withContext(Dispatchers.Main) {
                cameraProvider.unbindAll()
            }
        }
        delay(300)
        withContext(Dispatchers.Main) {
            (parentFragment as CameraFlowFragment).openPreviewScreen(
                isVideo = isVideo,
                filePath = filePath
            )
        }
    }

    override fun onError(exception: ImageCaptureException) {
        Log.d("ImageCaptureError", exception.stackTraceToString())
    }
}