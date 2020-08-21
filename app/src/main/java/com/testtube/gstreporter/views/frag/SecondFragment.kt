package com.testtube.gstreporter.views.frag

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.testtube.gstreporter.R
import com.testtube.gstreporter.firestoreController.ItemCollectionAdapter
import com.testtube.gstreporter.model.Profile
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.utils.Constant
import com.testtube.gstreporter.utils.Constants
import com.testtube.gstreporter.views.adapters.ImageRecyclerViewAdapter
import com.testtube.gstreporter.views.vInterface.RecyclerViewInterface
import com.testtube.gstreporter.workers.FirebaseStorageFileUpload
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_second.*
import kotlinx.android.synthetic.main.fragment_second.view.*
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), RecyclerViewInterface {

    private var isSameState: Boolean = false
    private var fileAbsPath: String? = "";
    private lateinit var rootView: View
    private var saleItem: SaleItem = SaleItem()
    private lateinit var imageRecyclerViewAdapter: ImageRecyclerViewAdapter
    private lateinit var profile: Profile

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
        context?.let {
            Profile().getProfile(it).addOnSuccessListener {
                profile = it?.toObject(Profile::class.java) ?: Profile()
            }
        }

        view.invoiceNumber.setText(saleItem.Invoice_Number)
        view.date.setText(Common.getFormattedDate(Constant.dateFormat, saleItem.Date))
        view.date.setOnClickListener(View.OnClickListener { v ->
            openDateSelector();
        })
        view.save.setOnClickListener {
            this.view?.let { v -> validate(v) }
        }

        view.gstNumber.setText(saleItem.Gst_Number)
        view.partyName.setText(saleItem.Party_Name)
        view.taxableAmount.setText(saleItem.Taxable_Amount.toString())
        view.sGST.setText(saleItem.sGST.toString())
        view.cGST.setText(saleItem.cGST.toString())
        view.iGST.setText(saleItem.iGST.toString())
        view.tGST.setText(saleItem.GST.toString())
        view.totalInvoiceAmount.setText(saleItem.Total_Invoice_Amount.toString())
        view.includeImageCheckBox.setOnClickListener {
            if (!view.includeImageCheckBox.isChecked) {
                view.rv_container.visibility = View.GONE
            } else {
                view.rv_container.visibility = View.VISIBLE
            }
        }

        tGST.addTextChangedListener { text ->
            val len = text?.length ?: 0
            if (len != 15) {
                tGST.error = "Invalid GST Number"
                return@addTextChangedListener
            }
            val states = Constants.States.values()
            if (len in 1..2) {
                when (val code = text.toString().toIntOrNull()) {
                    null -> input_state.setText(states[0].name)
                    else -> {
                        if (code in 1..states.size)
                            isSameState = states[code].name.replace("_", " ") == profile.state
                    }
                }
            }
        }

        context?.let { context ->
            view.recyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            imageRecyclerViewAdapter = ImageRecyclerViewAdapter(context, this)
            view.recyclerView.adapter = imageRecyclerViewAdapter
        }

        gstNumber.addTextChangedListener { text ->
            val gst = text?.toString()
            context?.let { c ->
                Profile().getProfile(c).addOnCompleteListener {
                    when {
                        it.isSuccessful -> {
                            val gstNumber = it.result?.toObject(Profile::class.java)?.gstNumber

                        }
                    }
                }
            }
        }
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
                    rootView.date.setText(
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
        val imagePathList = imageRecyclerViewAdapter.getImagePathList()
        when {
            invoiceNumber.isBlank() -> {
                view.invoiceNumber.error = getString(R.string.required)
                return
            }
            gstNumber.isBlank() -> {
                view.gstNumber.error = getString(R.string.required)
                return
            }
            partyName.isBlank() -> {
                view.partyName.error = getString(R.string.required)
                return
            }
            taxableAmount.isBlank() -> {
                view.taxableAmount.error = getString(R.string.required)
                return
            }
            sGST.isBlank() -> {
                view.sGST.error = getString(R.string.required)
                return
            }
            cGST.isBlank() -> {
                view.cGST.error = getString(R.string.required)
                return
            }
            iGST.isBlank() -> {
                view.iGST.error = getString(R.string.required)
                return
            }
            tGST.isBlank() -> {
                view.tGST.error = getString(R.string.required)
                return
            }
            totalInvoiceAmount.isBlank() -> {
                view.totalInvoiceAmount.error = getString(R.string.required)
                return
            }
            else -> {
                saleItem = SaleItem(
                    saleItem.InvoiceId,
                    invoiceNumber,
                    gstNumber,
                    partyName,
                    taxableAmount.toDouble(),
                    date,
                    saleItem.sDate,
                    sGST.toDouble(),
                    cGST.toDouble(),
                    iGST.toDouble(),
                    tGST.toDouble(),
                    totalInvoiceAmount.toDouble()
//                    imagePathList.map { it -> it.split("/").last() }
                )
                context?.let { context ->
                    ItemCollectionAdapter(context).saveItem(saleItem)
                    if (includeImageCheckBox.isChecked)
                        for (path in imagePathList) {
                            val data = Data.Builder()
                                .putString("path", path)
                                .putString("inv", invoiceNumber)
                                .build();
                            val req = OneTimeWorkRequestBuilder<FirebaseStorageFileUpload>()
                                .setInputData(data)
                                .build()
                            WorkManager.getInstance(context).enqueue(req)
                        }
                }
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
            val lastPosition = imageRecyclerViewAdapter.addImagePath(fileAbsPath!!)
            recyclerView.scrollToPosition(lastPosition)
            fileAbsPath = null
        } else Common.showToast(context, R.string.image_capture_error)
    }
}
