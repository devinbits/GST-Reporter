package com.testtube.gstreporter.views.frag

import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_first.view.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var saleList: MutableList<SaleItem> = ArrayList()
    lateinit var saleListAdapter: SalesListAdapter

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
        saleListAdapter = SalesListAdapter(saleList)
        view.docRecyclerView.adapter = saleListAdapter

        getAllDocuments()

    }

    private fun getAllDocuments() {
        saleList.clear()
        context?.let {
            ItemCollectionAdapter(it).getAllDocuments().addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { document ->
                    document?.toObject(SaleItem::class.java).let { it1 ->
                        it1?.let { it2 ->
                            saleList.add(it2)
                        }
                    }
                }
                if (saleList.size > 0) {
                    saleListAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
