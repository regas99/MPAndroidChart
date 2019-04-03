package com.github.mikephil.charting.data

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.github.mikephil.charting.interfaces.IHasMinMax
import com.github.mikephil.charting.utils.pp

/**
 * Subclass of Entry that holds a pair for one item in a StackChart.
 * A Stack Item is drawn as a box:
 *
 *         +-----+  <-- max
 *         |     |
 *         |     |  <-- value
 *         |     |
 *         +-----+  <-- min
 *
 *  The item can be drawn with an optional icon in the center,
 *  and with min and max values (controlled by showValues).
 *
 */
class StackItem : Entry, IHasMinMax, Comparable<StackItem> {

    override val min: Float
    override val max: Float
    var color: Int
    var drawIcon: Boolean
    var drawBorder: Boolean
    var borderColor: Int
    var drawValues: Boolean
    var valueColor: Int
    
    constructor( x: Float,
                 min: Float,
                 max: Float,
                 color: Int = Color.rgb(122, 242, 84),
                 data: Any? = null,
                 icon: Drawable? = null,
                 drawIcon: Boolean = icon != null,
                 drawBorder: Boolean = true,
                 borderColor: Int = Color.BLACK,
                 drawValues: Boolean = true,
                 valueColor: Int = Color.BLUE)
        : super(x, (min + max) / 2f, icon, data) {
            this.x = x
            this.min = Math.min(min, max)  // swap min and max if inverted
            this.max = Math.max(max, min)
            this.color = color
            this.data = data
            this.icon = icon
            this.drawIcon = drawIcon
            this.drawBorder = drawBorder
            this.borderColor = borderColor
            this.drawValues = drawValues
            this.valueColor = valueColor
        }

    /**
     * compare by min and max values
     */
    override fun compareTo(other: StackItem) : Int {
        if (min.compareTo(other.min) != 0) return min.compareTo(other.min)
        return max.compareTo(other.max)
}

    override fun copy() : StackItem {
        return StackItem(x, min, max, color, data, icon, drawIcon, drawBorder, borderColor,
                drawValues, valueColor)
    }

    override fun toString() = "StackItem: x= ${x.pp(1)} y= ${min.pp(1)} -> ${max.pp(1)}"

}