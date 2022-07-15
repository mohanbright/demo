package com.journalmetro.app.ui.common.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.journalmetro.app.R


class CustomArrayAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val values: List<String>, private val hint: String,private  val allowToSelectHint:Boolean = false
) : ArrayAdapter<String>(context, layoutResource, values) {

    override fun getItem(position: Int): String = values[position]


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        val row = LayoutInflater.from(context)
            .inflate(R.layout.spinner_drop_down_item, parent, false) as TextView
        row.text = getItem(position)


        // make hint item color gray
        if (!allowToSelectHint && getItem(position) == hint) {
            row.setTextColor(Color.LTGRAY)
        } else {
            row.setTextColor(context.getColor(R.color.textDark))
        }

        return row
    }

    override fun isEnabled(position: Int): Boolean {
        // disable first item
        // first item is display as hint
        return allowToSelectHint || getItem(position) != hint
    }


}