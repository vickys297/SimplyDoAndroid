package com.example.simplydo.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * TODO: document your custom view class.
 */
class PieChartView : View {

    var arcAngle = 360F

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


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.strokeWidth = 10f
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#F76E11")
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
        canvas.drawArc(rect, -180F, 300F, true, paint)

        paint.color = Color.parseColor("#219F94")

        canvas.drawArc(rect, -240F, 60F, true, paint);

    }


}