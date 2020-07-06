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
import com.testtube.gstreporter.views.vInterface.RecyclerViewInterface
import com.testtube.gstreporter.views.vInterface.RecyclerViewInterface.Actions
import kotlinx.android.synthetic.main.sale_item.view.*
import java.util.*

class SalesListAdapter(
    private var salesList: List<SaleItem>, private var listener: RecyclerViewInterface
) : RecyclerView.Adapter<SalesListAdapter.MyViewHolder>() {

    val rawItems: List<SaleItem> = salesList

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val invoiceNumber: TextView = itemView.invoiceNumber
        private val date: TextView = itemView.date
        private val partyName: TextView = itemView.partyName
        private val invoiceAmount: TextView = itemView.invoiceAmount
        private val editSaleItem: View = itemView.editButton
        private val deleteSaleItem: View = itemView.deleteButton

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
//            mListener?.onClick(adapterPosition, mSaleItem)
        }

        @SuppressLint("SetTextI18n")
        fun bindValues(
            saleItem: SaleItem,
            listener: RecyclerViewInterface
        ) {
            invoiceNumber.text = saleItem.invoiceNumber
            date.text = Common.getFormattedDate(Constant.dateFormat, saleItem.date)
            partyName.text = saleItem.partyName
            invoiceAmount.text = "₹ ${saleItem.totalInvoiceAmount}"
            deleteSaleItem.setOnClickListener {
                listener.onAction(adapterPosition, Actions.Delete, saleItem.invoiceId.toString())
            }
            editSaleItem.setOnClickListener {
                listener.onAction(adapterPosition, Actions.Edit, saleItem)
            }
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
        holder.bindValues(salesList[position], listener)
    }

    fun filter(query: String?) {
        salesList = if (query?.length!! > 0)
            rawItems.filter { saleItem ->
                saleItem.invoiceNumber.contains(query)
                        || Common.getFormattedDate(Constant.dateFormat, saleItem.date)
                    .toLowerCase(Locale.getDefault())
                    .contains(query)
                        || saleItem.gstNumber.toLowerCase(Locale.getDefault()).contains(query)
                        || saleItem.partyName.toLowerCase(Locale.getDefault()).contains(query)
                        || saleItem.totalInvoiceAmount.toString().contains(query)
            }
        else rawItems
        notifyDataSetChanged()
    }
}