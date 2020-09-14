package com.testtube.gstreporter.views.frag

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testtube.gstreporter.R
import com.testtube.gstreporter.databinding.NewSaleFragBinding
import com.testtube.gstreporter.model.Profile
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.utils.Constant
import com.testtube.gstreporter.viewmodel.NewSaleVM
import com.testtube.gstreporter.views.adapters.ImageRecyclerViewAdapter
import com.testtube.gstreporter.views.vInterface.RecyclerViewInterface
import kotlinx.android.synthetic.main.new_sale_frag.*
import kotlinx.android.synthetic.main.new_sale_frag.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class NewSaleFrag : Fragment(R.layout.new_sale_frag), RecyclerViewInterface {

    private lateinit var viewModel: NewSaleVM
    private var fileAbsPath: String? = "";
    private lateinit var rootView: View
    private var saleItem: SaleItem = SaleItem()
    private lateinit var imageRecyclerViewAdapter: ImageRecyclerViewAdapter
    private var profile: Profile = Profile()
    private lateinit var binding: NewSaleFragBinding

    private val args: NewSaleFragArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(NewSaleVM::class.java)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.new_sale_frag,
            container,
            false
        )
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rootView = view;
        if (args.sale != null ) {
            saleItem = args.sale!!
        }
        savedInstanceState?.let {
            saleItem = it["SaleItem"] as SaleItem
        }
        initViews(view)
    }

    private fun initViews(view: View) {
        viewModel.profile.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            profile = it
            viewModel.checkIsSameState()
        })

//        view.date_text.setText(Common.getFormattedDate(Constant.dateFormat, saleItem.Date))
        view.date_text.setOnClickListener { _ ->
            openDateSelector()
        }
        view.save.setOnClickListener {
            validate()
        }

        viewModel.invoiceNumber = saleItem.Bill
        viewModel.gstNumber = saleItem.Party_GSTN
        viewModel.partyName = saleItem.Party_Name
        viewModel.taxableAmount = saleItem.Bill_Amount.toString()
        viewModel.sGST = saleItem.sGST.toString()
        viewModel.cGST = saleItem.cGST.toString()
        viewModel.iGST = saleItem.iGST.toString()
        viewModel.tGST = saleItem.GST_Percentage.toString()
        viewModel.totalInvoiceAmount = saleItem.Total_Invoice_Value.toString()
        viewModel.date = Common.getFormattedDate(Constant.dateFormat, saleItem.Date)

        view.includeImageCheckBox.setOnClickListener {
            viewModel.includeImageCheckBox = it.includeImageCheckBox.isChecked
        }

        gst_number.addTextChangedListener { _ ->
            viewModel.checkIsSameState()
        }

        t_GST.addTextChangedListener {
            if (!it.isNullOrBlank())
                calculateTotalAmount()
        }

        t_GST.addTextChangedListener { _: Editable? ->
            calculateTotalAmount()
        }

        viewModel.isSameState.observe(viewLifecycleOwner, androidx.lifecycle.Observer
        {
            if (it) {
                cGST_layout.visibility = View.VISIBLE
                sGST_layout.visibility = View.VISIBLE
                iGST_layout.visibility = View.GONE
            } else {
                iGST_layout.visibility = View.VISIBLE
                cGST_layout.visibility = View.GONE
                sGST_layout.visibility = View.GONE
            }
            calculateTotalAmount()
        })

        taxable_amount.addTextChangedListener { text: Editable? ->
            calculateTotalAmount()
        }

        context?.let { context ->
            view.recyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            imageRecyclerViewAdapter = ImageRecyclerViewAdapter(context, this)
            view.recyclerView.adapter = imageRecyclerViewAdapter
        }
    }

    private fun calculateTotalAmount(): Double {
        val amount = taxable_amount?.text?.toString()?.toDoubleOrNull() ?: 0.0
        val gst = t_GST?.text.toString().toDoubleOrNull() ?: 0.0
        val totalAmount = ((amount * (gst * 0.01)) + amount).toBigDecimal()
        total_invoice_amount.setText("$totalAmount")
        if (viewModel.isSameState.value == true) {
            c_GST.setText("${(amount * (gst * 0.01)) / 2}")
            s_GST.setText("${(amount * (gst * 0.01)) / 2}")
            i_GST.setText("0")
        } else {
            i_GST.setText("${amount * (gst * 0.01)}")
            c_GST.setText("0")
            s_GST.setText("0")
        }
        total_GST.setText("${amount * (gst * 0.01)}")

        return amount
    }

    override fun onAction(pos: Int, actionId: RecyclerViewInterface.Actions, data: Any) {
        TODO("Not yet implemented")
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
                    cal.set(nyear, monthOfYear, dayOfMonth, 0, 0, 0)
                    saleItem.Date = cal.time
                    saleItem.sDate = Common.getSDate(cal.time)
                    rootView.date_text.setText(
                        Common.getFormattedDate(
                            Constant.dateFormat,
                            saleItem.Date
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

    private fun validate() {
        saleItem.images = imageRecyclerViewAdapter.getImagePathList()
        when {
            viewModel.invoiceNumber.isBlank() -> {
                invoice_number.error = getString(R.string.required)
                return
            }
            viewModel.gstNumber.isBlank() || gst_number.text?.length != 15 -> {
                gstNumber_layout.error = getString(R.string.required)
                return
            }
            viewModel.partyName.isBlank() -> {
                party_name.error = getString(R.string.required)
                return
            }
            viewModel.taxableAmount.isBlank() || (viewModel.taxableAmount.toDoubleOrNull()
                ?: 0.0) <= 0 -> {
                taxable_amount.error = getString(R.string.required)
                return
            }
            viewModel.tGST.isBlank() || (viewModel.tGST.toDoubleOrNull() ?: 0.0) <= 0 -> {
                t_GST.error = getString(R.string.required)
                return
            }
            viewModel.totalInvoiceAmount.isBlank() || (viewModel.totalInvoiceAmount.toDoubleOrNull()
                ?: 0.0) <= 0 -> {
                total_invoice_amount.error = getString(R.string.required)
                return
            }
            else -> {
                saveGstPartyInfo(saleItem.Party_GSTN, saleItem.Party_Name)
                viewModel.saveItem(saleItem)
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }
        }
    }

    private fun saveGstPartyInfo(gstIN: String, partyName: String) {
        lifecycleScope.async(Dispatchers.IO) {
            viewModel.saveGstPartyInfo(gstIN, partyName)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("SaleItem", viewModel.getSaleItem(saleItem))
        super.onSaveInstanceState(outState)
    }

    override fun onClick(pos: Int) {
        fileAbsPath = Common.startPictureCaptureIntentFragment(this, 0)
    }

    override fun onClick(pos: Int, data: Any) {
        val filePath: String = data as String
        val action = NewSaleFragDirections.actionSecondFragmentToImageViewerFrag(filePath)
        findNavController().navigate(action);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            fileAbsPath?.let {
                val pos = imageRecyclerViewAdapter.addImagePath(it)
                recyclerView.smoothScrollToPosition(pos)
            }
            fileAbsPath = null
        } else Common.showToast(context, R.string.image_capture_error)
    }
}
