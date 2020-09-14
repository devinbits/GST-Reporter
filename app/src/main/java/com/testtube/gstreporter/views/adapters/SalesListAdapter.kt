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

    private val rawItems: List<SaleItem> = salesList

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val invoiceNumber: TextView = itemView.invoice_number
        private val date: TextView = itemView.date_text
        private val partyName: TextView = itemView.party_name
        private val invoiceAmount: TextView = itemView.invoiceAmount
        private val deleteSaleItem: View = itemView.deleteButton

        @SuppressLint("SetTextI18n")
        fun bindValues(
            saleItem: SaleItem,
            listener: RecyclerViewInterface
        ) {
            invoiceNumber.text = saleItem.Bill
            date.text = Common.getFormattedDate(Constant.dateFormat, saleItem.Date)
            partyName.text = saleItem.Party_Name
            invoiceAmount.text = "₹ ${saleItem.Total_Invoice_Value}"
            deleteSaleItem.setOnClickListener {
                listener.onAction(
                    adapterPosition,
                    Actions.Delete,
                    saleItem.InvoiceId
                )
            }
            itemView.setOnClickListener {
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
                saleItem.Bill.contains(query)
                        || Common.getFormattedDate(Constant.dateFormat, saleItem.Date)
                    .toLowerCase(Locale.getDefault())
                    .contains(query)
                        || saleItem.Party_GSTN.toLowerCase(Locale.getDefault()).contains(query)
                        || saleItem.Party_Name.toLowerCase(Locale.getDefault()).contains(query)
                        || saleItem.Total_Invoice_Value.toString().contains(query)
            }
        else rawItems
        notifyDataSetChanged()
    }
}