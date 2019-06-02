package cz.jankotas.bakalarka.fragments

import android.content.Intent
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
import cz.jankotas.bakalarka.ReportActivity
import cz.jankotas.bakalarka.adapters.ReportAllAdapter
import cz.jankotas.bakalarka.models.Report
import cz.jankotas.bakalarka.viewmodels.ReportAllViewModel

class MainTabCurrent : Fragment() {

    private lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.tab_main, container, false)

        mRecyclerView = view.findViewById(R.id.recycleView)
        mRecyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        mRecyclerView.setHasFixedSize(true)

        val adapter = ReportAllAdapter(view.context, onClickListener = { viewCard, report ->
            run {
                val intent = Intent(viewCard.context, ReportActivity::class.java)
                intent.putExtra("report", report)
                startActivity(intent)
            }
        })

        val reportViewModel = ViewModelProviders.of(this).get(ReportAllViewModel::class.java)

        reportViewModel.itemPagedList.observe(this, androidx.lifecycle.Observer<PagedList<Report>> { pagedList ->
            adapter.submitList(pagedList)
        })

        mRecyclerView.adapter = adapter

        return view
    }
}