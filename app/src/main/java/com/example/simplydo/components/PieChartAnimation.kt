package com.example.simplydo.components

import android.view.animation.Animation
import android.view.animation.Transformation

class PieChartAnimation(arcView: PieChartView, newAngle: Int) : Animation() {
    private var arcView: PieChartView? = null

    private var oldAngle = 0f
    private var newAngle = 0f

    init {
        oldAngle = arcView.arcAngle
        this.newAngle = newAngle.toFloat()
        this.arcView = arcView
    }

    override fun applyTransformation(interpolatedTime: Float, transformation: Transformation?) {
        val angle = 0 + (newAngle - oldAngle) * interpolatedTime
        arcView!!.arcAngle = angle
        arcView!!.requestLayout()
    }
}