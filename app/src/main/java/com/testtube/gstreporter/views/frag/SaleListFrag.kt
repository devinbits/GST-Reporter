package com.testtube.gstreporter.views.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.testtube.gstreporter.R
import com.testtube.gstreporter.database.MyDatabase
import com.testtube.gstreporter.model.Filter
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.utils.DocumentExportService
import com.testtube.gstreporter.utils.MyItemAnimator
import com.testtube.gstreporter.viewmodel.SaleListVM
import com.testtube.gstreporter.views.adapters.SalesListAdapter
import com.testtube.gstreporter.views.vInterface.RecyclerViewInterface
import com.testtube.gstreporter.views.vInterface.RecyclerViewInterface.Actions
import kotlinx.android.synthetic.main.sale_list_frag.*
import kotlinx.android.synthetic.main.sale_list_frag.view.*
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
    private var saleList: MutableList<SaleItem> = ArrayList()
    private lateinit var saleListAdapter: SalesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sale_list_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(SaleListVM::class.java)
        fab_new_form.setOnClickListener {
            val saleItem: SaleItem? = viewModel.getLastDocument()
            val actionFirstFragmentToSecondFragment =
                SaleListFragDirections.actionFirstFragmentToSecondFragment(saleItem)
            findNavController().navigate(actionFirstFragmentToSecondFragment)
        }
        filterView.setOnClickListener {
            fragmentManager?.let { it1 ->
                DateFilterView.getInstance(this, viewModel.filter.value).show(it1, "dialog")
            }
        }

        share.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_progressDialog)
            context?.let { it1 ->
                val customHeaders: Array<String> = arrayOf(
                    "Bill",
                    "Date",
                    "GST_Percentage",
                    "Party_Name",
                    "Party_GSTN",
                    "Total_Invoice_Value",
                    "Bill_Amount",
                    "cGST",
                    "sGST",
                    "iGST",
                    "Total_GST"
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    val sheet = DocumentExportService<SaleItem>().createSheet(
                        it1,
                        saleList.asReversed(),
                        customHeaders
                    )?.await()
                    withContext(Dispatchers.Main) {
                        findNavController().navigateUp()
                        if (sheet != null) {
                            Common.sendEmail(it1, sheet)
                        } else
                            Common.showToast(it1, "Exporting Failed! Try Again")
                    }
                }
            }
        }

        docRecyclerView.layoutManager = LinearLayoutManager(context)
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

        viewModel.filter.observe(viewLifecycleOwner, androidx.lifecycle.Observer { filter ->
            if (filter == null) {
                filterView.setBackgroundResource(android.R.color.transparent)
                return@Observer
            }
            filterView.setBackgroundResource(R.drawable.circile_filled)
        })

        MyDatabase.getDatabase(requireContext()).partyDao().getAll().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.forEach {p->
                Log.d("Value", p.toString())
            }
        })
    }

    override fun onAction(pos: Int, actionId: Actions, data: Any) {
        when (actionId) {
            Actions.Delete -> {
                viewModel.deleteSale(pos,data as String)
            }
            Actions.Edit -> {
                val saleItem: SaleItem = data as SaleItem
                val actionFirstFragmentToSecondFragment =
                    SaleListFragDirections.actionFirstFragmentToSecondFragment(saleItem)
                findNavController().navigate(actionFirstFragmentToSecondFragment)
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
        viewModel.getFilteredDocDocuments(filter)
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
