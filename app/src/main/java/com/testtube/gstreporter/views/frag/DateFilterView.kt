package com.testtube.gstreporter.views.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.testtube.gstreporter.R
import com.testtube.gstreporter.model.Filter
import com.testtube.gstreporter.utils.Constants
import kotlinx.android.synthetic.main.dialog_date_filter.*
import kotlinx.android.synthetic.main.dialog_date_filter.view.*

class DateFilterView : DialogFragment() {

    private lateinit var listener: OnDateFilter
    private var isDateRange: Boolean = false
    private var filter: Filter? = null

    companion object {
        @JvmStatic
        fun getInstance(listener: OnDateFilter, filter: Filter?): DateFilterView {
            val dialog = DateFilterView()
            dialog.listener = listener
            dialog.filter = filter
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_date_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.tabs.setOnCheckedChangeListener { _, _ ->
            date_start.setShowText(date_text.isChecked)
            date_end.setShowText(date_text.isChecked)
        }
        view.date_range.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> date_end.visibility = View.VISIBLE
                else -> {
                    date_end.visibility = View.GONE
                    date_end.setDate(null)
                }
            }
            isDateRange = isChecked
        }
        apply.setOnClickListener {
            listener.onApply(
                Filter(
                    date_start.getDate(),
                    date_end.getDate(),
                    isDateRange,
                    if (date_start.hasDate()) Constants.FilterType.Date else Constants.FilterType.Month
                )
            )
            dismiss()
        }
        clear.setOnClickListener {
            listener.onApply(null)
            dismiss()
        }
    }

    interface OnDateFilter {
        fun onApply(filter: Filter?)
    }

}