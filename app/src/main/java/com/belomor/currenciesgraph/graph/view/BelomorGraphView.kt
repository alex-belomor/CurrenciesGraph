package com.belomor.currenciesgraph.graph.view

import android.content.Context
import android.graphics.*
import android.util.ArrayMap
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.belomor.currenciesgraph.R
import com.belomor.currenciesgraph.graph.data.ChartValuesArray
import com.belomor.currenciesgraph.graph.data.GridData
import com.belomor.currenciesgraph.graph.extensions.getDpInFloat

class BelomorGraphView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val HEIGHT_SEEK_BAR = getDpInFloat(62f)

    private var data: ChartValuesArray? = null

    private val gridPaint = Paint()
    private val valuesPaint = Paint()
    private val linesPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var componentWidth: Float = 0f
    private var componentHeight: Float = 0f

    private val horizontalMargin = getDpInFloat(16f)

    private val seekVerticalBordersPaint = Paint()
    private val seekHorizontalBordersPaint = Paint()
    private val seekOffPaint = Paint()

    private var beginSeekX = 0f
    private var endSeekX = 0f
    private var lengthSeek = 0f

    private var lastTouchX: Float? = 0f
    private var beginTouchX: Float? = 0f

    private var touchSeek = false
    private var touchExpandSeekLeft = false
    private var touchExpandSeekRight = false

    private val seekMatrix = Matrix()

    private val arrayPath = ArrayMap<String, Path>()

    private val gridArray = ArrayList<GridData>()

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
        linesPaint.color = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)

        linesPaint.strokeWidth = getDpInFloat(1f)
        linesPaint.strokeJoin = Paint.Join.ROUND
        linesPaint.strokeCap = Paint.Cap.ROUND
        linesPaint.isAntiAlias = true
        linesPaint.style = Paint.Style.STROKE

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
                    ((componentHeight - HEIGHT_SEEK_BAR - HEIGHT_SEEK_BAR) / 5 * i).toFloat() + getDpInFloat(
                        30f
                    ),
                    componentWidth - horizontalMargin,
                    ((componentHeight - HEIGHT_SEEK_BAR - HEIGHT_SEEK_BAR) / 5 * i).toFloat() + getDpInFloat(
                        30f
                    ),
                    gridPaint
                )
            }

            drawSeekChart(it)
            drawSeekBar(it)
        }
    }

    fun setData(data: ChartValuesArray) {
        this.data = data
        invalidate()

        Log.d("SUKABLYATNAHUY", "${data.getAllMaxValue()}")
    }

    private fun drawChart(canvas: Canvas) {
        data?.let {

        }
    }

    private fun drawSeekChart(canvas: Canvas) {
        data?.let {
            val oneDateWidth = (componentWidth - horizontalMargin * 2) / (it[0].details.size - 1)
            val allMinValue = it.getAllMinValue()
            val allMaxValue = it.getAllMaxValue()
            val percentOfHeight = getDpInFloat(60f) / 100f
            val heightOfValuePercent = (allMaxValue - allMinValue) / 100f

            for (data in it) {
                if (data.visible) {
                    linesPaint.color = data.color
                    val path = Path()
                    var latestXDraw = horizontalMargin
                    path.moveTo(latestXDraw,  ((data.details[0].value - allMinValue) / heightOfValuePercent * percentOfHeight))
                    for (i in 0 until data.details.size) {
                        path.lineTo(latestXDraw, ((data.details[i].value - allMinValue) / heightOfValuePercent * percentOfHeight))
                        latestXDraw += oneDateWidth
                    }

                    path.transform(seekMatrix)
                    canvas.drawPath(path, linesPaint)
                }
            }
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
            componentHeight - HEIGHT_SEEK_BAR,
            beginSeekX,
            componentHeight,
            seekOffPaint
        )

        //off right seek background
        canvas.drawRect(
            endSeekX,
            componentHeight - HEIGHT_SEEK_BAR,
            componentWidth - horizontalMargin,
            componentHeight,
            seekOffPaint
        )

        //left vertical seek line
        canvas.drawLine(
            beginSeekX + seekVerticalBordersPaint.strokeWidth / 2,
            componentHeight - HEIGHT_SEEK_BAR,
            beginSeekX + seekVerticalBordersPaint.strokeWidth / 2,
            componentHeight,
            seekVerticalBordersPaint
        )

        //right vertical seek line
        canvas.drawLine(
            endSeekX - seekVerticalBordersPaint.strokeWidth / 2,
            componentHeight - HEIGHT_SEEK_BAR,
            endSeekX - seekVerticalBordersPaint.strokeWidth / 2,
            componentHeight,
            seekVerticalBordersPaint
        )

        //top horizontal seek line
        canvas.drawLine(
            beginSeekX + seekVerticalBordersPaint.strokeWidth,
            componentHeight - HEIGHT_SEEK_BAR + seekHorizontalBordersPaint.strokeWidth / 2,
            endSeekX - seekVerticalBordersPaint.strokeWidth,
            componentHeight - HEIGHT_SEEK_BAR + seekHorizontalBordersPaint.strokeWidth / 2,
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

        seekMatrix.setRotate(180f, componentWidth / 2f, componentHeight / 2f )
    }
}