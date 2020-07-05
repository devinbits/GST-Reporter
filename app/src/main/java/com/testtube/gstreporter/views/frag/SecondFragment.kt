package com.testtube.gstreporter.views.frag

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.testtube.gstreporter.R
import com.testtube.gstreporter.firestoreController.ItemCollectionAdapter
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.utils.Constant
import kotlinx.android.synthetic.main.fragment_second.view.*
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private lateinit var rootView: View
    private var saleItem: SaleItem = SaleItem()

    val args: SecondFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view;
        if (args.sale != null) {
            saleItem = args.sale!!
        }
        initViews(view)
    }

    private fun initViews(view: View) {
        view.invoiceNumber.setText(saleItem.invoiceNumber)
        view.date.setText(Common.getFormattedDate(Constant.dateFormat, saleItem.date))
        view.date.setOnClickListener(View.OnClickListener { v ->
            openDateSelector();
        })
        view.save.setOnClickListener {
            this.view?.let { v -> validate(v) }
        }

        view.gstNumber.setText(saleItem.gstNumber)
        view.partyName.setText(saleItem.partyName)
        view.taxableAmount.setText(saleItem.taxableAmount.toString())
        view.sGST.setText(saleItem.sGST.toString())
        view.cGST.setText(saleItem.cGST.toString())
        view.iGST.setText(saleItem.iGST.toString())
        view.tGST.setText(saleItem.tGST.toString())
        view.totalInvoiceAmount.setText(saleItem.totalInvoiceAmount.toString())
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
                    saleItem.date = Date(year, monthOfYear, dayOfMonth)
                    rootView.date.setText(
                        Common.getFormattedDate(
                            Constant.dateFormat,
                            saleItem.date
                        )
                    )
                },
                year,
                month,
                day
            )
        }
        dpd?.show()
    }

    private fun validate(view: View) {
        val invoiceNumber = view.invoiceNumber.text.toString()
        val gstNumber = view.gstNumber.text.toString()
        val partyName = view.partyName.text.toString()
        val taxableAmount = view.taxableAmount.text.toString()
        val date = Date(view.date.text.toString())
        val sGST = view.sGST.text.toString()
        val cGST = view.cGST.text.toString()
        val iGST = view.iGST.text.toString()
        val tGST = view.tGST.text.toString()
        val totalInvoiceAmount = view.totalInvoiceAmount.text.toString()

        when {
            invoiceNumber.isBlank() -> return
            gstNumber.isBlank() -> return
            partyName.isBlank() -> return
            taxableAmount.isBlank() -> return
            sGST.isBlank() -> return
            cGST.isBlank() -> return
            iGST.isBlank() -> return
            tGST.isBlank() -> return
            totalInvoiceAmount.isBlank() -> return
            else -> {
                saleItem = SaleItem(
                    saleItem.invoiceId,
                    invoiceNumber,
                    gstNumber,
                    partyName,
                    taxableAmount.toDouble(),
                    date,
                    sGST.toDouble(),
                    cGST.toDouble(),
                    iGST.toDouble(),
                    tGST.toDouble(),
                    totalInvoiceAmount.toDouble()
                )
                context?.let { context -> ItemCollectionAdapter(context).saveItem(saleItem) }
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }
        }
    }
}
