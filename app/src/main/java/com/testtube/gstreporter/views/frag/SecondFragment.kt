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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testtube.gstreporter.R
import com.testtube.gstreporter.databinding.FragmentSecondBinding
import com.testtube.gstreporter.model.Profile
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.utils.Constant
import com.testtube.gstreporter.viewmodel.NewSaleVM
import com.testtube.gstreporter.views.adapters.ImageRecyclerViewAdapter
import com.testtube.gstreporter.views.vInterface.RecyclerViewInterface
import kotlinx.android.synthetic.main.fragment_second.*
import kotlinx.android.synthetic.main.fragment_second.view.*
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(R.layout.fragment_second), RecyclerViewInterface {

    private lateinit var viewModel: NewSaleVM
    private var fileAbsPath: String? = "";
    private lateinit var rootView: View
    private var saleItem: SaleItem = SaleItem()
    private lateinit var imageRecyclerViewAdapter: ImageRecyclerViewAdapter
    private var profile: Profile = Profile()
    private lateinit var binding: FragmentSecondBinding

    private val args: SecondFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(NewSaleVM::class.java)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_second,
            container,
            false
        )
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rootView = view;
        if (args.sale != null) {
            saleItem = args.sale!!
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

        viewModel.invoiceNumber = saleItem.Invoice_Number
        viewModel.gstNumber = saleItem.Gst_Number
        viewModel.partyName = saleItem.Party_Name
        viewModel.taxableAmount = saleItem.Taxable_Amount.toString()
        viewModel.sGST = saleItem.sGST.toString()
        viewModel.cGST = saleItem.cGST.toString()
        viewModel.iGST = saleItem.iGST.toString()
        viewModel.tGST = saleItem.GST.toString()
        viewModel.totalInvoiceAmount = saleItem.Total_Invoice_Amount.toString()
        viewModel.date = Common.getFormattedDate(Constant.dateFormat, saleItem.Date)

        view.includeImageCheckBox.setOnClickListener {
            viewModel.includeImageCheckBox = it.includeImageCheckBox.isChecked
        }

        gst_number.addTextChangedListener { text ->
            viewModel.checkIsSameState()
        }

        t_GST.addTextChangedListener {
            if (!it.isNullOrBlank())
                calculateTotalAmount()
        }

        t_GST.addTextChangedListener { text: Editable? ->
            if (viewModel.isSameState.value == true) {
                val gst: Double = t_GST.text?.toString()?.toDoubleOrNull() ?: 0.0
                c_GST.setText("${gst / 2}")
                s_GST.setText("${gst / 2}")
                calculateTotalAmount()
            } else {
                c_GST.setText("0")
                s_GST.setText("0")
            }
        }

        viewModel.isSameState.observe(viewLifecycleOwner, androidx.lifecycle.Observer
        {
            if (it) {
                val gst: Double = t_GST.text?.toString()?.toDoubleOrNull() ?: 0.0
                c_GST.setText("${gst / 2}")
                s_GST.setText("${gst / 2}")
                i_GST.visibility = View.GONE
                i_GST.setText("")
                cGST_layout.visibility = View.VISIBLE
                sGST_layout.visibility = View.VISIBLE
            } else {
                i_GST.visibility = View.VISIBLE
                i_GST.setText(getString(R.string.tax_18))
                t_GST.setText(getString(R.string.tax_18))
                cGST_layout.visibility = View.GONE
                sGST_layout.visibility = View.GONE
            }
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

    private fun calculateTotalAmount() {
        val amount = taxable_amount?.text?.toString()?.toDoubleOrNull() ?: 0.0
        val gst = t_GST?.text.toString().toDoubleOrNull() ?: 0.0
        val totalAmount = ((amount * (gst * 0.01)) + amount).toBigDecimal()
        total_invoice_amount.setText("$totalAmount")
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
                viewModel.saveItem(saleItem)
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }
        }
    }

    override fun onClick(pos: Int) {
        fileAbsPath = Common.startPictureCaptureIntentFragment(this, 0)
    }

    override fun onClick(pos: Int, data: Any) {
        val filePath: String = data as String
        val action = SecondFragmentDirections.actionSecondFragmentToImageViewerFrag(filePath)
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
