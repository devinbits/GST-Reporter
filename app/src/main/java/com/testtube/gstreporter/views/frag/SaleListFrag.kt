package com.testtube.gstreporter.views.frag

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.testtube.gstreporter.R
import com.testtube.gstreporter.firestoreController.ItemCollectionAdapter
import com.testtube.gstreporter.model.Filter
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.utils.DocumentExportService
import com.testtube.gstreporter.utils.MyItemAnimator
import com.testtube.gstreporter.viewmodel.SaleListVM
import com.testtube.gstreporter.views.adapters.SalesListAdapter
import com.testtube.gstreporter.views.vInterface.RecyclerViewInterface
import com.testtube.gstreporter.views.vInterface.RecyclerViewInterface.Actions
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.fragment_first.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SaleListFrag : Fragment(), RecyclerViewInterface,
    androidx.appcompat.widget.SearchView.OnQueryTextListener, DateFilterView.OnDateFilter {

    private lateinit var viewModel: SaleListVM
    private var filter: Filter? = null
    private var saleList: MutableList<SaleItem> = ArrayList()
    private lateinit var saleListAdapter: SalesListAdapter
    private lateinit var itemCollectionAdapter: ItemCollectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(SaleListVM::class.java)
        fab_new_form.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        filterView.setOnClickListener {
            fragmentManager?.let { it1 ->
                DateFilterView.getInstance(this, filter).show(it1, "dialog")
            }
        }

        share.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_progressDialog)
            context?.let { it1 ->
                val customHeaders: Array<String> = arrayOf("Bill", "Date","GST_Percentage","Party_Name",
                    "Party_GSTN","Total_Invoice_Value","Bill_Amount","cGST","sGST","iGST","Total_GST")

                CoroutineScope(Dispatchers.IO).launch {
                    val sheet = DocumentExportService<SaleItem>().createSheet(
                        it1,
                        saleList,
                        customHeaders
                    )?.await()
                    withContext(Dispatchers.Main) {
                        findNavController().navigateUp()
                        if (sheet != null) {
                            Common.sendEmail(it1, sheet)
                        } else
                            Common.showToast(it1, "Error occurred in exporting data!")
                    }
                }
            }
        }

        docRecyclerView.layoutManager = LinearLayoutManager(context);
        saleListAdapter = SalesListAdapter(saleList, this)
        docRecyclerView.adapter = saleListAdapter
        docRecyclerView.itemAnimator = MyItemAnimator()

        viewModel.getRecentDocuments().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            saleList.clear()
            saleList.addAll(it)
            updateSaleListView()
        })

        viewModel.loading.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                progress_circular?.visibility = View.VISIBLE
                fab_new_form.visibility = View.GONE
            } else {
                fab_new_form.visibility = View.VISIBLE
                progress_circular?.visibility = View.GONE
            }
        })
    }

    override fun onAction(pos: Int, actionId: Actions, data: Any) {
        when (actionId) {
            Actions.Delete -> {
                viewModel.deleteSale(data as String)
                Handler().postDelayed({
                    saleList.removeAt(pos)
                    updateSaleListView()
                }, 300)
            }
            Actions.Edit -> {
                val saleItem: SaleItem = data as SaleItem
                val actionFirstFragmentToSecondFragment =
                    SaleListFragDirections.actionFirstFragmentToSecondFragment(saleItem)
                findNavController().navigate(actionFirstFragmentToSecondFragment);
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        saleListAdapter.filter(newText?.toLowerCase(Locale.getDefault()))
        return false
    }

    override fun onApply(filter: Filter?) {
        view?.progress_circular?.visibility = View.VISIBLE
        if (filter == null) {
            view?.progress_circular?.visibility = View.GONE
            filterView.setBackgroundResource(android.R.color.transparent)
            viewModel.getRecentDocuments()
            return
        }
        val startDate = filter.startDate
        val endDate = filter.endDate
        val filterType = filter.filterType
        if (filter.isRange) {
            if (startDate != null && endDate != null) {
                itemCollectionAdapter.getDocuments(10, startDate, endDate).addOnCompleteListener {
                    when (it.isSuccessful) {
                        true -> {
                            viewModel.sales.postValue(it.result?.documents?.mapNotNull { snapshot ->
                                snapshot.toObject(SaleItem::class.java)
                            })
                        }
                    }
                    filterView.setBackgroundResource(R.drawable.circile_filled)
                }
            }
        } else if (startDate != null) {
            itemCollectionAdapter.getDocuments(100, startDate, filterType).addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        viewModel.sales.postValue(it.result?.documents?.mapNotNull { snapshot ->
                            snapshot.toObject(SaleItem::class.java)
                        })
                    }
                    else -> {
                        Log.e(SaleListFrag::class.simpleName, "onApply: ", it.exception)
                    }
                }
                filterView.setBackgroundResource(R.drawable.circile_filled)
            }
        } else {
            view?.progress_circular?.visibility = View.GONE
            filterView.setBackgroundResource(android.R.color.transparent)
        }
        this.filter = filter
    }

    private fun updateSaleListView() {
        saleListAdapter.notifyDataSetChanged()
        if (saleList.isEmpty()) {
            view?.searchView?.visibility = View.INVISIBLE
            view?.share?.visibility = View.GONE
        } else {
            view?.share?.visibility = View.VISIBLE
            view?.searchView?.visibility = View.VISIBLE
            view?.searchView?.setOnQueryTextListener(this)
        }
    }

}
