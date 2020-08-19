package com.testtube.gstreporter.views

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import com.testtube.gstreporter.R
import kotlinx.android.synthetic.main.date_month_text.view.*
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class DateFilterWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes), View.OnClickListener {

    private var sDate: Date? = null
    private var mShowDate: Boolean
    private var titleText: String? = "Select Date"

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.date_month_text, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DateFilterWidget,
            0, 0
        ).apply {
            try {
                mShowDate = getBoolean(R.styleable.DateFilterWidget_showDate, true)
                titleText = getString(R.styleable.DateFilterWidget_titleTextValue)
                val titleTextColor =
                    getColor(
                        R.styleable.DateFilterWidget_titleTextColorValue,
                        resources.getColor(R.color.colorAccent)
                    )
//                val titleTextSize = getDimension(R.styleable.DateFilterWidget_titleTextSizeInDpValue, 5f)
                if (titleText != null)
                    title.text = titleText
                title.setTextColor(titleTextColor)

                button.setOnClickListener(this@DateFilterWidget)

            } finally {
                recycle()
                setView()
            }
        }
    }

    private fun setView() {
        if (mShowDate) {
            date.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE
        } else {
            date.visibility = View.GONE
            divider.visibility = View.GONE
        }
    }

    fun hasDate(): Boolean {
        return mShowDate
    }

    fun setShowText(showDate: Boolean) {
        mShowDate = showDate
        invalidate()
        requestLayout()
        setView()
    }

    private fun openDateSelector() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = context?.let {
            DatePickerDialog(
                it,
                DatePickerDialog.OnDateSetListener { view, nyear, monthOfYear, dayOfMonth ->
                    val cal = Calendar.getInstance()
                    cal.set(nyear, monthOfYear, dayOfMonth,0,0,0)
                    date.text = dayOfMonth.toString()
                    this.month.text = DateFormatSymbols().months[(cal.time.month)]
                    sDate = cal.time
                },
                year,
                month,
                day
            )
        }
        dpd?.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDialogWithoutDateField() {
        val popup = PopupMenu(context, main)
        popup.menuInflater.inflate(R.menu.popup_month_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            val date: Date? = SimpleDateFormat("MMMM").parse(it.title.toString())
            val cal = Calendar.getInstance()
            cal.set(cal.get(Calendar.YEAR), date?.month!!, 1,0,0,0)
            this.month.text = it.title
            sDate = cal.time
            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    override fun onClick(v: View?) {
        if (mShowDate) {
            openDateSelector()
        } else
            showDialogWithoutDateField()
    }

    fun getDate() = sDate

    fun setDate(date: Date?) {
        sDate = date
    }
}