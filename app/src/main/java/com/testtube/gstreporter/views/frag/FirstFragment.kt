package com.testtube.gstreporter.views.frag

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.testtube.gstreporter.R
import com.testtube.gstreporter.firestoreController.ItemCollectionAdapter
import com.testtube.gstreporter.model.Filter
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.utils.DocumentExportService
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
class FirstFragment : Fragment(), RecyclerViewInterface,
    androidx.appcompat.widget.SearchView.OnQueryTextListener, DateFilterView.OnDateFilter {

    private var filter: Filter? = null
    private var saleList: MutableList<SaleItem> = ArrayList()
    private lateinit var saleListAdapter: SalesListAdapter
    private lateinit var itemCollectionAdapter: ItemCollectionAdapter
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(context)
        view.fab_new_form.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        view.filterView.setOnClickListener {
            fragmentManager?.let { it1 ->
                DateFilterView.getInstance(this, filter).show(it1, "dialog")
            }
        }

        view.export.setOnClickListener {
            showProgress("Generating file. Please Wait...")
            context?.let { it1 ->
                val exclude = ArrayList<String>()
                exclude.add("Invoice_Id")
                exclude.add("sDate")
                CoroutineScope(Dispatchers.IO).launch {
                    val sheet = DocumentExportService<SaleItem>().createSheet(
                        it1,
                        saleList,
                        exclude
                    )?.await()
                    withContext(Dispatchers.Main) {
                        if (sheet != null) {
                            Common.sendEmail(it1, sheet)
                        } else
                            Common.showToast(it1, "Error occurred in exporting data!")
                        hideProgress()
                    }
                }
            }
        }

        view.docRecyclerView.layoutManager = LinearLayoutManager(context);
        saleListAdapter = SalesListAdapter(saleList, this)
        view.docRecyclerView.adapter = saleListAdapter
        itemCollectionAdapter = context?.let { ItemCollectionAdapter(it) }!!
        getRecentDocuments()
    }

    private fun getRecentDocuments() {
        view?.progress_circular?.visibility = View.VISIBLE
        saleList.clear()
        itemCollectionAdapter.getRecentDocuments()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { document ->
                    document?.toObject(SaleItem::class.java).let { it1 ->
                        it1?.let { it2 ->
                            saleList.add(it2)
                        }
                    }
                }
                saleListAdapter.notifyDataSetChanged()
                if (saleList.isEmpty())
                    view?.searchView?.visibility = View.INVISIBLE
                else {
                    view?.searchView?.visibility = View.VISIBLE
                    view?.searchView?.setOnQueryTextListener(this)
                    view?.searchView?.setOnQueryTextListener(this)
                }
                view?.progress_circular?.visibility = View.GONE
            }
    }

    override fun onAction(pos: Int, actionId: Actions, data: Any) {
        when (actionId) {
            Actions.Delete -> {
                itemCollectionAdapter.deleteSaleItem(data as String)
                Handler().postDelayed({
                    getRecentDocuments()
                }, 500)
            }
            Actions.Edit -> {
                val saleItem: SaleItem = data as SaleItem
                val actionFirstFragmentToSecondFragment =
                    FirstFragmentDirections.actionFirstFragmentToSecondFragment(saleItem)
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
            getRecentDocuments()
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
                            saleList.clear()
                            val list = it.result?.documents?.map { snapshot ->
                                snapshot.toObject(SaleItem::class.java)
                            }
                            if (list != null)
                                saleList.addAll(list as ArrayList<SaleItem>)
                        }
                        else -> {
                        }
                    }
                    updateSaleListView()
                    filterView.setBackgroundResource(R.drawable.circile_filled)
                }
            }
        } else if (startDate != null) {
            itemCollectionAdapter.getDocuments(100, startDate, filterType).addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        saleList.clear()
                        val list = it.result?.documents?.map { snapshot ->
                            snapshot.toObject(SaleItem::class.java)
                        }
                        if (list != null)
                            saleList.addAll(list as ArrayList<SaleItem>)
                    }
                    else -> {
                        Log.e(FirstFragment::class.simpleName, "onApply: ", it.exception)
                    }
                }
                updateSaleListView()
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
        if (saleList.isEmpty())
            view?.searchView?.visibility = View.INVISIBLE
        else {
            view?.searchView?.visibility = View.VISIBLE
            view?.searchView?.setOnQueryTextListener(this)
        }
        view?.progress_circular?.visibility = View.GONE
    }

    private fun showProgress(text: String = "loading") {
        progressDialog.setMessage(text)
        if (!progressDialog.isShowing) progressDialog.show()
    }

    private fun hideProgress() {
        if (progressDialog.isShowing) progressDialog.hide()
    }

}
