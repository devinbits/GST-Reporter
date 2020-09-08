package com.testtube.gstreporter.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


@Suppress("UNCHECKED_CAST")
class AutoCompleteAdapter(
    context: Context,
    private var items: List<String>
) :
    ArrayAdapter<String?>(context, android.R.layout.simple_spinner_dropdown_item, items) {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view = convertView
        if (convertView == null) {
            view =
                LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        }
        val name = view!!.findViewById<TextView>(android.R.id.text1)
        name.text = getItem(position)
        return view
    }

    override fun getItem(position: Int): String? {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}