package com.tretton37.twitter37.utils.ui.zoomableimageview

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import com.tretton37.twitter37.utils.AppConstants.Companion.ZERO

class ZoomableImageView : AppCompatImageView {

    private var mode = NONE

    private val customMatrix = Matrix()

    private val last = PointF()
    private val start = PointF()
    private val minScale = 0.5f
    private var maxScale = 4f
    private var m: FloatArray? = null

    private var redundantXSpace: Float = ZERO.toFloat()
    private var redundantYSpace: Float = ZERO.toFloat()
    private var saveScale = 1f
    private var right: Float = ZERO.toFloat()
    private var bottom: Float = ZERO.toFloat()
    private var originalBitmapWidth: Float = ZERO.toFloat()
    private var originalBitmapHeight: Float = ZERO.toFloat()

    private var mScaleDetector: ScaleGestureDetector? = null

    private val bmWidth: Int
        get() {
            val drawable = drawable
            return drawable?.intrinsicWidth ?: ZERO
        }

    private val bmHeight: Int
        get() {
            val drawable = drawable
            return drawable?.intrinsicHeight ?: ZERO
        }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val newScale = saveScale * scaleFactor
            if (newScale < maxScale && newScale > minScale) {
                saveScale = newScale
                val width = width.toFloat()
                val height = height.toFloat()
                right = originalBitmapWidth * saveScale - width
                bottom = originalBitmapHeight * saveScale - height

                val scaledBitmapWidth = originalBitmapWidth * saveScale
                val scaledBitmapHeight = originalBitmapHeight * saveScale

                if (scaledBitmapWidth <= width || scaledBitmapHeight <= height) {
                    customMatrix.postScale(scaleFactor, scaleFactor, width / 2, height / 2)
                } else {
                    customMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                }
            }
            return true
        }

    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        super.setClickable(true)
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        m = FloatArray(9)
        imageMatrix = customMatrix
        scaleType = ImageView.ScaleType.MATRIX
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val bmHeight = bmHeight
        val bmWidth = bmWidth

        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()
        //Fit to screen.
        val scale = if (width > height) height / bmHeight else width / bmWidth

        customMatrix.setScale(scale, scale)
        saveScale = 1f

        originalBitmapWidth = scale * bmWidth
        originalBitmapHeight = scale * bmHeight

        // Center the image
        redundantYSpace = height - originalBitmapHeight
        redundantXSpace = width - originalBitmapWidth

        customMatrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2)

        imageMatrix = customMatrix
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleDetector!!.onTouchEvent(event)

        customMatrix.getValues(m)
        val x = m!![Matrix.MTRANS_X]
        val y = m!![Matrix.MTRANS_Y]
        val curr = PointF(event.x, event.y)

        when (event.action) {
        //when one finger is touching
        //set the mode to DRAG
            MotionEvent.ACTION_DOWN -> {
                last.set(event.x, event.y)
                start.set(last)
                mode = DRAG
            }
        //when two fingers are touching
        //set the mode to ZOOM
            MotionEvent.ACTION_POINTER_DOWN -> {
                last.set(event.x, event.y)
                start.set(last)
                mode = ZOOM
            }
        //when a finger moves
        //If mode is applicable move image
            MotionEvent.ACTION_MOVE ->
                //if the mode is ZOOM or
                //if the mode is DRAG and already zoomed
                if (mode == ZOOM || mode == DRAG && saveScale > minScale) {
                    var deltaX = curr.x - last.x// x difference
                    var deltaY = curr.y - last.y// y difference
                    val scaleWidth = Math.round(originalBitmapWidth * saveScale).toFloat()// width after applying current scale
                    val scaleHeight = Math.round(originalBitmapHeight * saveScale).toFloat()// height after applying current scale

                    var limitX = false
                    var limitY = false

                    //if scaleWidth is smaller than the views width
                    //in other words if the image width fits in the view
                    //limit left and right movement
                    if (scaleWidth < width && scaleHeight < height) {
                        // don't do anything
                    } else if (scaleWidth < width) {
                        deltaX = 0f
                        limitY = true
                    } else if (scaleHeight < height) {
                        deltaY = 0f
                        limitX = true
                    } else {
                        limitX = true
                        limitY = true
                    }//if the image doesnt fit in the width or height
                    //limit both up and down and left and right
                    //if scaleHeight is smaller than the views height
                    //in other words if the image height fits in the view
                    //limit up and down movement

                    if (limitY) {
                        if (y + deltaY > 0) {
                            deltaY = -y
                        } else if (y + deltaY < -bottom) {
                            deltaY = -(y + bottom)
                        }

                    }

                    if (limitX) {
                        if (x + deltaX > 0) {
                            deltaX = -x
                        } else if (x + deltaX < -right) {
                            deltaX = -(x + right)
                        }

                    }
                    //move the image with the customMatrix
                    customMatrix.postTranslate(deltaX, deltaY)
                    //set the last touch location to the current
                    last.set(curr.x, curr.y)
                }
        //first finger is lifted
            MotionEvent.ACTION_UP -> {
                mode = NONE
                val xDiff = Math.abs(curr.x - start.x).toInt()
                val yDiff = Math.abs(curr.y - start.y).toInt()
                if (xDiff < CLICK && yDiff < CLICK)
                    performClick()
            }
        // second finger is lifted
            MotionEvent.ACTION_POINTER_UP -> mode = NONE
        }
        imageMatrix = customMatrix
        invalidate()
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    companion object {
        internal val NONE = 0
        internal val DRAG = 1
        internal val ZOOM = 2
        internal val CLICK = 3
    }
}
