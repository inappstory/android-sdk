package com.inappstory.sdk.utils.filepicker.old

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ThumbnailUtils
import android.os.Build
import android.os.CancellationSignal
import android.os.Handler
import android.provider.MediaStore
import android.util.Size
import android.widget.ImageView
import androidx.exifinterface.media.ExifInterface
import com.inappstory.sdk.stories.utils.Sizes
import com.inappstory.sdk.utils.filepicker.utils.BitmapCache
import com.inappstory.sdk.utils.filepicker.utils.QueuedTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class FilePreviewsCache {
    private var memoryCache: BitmapCache? = null
    private val maxThreads = 6
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var noCache = false

    constructor(noCache: Boolean) {
        this.noCache = noCache

        initialize()
    }

    constructor() {
        initialize()
    }

    private fun initialize() {
        if (noCache) return
        initializeCache()
        runTaskQueue()
    }

    private fun initializeCache() {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 16
        memoryCache = BitmapCache(cacheSize)
    }

    private fun runTaskQueue() {
        coroutineScope.launch {
            while (true) {
                checkAndLoadTask()
                delay(200)
            }
        }
    }

    fun remove(path: String) {
        synchronized(queueLock) {
            tasks.remove(path)
        }
    }

    suspend fun loadPreview(
        path: String,
        imageView: ImageView,
        unavailable: Boolean,
        isVideo: Boolean
    ) {
        if (isVideo)
            loadVideoThumbnail(
                path,
                imageView,
                unavailable
            )
        else
            loadBitmap(
                path,
                imageView,
                unavailable,
                noCache
            )
    }

    private suspend fun loadVideoThumbnail(
        path: String,
        imageView: ImageView,
        unavailable: Boolean
    ) {
        val bmp = getBitmap(path)
        if (bmp == null) {
            val mSize = Size(96, 96)
            val ca = CancellationSignal()
            val bitmapThumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ThumbnailUtils.createVideoThumbnail(
                    File(
                        path
                    ), mSize, ca
                )
            } else {
                ThumbnailUtils.createVideoThumbnail(
                    path,
                    MediaStore.Video.Thumbnails.MINI_KIND
                )
            }
            val loaded = if (unavailable) {
                toGrayscale(
                    bitmapThumbnail
                )
            } else {
                bitmapThumbnail
            }
            if (loaded == null) return
            synchronized(memCacheLock) {
                if (!noCache) memoryCache!!.put(
                    path,
                    loaded
                )
            }
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(
                    loaded
                )
            }
        }
    }

    private suspend fun loadBitmap(
        path: String,
        imageView: ImageView,
        noCache: Boolean,
        unavailable: Boolean
    ) {
        var bmp: Bitmap? = null
        if (!noCache) bmp = getBitmap(path)
        if (bmp == null) {
            if (noCache) {
                val file = File(path)
                val loaded =
                    if (unavailable) toGrayscale(decodeFile(file)) else decodeFile(file)
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(
                        loaded
                    )
                }
            } else {
                addPriorityTask(path, imageView)
            }
        }
    }

    private fun toGrayscale(bmpOriginal: Bitmap?): Bitmap? {
        if (bmpOriginal == null) return null
        val width = bmpOriginal.height
        val height = bmpOriginal.width
        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(bmpOriginal, 0f, 0f, paint)
        return bmpGrayscale
    }

    @Throws(IOException::class)
    private fun getExifInformation(filePath: String): Int {
        val ei = ExifInterface(filePath)
        return ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(img: Bitmap, filePath: String): Bitmap {
        return when (getExifInformation(filePath)) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipImage(img)
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    private val queueLock = Any()
    private val tasks = HashMap<String, QueuedTask>()

    private fun getMaxPriorityTaskKey(): String? {
        synchronized(queueLock) {
            if (tasks.isEmpty()) return null
            var minTaskKey: String? = null
            var minPriority = 99999
            var startedCount = 0
            for (taskKey in tasks.keys) {
                val cur =
                    tasks[taskKey] ?: continue
                if (cur.started) {
                    startedCount++
                    continue
                }
                if (startedCount >= maxThreads) return null
                if (minPriority > cur.priority) {
                    minPriority = cur.priority
                    minTaskKey = taskKey
                }
            }
            if (minTaskKey != null) {
                val cur: QueuedTask? =
                    tasks[minTaskKey]
                if (cur != null) cur.started = true
            }
            return minTaskKey!!
        }
    }

    var handler = Handler()

    private val runnable: Runnable = Runnable {

    }


    private fun checkAndLoadTask() {
        getMaxPriorityTaskKey()?.let { key ->
            coroutineScope.launch {
                val loaded = decodeFile(File(key))
                val task = tasks[key]
                if (loaded == null) return@launch
                synchronized(memCacheLock) {
                    if (!noCache) {
                        memoryCache!!.put(key, loaded)
                        remove(key)
                    }
                }
                withContext(Dispatchers.Main) {
                    task?.imageView?.setImageBitmap(
                        loaded
                    )
                }
            }

        }
    }

    private fun addPriorityTask(key: String, imageView: ImageView) {
        synchronized(memCacheLock) { if (!noCache && memoryCache!![key] != null) return }
        synchronized(queueLock) {
            for (task in tasks.values) {
                task.priority++
            }
            tasks.put(key, QueuedTask(imageView))
        }
    }

    private fun decodeFile(f: File): Bitmap? {
        try {
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)
            val REQUIRED_SIZE = Sizes.dpToPxExt(200)
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            while (width_tmp / 2 >= REQUIRED_SIZE || height_tmp / 2 >= REQUIRED_SIZE) {
                width_tmp /= 2
                height_tmp /= 2
                scale *= 2
            }
            o.inJustDecodeBounds = false
            o.inSampleSize = scale
            val bitmap = BitmapFactory.decodeFile(f.absolutePath, o)
            return rotateImageIfRequired(bitmap, f.absolutePath)
            //
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private val memCacheLock = Any()
    private fun getBitmap(path: String): Bitmap? {
        synchronized(memCacheLock) {
            if (noCache) return null
            return memoryCache!![path]
        }
    }

    companion object {
        private fun flipImage(img: Bitmap): Bitmap {
            val matrix = Matrix()
            matrix.postScale(-1.0f, 1.0f)
            val rotatedImg = Bitmap.createBitmap(
                img, 0, 0,
                img.width, img.height, matrix, true
            )
            img.recycle()
            return rotatedImg
        }

        private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            val rotatedImg = Bitmap.createBitmap(
                img, 0, 0,
                img.width, img.height, matrix, true
            )
            img.recycle()
            return rotatedImg
        }
    }
}