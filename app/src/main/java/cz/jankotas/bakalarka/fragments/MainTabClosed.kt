package cz.jankotas.bakalarka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.jankotas.bakalarka.R
import cz.jankotas.bakalarka.adapters.ReportClosedAdapter
import cz.jankotas.bakalarka.models.Report
import cz.jankotas.bakalarka.viewmodels.ReportClosedViewModel

class MainTabClosed : Fragment() {

    private lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.tab_closed, container, false)

        mRecyclerView = view.findViewById(R.id.recycleViewClosed)
        mRecyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        mRecyclerView.setHasFixedSize(true)

        val adapter = ReportClosedAdapter(view.context)

        val reportViewModel = ViewModelProviders.of(this).get(ReportClosedViewModel::class.java)

        reportViewModel.itemPagedList.observe(this, androidx.lifecycle.Observer<PagedList<Report>> {pagedList ->
            adapter.submitList(pagedList)
        })

        mRecyclerView.adapter = adapter

        return view
    }
}