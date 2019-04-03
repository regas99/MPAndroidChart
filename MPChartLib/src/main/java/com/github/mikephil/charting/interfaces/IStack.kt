package com.github.mikephil.charting.interfaces

interface IStack : IHasMinMax {
    val elements : MutableList<IHasMinMax>  // provides an alias for the various implementations
    fun getIndexOf(mm : IHasMinMax) : Int = elements.indexOf(mm)

    val highest get() = elements.maxBy { it.max }?.max ?: max
    val lowest get() = elements.minBy { it.min }?.min ?: max
    val middle get() = (highest + lowest) / 2f

    val size get() = elements.size
    val isEmpty get() = elements.isEmpty()
}
