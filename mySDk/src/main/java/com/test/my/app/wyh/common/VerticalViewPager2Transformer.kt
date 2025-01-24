package com.test.my.app.wyh.common

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class VerticalViewPager2Transformer : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        val pageHeight = view.height
        when {
            position < -1 -> {
                view.alpha = 0f
            }

            position <= 1 -> {
                view.alpha = 1f
                view.translationX = pageWidth * -position
                val yPosition = position * pageHeight
                view.translationY = yPosition
            }

            else -> {
                view.alpha = 0f
            }
        }
    }

}