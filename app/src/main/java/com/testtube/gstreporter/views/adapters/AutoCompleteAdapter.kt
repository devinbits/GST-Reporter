package com.testtube.gstreporter.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import java.util.*


class AutoCompleteAdapter(
    context: Context,
    private val items: List<String>
) :
    ArrayAdapter<String?>(context, android.R.layout.simple_spinner_dropdown_item, items),
    Filterable {

    private var suggestions = items

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
        return suggestions[position]
    }

    override fun getCount(): Int {
        return suggestions.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return myFilter
    }

    private val myFilter: Filter = object : Filter() {

        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            filterResults.values = if (charSequence == null || charSequence.isEmpty()) {
                items
            } else {
                items.filter {
                    it.toLowerCase(Locale.getDefault())
                        .contains(charSequence.toString().toLowerCase(Locale.getDefault()))
                }
            }
            return filterResults
        }

        override fun publishResults(
            charSequence: CharSequence?,
            filterResults: FilterResults
        ) {
            suggestions = filterResults.values as List<String>
            notifyDataSetChanged()
        }
    }
}