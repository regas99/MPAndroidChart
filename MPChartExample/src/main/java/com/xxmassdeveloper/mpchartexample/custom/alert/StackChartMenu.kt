package com.xxmassdeveloper.mpchartexample.custom.alert

import android.app.Activity
import android.app.Dialog
import android.graphics.Point
import android.util.Log
import android.view.*
import android.widget.CompoundButton
import android.widget.TextView
import com.github.mikephil.charting.charts.StackChart
import com.google.android.material.chip.Chip
import com.xxmassdeveloper.mpchartexample.R
import kotlinx.android.synthetic.main.stackchart_menu.*

val TAG = "StackChartMenu"
val logEnabled = true

class StackChartMenu(activity: Activity, val chart: StackChart, val menu: Menu) {

    var dialog: Dialog

    lateinit var valuesGroup: ViewGroup
    lateinit var iconsGroup: ViewGroup
    lateinit var shadowsGroup: ViewGroup

    lateinit var valuesEntry: Chip
    lateinit var valuesUnit: Chip
    lateinit var valuesItem: Chip

    lateinit var iconsEntry: Chip
    lateinit var iconsUnit: Chip
    lateinit var iconsItem: Chip

    lateinit var shadowsEntry: Chip
    lateinit var shadowsUnit: Chip
    lateinit var shadowsItem: Chip

    var settingChipStatus = false

    val checkListener = object : CompoundButton.OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            buttonView ?: return
            if (settingChipStatus) return
            Log.i(TAG, "onCheckedChanged: " + buttonView.getTag(R.string.menu_group_tag).toString() + " -- " + nameOfView(buttonView))

            val group = buttonView.getTag(R.string.menu_group_tag) as GROUP ?: return

