package com.belomor.currenciesgraph.graph.extensions

import android.view.View
import android.util.TypedValue




fun View.getDpInFloat(dp : Float) : Float {
    val r = resources
    val px = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        r.displayMetrics
    )

    return px
}