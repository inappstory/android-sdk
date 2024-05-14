package com.inappstory.sdk.utils.filepicker.camera

import android.widget.RelativeLayout
import android.graphics.drawable.GradientDrawable
import com.inappstory.sdk.stories.utils.Sizes
import android.widget.FrameLayout
import android.view.ViewGroup
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.animation.ObjectAnimator
import android.animation.AnimatorInflater
import com.inappstory.sdk.utils.filepicker.R
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View

class CameraButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    interface OnAction {
        fun onClick()
        fun onLongPressDown()
        fun onLongPressUp()
    }

    companion object {
        const val C_TYPE_MIX = 0
        const val C_TYPE_PHOTO = 1
        const val C_TYPE_VIDEO = 2
    }

    var started = false
    var contentType = C_TYPE_MIX //0 - Mix, 1 - Photo, 2 - Video

    lateinit var actions: OnAction
    private var gradientDrawable: GradientDrawable
    private var animatedView: View
    private lateinit var mGestureDetector: GestureDetector

    init {
        val nonAnimatedGradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                Color.WHITE, Color.WHITE
            )
        )
        nonAnimatedGradientDrawable.shape = GradientDrawable.OVAL
        background = nonAnimatedGradientDrawable
        setPadding(Sizes.dpToPxExt(2), Sizes.dpToPxExt(2), Sizes.dpToPxExt(2), Sizes.dpToPxExt(2))
        background = nonAnimatedGradientDrawable
        animatedView = FrameLayout(context)
        animatedView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                Color.RED, Color.RED
            )
        )
        gradientDrawable.cornerRadius = 200.0f
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        animatedView.background = gradientDrawable
        addView(animatedView)
        setGestureDetector(context)
    }


    private fun setGestureDetector(context: Context) {
        mGestureDetector = GestureDetector(context,
            object : SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    Log.e("SimpleOnGestureListener", "onDown")
                    return true
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    val r = super.onSingleTapConfirmed(e)
                    synchronized(lock) {
                        if (!onLongPressed) {
                            actions.onClick()
                        }
                        onLongPressed = false
                    }
                    return r
                }

                override fun onLongPress(e: MotionEvent) {

                    if (contentType != C_TYPE_PHOTO) {
                        synchronized(lock) {
                            onLongPressed = true
                        }
                        start()
                        actions.onLongPressDown()
                    }
                    Log.e("SimpleOnGestureListener", "onLongPress $onLongPressed")
                /*    synchronized(lock) {
                        if (!isStatic) {
                            Log.e("SimpleOnGestureListener", "onLongPress")
                            onLongPressed = true
                            start()
                            actions.onLongPressDown()
                        }
                    }*/
                }
            })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_MOVE) {
            Log.e("SimpleOnGestureListener", "$onLongPressed $event")
        }
        if (event.action == MotionEvent.ACTION_UP ||
            event.action == MotionEvent.ACTION_CANCEL
        ) {
            synchronized(lock) {
                if (onLongPressed) {
                    postDelayed( {
                        stop()
                        actions.onLongPressUp()
                    }, 300)
                }
            }
            onLongPressed = false
        }
        return mGestureDetector.onTouchEvent(event)
    }

    private val lock = Any()

    private var onLongPressed = false

    fun start() {
        started = true
        val cornerAnimation = ObjectAnimator.ofFloat(gradientDrawable, "cornerRadius", 200.0f, 30f)
        val shiftAnimation = AnimatorInflater.loadAnimator(context, R.animator.cs_scale_down)
        shiftAnimation.setTarget(animatedView)
        val animatorSet = AnimatorSet()
        animatorSet.duration = 500
        animatorSet.playTogether(cornerAnimation, shiftAnimation)
        animatorSet.start()
    }

    fun stop() {
        if (!started) return
        started = false
        val cornerAnimation = ObjectAnimator.ofFloat(gradientDrawable, "cornerRadius", 30f, 200.0f)
        val shiftAnimation = AnimatorInflater.loadAnimator(context, R.animator.cs_scale_up)
        shiftAnimation.setTarget(animatedView)
        val animatorSet = AnimatorSet()
        animatorSet.duration = 500
        animatorSet.playTogether(cornerAnimation, shiftAnimation)
        animatorSet.start()
    }
}