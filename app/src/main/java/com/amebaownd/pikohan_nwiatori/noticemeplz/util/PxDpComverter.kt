package com.amebaownd.pikohan_nwiatori.noticemeplz.util

import android.content.Context

object PxDpComverter {
    fun dp2Px(dp:Float,context: Context):Float{
        val metrics = context.resources.displayMetrics
        return dp * metrics.density
    }

    fun px2Dp(px:Int,context:Context):Float{
        val metrics = context.resources.displayMetrics
        return px / metrics.density
    }
}