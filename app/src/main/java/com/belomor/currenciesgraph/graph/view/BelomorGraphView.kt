package com.belomor.currenciesgraph.graph.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.belomor.currenciesgraph.R
import com.belomor.currenciesgraph.graph.data.GridData
import com.belomor.currenciesgraph.graph.extensions.getDpInFloat

class BelomorGraphView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    val gridPaint = Paint()
    val valuesPaint = Paint()
    val linesPaint = Paint()

    var componentWidth: Float = 0f
    var componentHeight: Float = 0f

    val horizontalMargin = getDpInFloat(16f)

    val seekVerticalBordersPaint = Paint()
    val seekHorizontalBordersPaint = Paint()
    val seekOffPaint = Paint()

    var beginSeekX = 0f
    var endSeekX = 0f
    var lengthSeek = 0f

    var lastTouchX: Float? = 0f
    var beginTouchX: Float? = 0f

    var touchSeek = false
    var touchExpandSeekLeft = false
    var touchExpandSeekRight = false

    val gridArray = ArrayList<GridData>()

    init {
        gridPaint.isAntiAlias = true
        valuesPaint.isAntiAlias = true
        linesPaint.isAntiAlias = true
        seekOffPaint.isAntiAlias = true

        seekOffPaint.color = ContextCompat.getColor(getContext(), R.color.trans_blue2)
        seekVerticalBordersPaint.color = ContextCompat.getColor(getContext(), R.color.trans_blue)
        seekHorizontalBordersPaint.color = ContextCompat.getColor(getContext(), R.color.trans_blue)

        seekVerticalBordersPaint.strokeWidth = getDpInFloat(12f)
        seekHorizontalBordersPaint.strokeWidth = getDpInFloat(4f)

        gridPaint.color = ContextCompat.getColor(getContext(), R.color.gray)
        valuesPaint.color = ContextCompat.getColor(getContext(), R.color.gray)

        gridPaint.strokeWidth = getDpInFloat(1f)

        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                Log.d("SUKABLYAT", "X = ${event.x} , Y = ${event.y}")

                beginTouchX = event.x
                if (event.y < componentHeight && event.y > componentHeight - getDpInFloat(60f) &&
                    event.x > horizontalMargin && event.x < componentWidth - horizontalMargin
                ) {
                    if (event.x < beginSeekX + seekVerticalBordersPaint.strokeWidth &&
                        event.x > beginSeekX - seekVerticalBordersPaint.strokeWidth
                    ) {
                        touchExpandSeekLeft = true
                    } else if (event.x < endSeekX + seekVerticalBordersPaint.strokeWidth &&
                        event.x > endSeekX - seekVerticalBordersPaint.strokeWidth
                    ) {
                        touchExpandSeekRight = true
                    } else if (event.x > beginSeekX && event.x < endSeekX) {
                        touchSeek = true
                    }
                }
            }

            lastTouchX = event?.x

            if (event.action == MotionEvent.ACTION_UP) {
                touchSeek = false
                touchExpandSeekLeft = false
                touchExpandSeekRight = false
                beginTouchX = 0f
                lastTouchX = 0f
            }

            invalidate()
            true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            for (i in 0..5) {
                it.drawLine(
                    horizontalMargin,
                    ((componentHeight - getDpInFloat(60f) - getDpInFloat(60f)) / 5 * i).toFloat() + getDpInFloat(
                        30f
                    ),
                    componentWidth - horizontalMargin,
                    ((componentHeight - getDpInFloat(60f) - getDpInFloat(60f)) / 5 * i).toFloat() + getDpInFloat(
                        30f
                    ),
                    gridPaint
                )
            }

            drawSeekBar(it)
        }
    }

    private fun drawSeekBar(canvas: Canvas) {
        val checkBeginSeekX = beginSeekX + (lastTouchX!! - beginTouchX!!)
        val checkEndSeekX = endSeekX + (lastTouchX!! - beginTouchX!!)

        if (touchSeek) {

            if (checkBeginSeekX >= horizontalMargin && checkEndSeekX <= componentWidth - horizontalMargin) {
                beginSeekX = checkBeginSeekX
                endSeekX = checkEndSeekX
                lengthSeek = endSeekX - beginSeekX
                beginTouchX = lastTouchX
            } else if (checkBeginSeekX <= horizontalMargin) {
                beginSeekX = horizontalMargin
                endSeekX = beginSeekX + lengthSeek
                beginTouchX = lastTouchX
            } else if (checkEndSeekX >= componentWidth - horizontalMargin) {
                endSeekX = componentWidth - horizontalMargin
                beginSeekX = endSeekX - lengthSeek
                beginTouchX = lastTouchX
            }
        } else if (touchExpandSeekLeft) {

            if (checkBeginSeekX >= horizontalMargin) {
                if (endSeekX - checkBeginSeekX < getDpInFloat(80f)) {
                    beginSeekX = endSeekX - getDpInFloat(80f)
                } else if (beginSeekX < horizontalMargin) {
                    beginSeekX = horizontalMargin
                } else {
                    beginSeekX = checkBeginSeekX
                }
            }

            beginTouchX = lastTouchX
        } else if (touchExpandSeekRight) {

            if (checkEndSeekX <= componentWidth - horizontalMargin) {
                if (checkEndSeekX - beginSeekX < getDpInFloat(80f)) {
                    endSeekX = beginSeekX + getDpInFloat(80f)
                } else if (endSeekX > componentWidth - horizontalMargin) {
                    endSeekX = componentWidth - horizontalMargin
                } else {
                    endSeekX = checkEndSeekX
                }
            }

            beginTouchX = lastTouchX
        }

        //off left seek background
        canvas.drawRect(
            horizontalMargin,
            componentHeight - getDpInFloat(60f),
            beginSeekX,
            componentHeight,
            seekOffPaint
        )

        //off right seek background
        canvas.drawRect(
            endSeekX,
            componentHeight - getDpInFloat(60f),
            componentWidth - horizontalMargin,
            componentHeight,
            seekOffPaint
        )

        //left vertical seek line
        canvas.drawLine(
            beginSeekX + seekVerticalBordersPaint.strokeWidth / 2,
            componentHeight - getDpInFloat(60f),
            beginSeekX + seekVerticalBordersPaint.strokeWidth / 2,
            componentHeight,
            seekVerticalBordersPaint
        )

        //right vertical seek line
        canvas.drawLine(
            endSeekX - seekVerticalBordersPaint.strokeWidth / 2,
            componentHeight - getDpInFloat(60f),
            endSeekX - seekVerticalBordersPaint.strokeWidth / 2,
            componentHeight,
            seekVerticalBordersPaint
        )

        //top horizontal seek line
        canvas.drawLine(
            beginSeekX + seekVerticalBordersPaint.strokeWidth,
            componentHeight - getDpInFloat(60f) + seekHorizontalBordersPaint.strokeWidth / 2,
            endSeekX - seekVerticalBordersPaint.strokeWidth,
            componentHeight - getDpInFloat(60f) + seekHorizontalBordersPaint.strokeWidth / 2,
            seekHorizontalBordersPaint
        )

        //bottom horizontal seek line
        canvas.drawLine(
            beginSeekX + seekVerticalBordersPaint.strokeWidth,
            componentHeight - seekHorizontalBordersPaint.strokeWidth / 2,
            endSeekX - seekVerticalBordersPaint.strokeWidth,
            componentHeight - seekHorizontalBordersPaint.strokeWidth / 2,
            seekHorizontalBordersPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        componentWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        componentHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()

        beginSeekX = horizontalMargin
        endSeekX = (componentWidth - horizontalMargin)
    }
}