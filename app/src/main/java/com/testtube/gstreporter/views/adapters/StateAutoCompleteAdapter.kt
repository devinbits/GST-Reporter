package com.testtube.gstreporter.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import java.util.*
import kotlin.collections.ArrayList


@Suppress("UNCHECKED_CAST")
class StateAutoCompleteAdapter(
    context: Context,
    var items: List<String?>
) :
    ArrayAdapter<String?>(context, android.R.layout.simple_spinner_dropdown_item, items) {
    private val tempItems: List<String?> = items
    private val suggestions: MutableList<String?> = ArrayList()
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

    override fun getFilter(): Filter {
        return terminalNameFilter
    }

    private val terminalNameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            return (resultValue as String).trim()
        }

        override fun performFiltering(charSequence: CharSequence): FilterResults {
            return if (charSequence != null) {
                suggestions.clear()
                for (state in tempItems) {
                    if (state?.toLowerCase(Locale.getDefault())
                            ?.contains(charSequence.toString().toLowerCase(Locale.getDefault()))!!
                    ) {
                        suggestions.add(state)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            } else {
                val filterResults = FilterResults()
                filterResults.values = tempItems
                filterResults.count = tempItems.size
                filterResults
            }
        }

        override fun publishResults(
            charSequence: CharSequence,
            filterResults: FilterResults
        ) {
            if (filterResults.count > 0) {
                clear()
                val tempValues: ArrayList<String> =
                    filterResults.values as ArrayList<String>
                for (terminal in tempValues) {
                    add(terminal)
                }
                notifyDataSetChanged()
            } else {
                clear()
                notifyDataSetChanged()
            }
        }
    }
}