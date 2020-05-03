package com.testtube.gstreporter.views.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.testtube.gstreporter.R
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.utils.Constant
import kotlinx.android.synthetic.main.sale_item.view.*

class SalesListAdapter(var salesList: List<SaleItem>) :
    RecyclerView.Adapter<SalesListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val invoiceNumber: TextView = itemView.invoiceNumber
        private val date: TextView = itemView.date
        private val partyName: TextView = itemView.partyName
        private val invoiceAmount: TextView = itemView.invoiceAmount

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

        }

        @SuppressLint("SetTextI18n")
        fun bindValues(saleItem: SaleItem) {
            invoiceNumber.text = saleItem.invoiceNumber
            date.text = Common.getFormattedDate(Constant.dateFormat, saleItem.date)
            partyName.text = saleItem.partyName
            invoiceAmount.text = "₹ ${saleItem.totalInvoiceAmount}"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.sale_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return salesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindValues(salesList[position])
    }
}