package com.example.simplydo.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.random.Random


/**
 * TODO: document your custom view class.
 */
internal val TAG = StyledProgressBar::class.java.canonicalName

class StyledProgressBar : View {

    private val progressBarHeight = height
    private val progressBarWidth = width
    private lateinit var paint: Paint


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

        val arrayChart = arrayListOf(
            23, 10, 20, 13, 33
        )
        var leftPosition = 0
        for ((index, item) in arrayChart.withIndex()) {
            val color =
                Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))

            paint.color = color

            val firstSize = (leftPosition + width * item / 100)
            Log.i(TAG, "onDraw: width $width")
            Log.i(TAG, "onDraw: firstSize $firstSize")
            val rect = RectF(leftPosition.toFloat(), 0f, firstSize.toFloat(), 10f)
            val corners = floatArrayOf(15f, 15f, 15f, 15f, 15f, 15f, 15f, 15f)
            val path = Path()
            path.addRoundRect(rect, corners, Path.Direction.CW)
            canvas.drawPath(path, paint)
            leftPosition += firstSize + 1 - leftPosition
        }


//        canvas.drawRect(rect, paint)

    }
}