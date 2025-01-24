package com.test.my.app.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ShiftingIndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var itemCount = 0
    private var activePosition = 0
    private var circleRadius = 12f
    private var circleSpacing = 24f
    private var activeWidth = 48f
    private var indicatorColor = Color.BLUE
    private var inactiveColor = Color.LTGRAY
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val density = resources.displayMetrics.density
        circleRadius *= density
        circleSpacing *= density
        activeWidth *= density
    }

    fun setItemCount(count: Int) {
        itemCount = count
        invalidate()
    }

    fun setActivePosition(position: Int) {
        activePosition = position
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = (circleRadius * 2 * itemCount + circleSpacing * (itemCount - 1)).toInt()
        val height = (circleRadius * 2).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startX = circleRadius
        val centerY = height / 2f

        for (i in 0 until itemCount) {
            paint.color = if (i == activePosition) indicatorColor else inactiveColor

            val cx = startX + i * (circleRadius * 2 + circleSpacing)

            if (i == activePosition) {
                canvas.drawRoundRect(
                    cx - activeWidth / 2,
                    centerY - circleRadius,
                    cx + activeWidth / 2,
                    centerY + circleRadius,
                    circleRadius,
                    circleRadius,
                    paint
                )
            } else {
                canvas.drawCircle(cx, centerY, circleRadius, paint)
            }
        }
    }
}
