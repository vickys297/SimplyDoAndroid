package com.example.simplydo.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet


class ProfileImageView : androidx.appcompat.widget.AppCompatImageView {
    private val radius = 500.0f
    private var path: Path? = null
    private var rect: RectF? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        path = Path()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        rect = RectF(0F, 0F, this.width.toFloat(), this.height.toFloat())
        path!!.addRoundRect(rect!!, radius, radius, Path.Direction.CW)
        canvas.clipPath(path!!)
        super.onDraw(canvas)
    }
}