            toggleDisplay(group, buttonView, isChecked)

        }
    }


    val clickListener = object : View.OnClickListener {

        override fun onClick(v: View?) {
            v ?: return
            if (logEnabled) Log.i(TAG, "onClick: " + nameOfView(v))

            if (v.id == R.id.dismiss_button) {
                closeDialog()
                chart.invalidate()
            }
        }
    }

    init {
        dialog = Dialog(activity)

        with(dialog) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.stackchart_menu)

            val layout = findViewById<View>(android.R.id.content)
            layout.setToScreenWidth(activity)

            val attr = window.attributes
            attr.gravity = Gravity.TOP or Gravity.LEFT
            attr.flags = attr.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()

            valuesGroup = findViewById(R.id.display_values)
            iconsGroup = findViewById(R.id.display_icons)
            shadowsGroup = findViewById(R.id.display_shadows)

            valuesGroup.findViewById<TextView>(R.id.menu_tv).text = "Values"
            iconsGroup.findViewById<TextView>(R.id.menu_tv).text = "Icons"
            shadowsGroup.findViewById<TextView>(R.id.menu_tv).text = "Shadows"

            valuesEntry = valuesGroup.findViewById<Chip>(R.id.display_entry)
            valuesUnit  = valuesGroup.findViewById<Chip>(R.id.display_unit)
            valuesItem  = valuesGroup.findViewById<Chip>(R.id.display_item)

            iconsEntry = iconsGroup.findViewById<Chip>(R.id.display_entry)
            iconsUnit  = iconsGroup.findViewById<Chip>(R.id.display_unit)
            iconsItem  = iconsGroup.findViewById<Chip>(R.id.display_item)

            shadowsEntry = shadowsGroup.findViewById<Chip>(R.id.display_entry)
            shadowsUnit  = shadowsGroup.findViewById<Chip>(R.id.display_unit)
            shadowsItem  = shadowsGroup.findViewById<Chip>(R.id.display_item)
            shadowsItem.setDisabled(false)

            valuesGroup.setupGroup(GROUP.VALUES)
            iconsGroup.setupGroup(GROUP.ICONS)
            shadowsGroup.setupGroup(GROUP.SHADOWS)

            dismiss_button.setOnClickListener(clickListener)
        }
    }

    fun setDrawStates(group: GROUP, entry: Boolean, unit: Boolean, item: Boolean) {
        settingChipStatus = true

        when (group) {
            GROUP.VALUES -> {
                valuesEntry.setChecked(entry)
                valuesUnit.setChecked(unit)
                valuesItem.setChecked(item)
            }
            GROUP.ICONS -> {
                iconsEntry.setChecked(entry)
                iconsUnit.setChecked(unit)
                iconsItem.setChecked(item)
            }
            GROUP.SHADOWS -> {
                shadowsEntry.setChecked(entry)
                shadowsUnit.setChecked(unit)
                iconsItem.setChecked(item)
            }
        }

        settingChipStatus = false
    }

    fun Chip.setCheckState(isChecked: Boolean) {
        if (isChecked) {
            checkedIcon = resources.getDrawable(R.drawable.on, context.theme)
            chipIcon = null
            //isChecked = true
        } else {
            checkedIcon = null
            chipIcon = resources.getDrawable(R.drawable.off, context.theme)
            isChipIconVisible = true
        }
    }

    fun Chip.setDisabled(isChecked: Boolean) {
        isCheckable = false
        checkedIcon = resources.getDrawable( if (isChecked) R.drawable.on else R.drawable.off, context.theme)
        isCheckedIconVisible = true
    }

    fun showDialog() = dialog.show()

    fun closeDialog() {
        menu.close()
        dialog.dismiss()
    }

    fun View.setupGroup(group: GROUP) {
        if ( !(this is ViewGroup)) return
        for (v in (0 until childCount).map { getChildAt(it) }) {
            when (v) {
                is ViewGroup -> v.setupGroup(group)
                is Chip -> {
                    v.setTag(R.string.menu_group_tag, group)
                    v.setOnCheckedChangeListener(checkListener)
                }
            }
        }
    }

    fun toggleDisplay(group: GROUP, view: View, isChecked: Boolean) {
        val sets = chart.data.sets
        when (group) {
            GROUP.VALUES -> {
                when (view.id) {
                    R.id.display_entry -> sets.map { it.entries.map { it.drawValues = isChecked } }
                    R.id.display_unit -> sets.map { it.entries.map { it.units.map { it.drawValues = isChecked } } }
                    R.id.display_item -> sets.map { it.entries.map { it.units.map { it.items.map { it.drawValues = isChecked } } } }
                }
            }
            GROUP.ICONS -> {
                when (view.id) {
                    R.id.display_entry -> sets.map { it.entries.map { it.drawIcon = isChecked }}
                    R.id.display_unit -> sets.map { it.entries.map { it.units.map { it.drawIcon = isChecked }}}
                    R.id.display_item -> sets.map { it.entries.map { it.units.map { it.items.map { it.drawIcon = isChecked }}}}
                }
            }
            GROUP.SHADOWS -> {
                when (view.id) {
                    R.id.display_entry -> sets.map { it.entries.map { it.drawShadow = isChecked }}
                    R.id.display_unit -> sets.map { it.entries.map { it.units.map { it.drawShadows = isChecked }}}
                    // R.id.display_item -> sets.map { it.entries.map { it.units.map { it.items.map { it.drawShadow = onOff }}}}
                }
            }
        }

        (view as Chip).setCheckState(isChecked)
        chart.invalidate()
    }

    private fun nameOfView(view: View) : String {
        val path = view.resources.getResourceName(view.id)
        val name = path.drop(path.indexOf(":id/") + 4)
        return name
    }

    private fun getScreenWidth(activity: Activity) : Int {
        val display = activity.getWindowManager()?.getDefaultDisplay()
        val size = Point()
        display?.getSize(size)
        return size.x
    }

    private fun View.setToScreenWidth(activity: Activity) {
        layoutParams.width = getScreenWidth(activity)
    }


    enum class GROUP { VALUES, ICONS, SHADOWS, CAPS, HIGHLIGHTS }

    enum class CHIP_STATE { ON, OFF, DISABLED }

}