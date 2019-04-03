package com.github.mikephil.charting.interfaces

interface IHasMinMax {
    val min: Float
    val max: Float
    operator fun contains(value: Float): Boolean {
        return value in min .. max
    }
    val range get() = max - min
}
