package com.example.simplydo.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * TODO: document your custom view class.
 */
class ColorViewBadge : View {

    private var paint = Paint()
    private lateinit var badgeCanvas: Canvas

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

    }

    fun setIndicatorColor(colorString: String) {
        paint.color = Color.parseColor(colorString)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        badgeCanvas = canvas
        drawBadge(badgeCanvas)
    }

    private fun drawBadge(badgeCanvas: Canvas) {

        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL

        val rect = RectF(4f, 4f, width.toFloat(), height.toFloat())
        val corners = floatArrayOf(
            100f, 100f,
            100f, 100f,
            100f, 100f,
            100f, 100f
        )
        val path = Path()
        path.addRoundRect(rect, corners, Path.Direction.CW)
        badgeCanvas.drawArc(rect, 0F, 360F, true, paint)
    }
}