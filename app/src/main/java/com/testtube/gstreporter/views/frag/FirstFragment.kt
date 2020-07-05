package com.testtube.gstreporter.views.frag

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.testtube.gstreporter.R
import com.testtube.gstreporter.firestoreController.ItemCollectionAdapter
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.views.adapters.SalesListAdapter
import com.testtube.gstreporter.views.vInterface.RecyclerViewInterface
import com.testtube.gstreporter.views.vInterface.RecyclerViewInterface.Actions
import kotlinx.android.synthetic.main.fragment_first.view.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), RecyclerViewInterface {

    private var saleList: MutableList<SaleItem> = ArrayList()
    lateinit var saleListAdapter: SalesListAdapter
    lateinit var itemCollectionAdapter: ItemCollectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.fab_new_form.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        view.docRecyclerView.layoutManager = LinearLayoutManager(context);
        saleListAdapter = SalesListAdapter(saleList, this)
        view.docRecyclerView.adapter = saleListAdapter
        itemCollectionAdapter = context?.let { ItemCollectionAdapter(it) }!!
        getAllDocuments()

    }

    private fun getAllDocuments() {
        saleList.clear()
        itemCollectionAdapter.getAllDocuments().addOnSuccessListener { querySnapshot ->
            querySnapshot.documents.forEach { document ->
                document?.toObject(SaleItem::class.java).let { it1 ->
                    it1?.let { it2 ->
                        saleList.add(it2)
                    }
                }
            }
            saleListAdapter.notifyDataSetChanged()
        }
    }

    override fun onAction(pos: Int, actionId: Actions, data: Any) {
        when (actionId) {
            Actions.Delete -> {
                itemCollectionAdapter.deleteSaleItem(data as String)
                Handler().postDelayed({
                    getAllDocuments()
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

}
