package com.github.mikephil.charting.utils

import android.graphics.RectF
import android.util.Log
import com.github.mikephil.charting.interfaces.IHasMinMax

/**
 * Kotlin helper functions
 */

// pretty print
fun Float.pp(d : Int = 1) = String.format("%.${d}f", this)
fun Double.pp(d : Int = 1) = String.format("%.${d}f", this)
fun RectF.pp(d: Int = 1) : String = "${this.left.pp(d)} ${this.top.pp(d)} ${this.right.pp(d)} ${this.bottom.pp(d)}"
fun FloatArray.pp(d: Int = 1) : String = "${this.left().pp(d)} ${this.top().pp(d)} ${this.right().pp(d)} ${this.bottom().pp(d)}"
fun Int.pp(w: Int) = String.format("%0${w}d", this)

// alias side names for Float[]
fun FloatArray.left() = this[0]
fun FloatArray.top() = this[1]
fun FloatArray.right() = this[2]
fun FloatArray.bottom() = this[3]

// transformer utilities
fun MPPointD.recycle() {
    MPPointD.recycleInstance(this)
}
fun MPPointF.recycle() {
    MPPointF.recycleInstance(this)
}

/**
 * Runs a binary search to find the IHasMinMax instance containing target.
 *
 * @param f - what to find
 * @return - index of instance, or -1 if no match
 */
fun List<IHasMinMax>.getIndexContaining(f: Float): Int {
    if (size == 0) return -1
    if (size == 1) return if (this[0].contains(f)) 0 else -1
    if (f < this[0].min) return -1
    if (f > this[size - 1].max) return - 1

    var lo = 0
    var hi = size - 1
    while (lo <= hi) {
        val mid = (lo + hi) / 2
        if (this[mid].contains(f)) return mid
        if (f < this[mid].min)
            hi = mid - 1
        else if (f > this[mid].max)
            lo = mid + 1
        else {
            Log.d("Error", "List<IHasMinMax>.getIndexContaining could not parse $f")
            return -1
        }
    }
    Log.d("Error", "List<IHasMinMax>.getIndexContaining could not parse $f")
    return -1
}