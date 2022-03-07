package com.example.simplydo.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View


/**
 * TODO: document your custom view class.
 */
internal val TAG = StackedProgressBar::class.java.canonicalName

class StackedProgressBar : View {


    private lateinit var paint: Paint

    private lateinit var progressBarCanvas: Canvas

    private var arrayChart = arrayListOf(
        25, 25, 25, 25
    )
    private var totalItem = 100


    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        paint = Paint()
        paint.strokeWidth = 10f
        paint.style = Paint.Style.FILL
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        progressBarCanvas = canvas
        drawProgressBar(progressBarCanvas)
    }

    private fun drawProgressBar(canvas: Canvas) {
        Log.i(TAG, "drawProgressBar: $totalItem")
        val colors = arrayListOf(
            Color.parseColor("#FC4F4F"),
            Color.parseColor("#FF9F45"),
            Color.parseColor("#FFE162"),
            Color.parseColor("#49FF00")
        )

        var leftPosition = 0
        for ((index, item) in arrayChart.withIndex()) {

            paint.color = colors[index]
            val firstSize = (leftPosition + width * item / totalItem)

            val rect = RectF(leftPosition.toFloat(), 0f, firstSize.toFloat(), 10f)

            val corners = floatArrayOf(15f, 15f, 15f, 15f, 15f, 15f, 15f, 15f)
            val path = Path()
            path.addRoundRect(rect, corners, Path.Direction.CW)
            canvas.drawPath(path, paint)
            leftPosition += firstSize + 1 - leftPosition
        }

    }

    fun updateProgressSize(contactsByStatus: ArrayList<Int>, size: Int) {
        arrayChart = contactsByStatus
        totalItem = size
        Log.i(TAG, "updateProgressSize: $totalItem")
        invalidate()
    }
}