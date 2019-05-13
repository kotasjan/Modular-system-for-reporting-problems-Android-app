package cz.jankotas.bakalarka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.jankotas.bakalarka.R
import cz.jankotas.bakalarka.adapters.ReportAdapter
import cz.jankotas.bakalarka.models.Location
import cz.jankotas.bakalarka.models.Report
import kotlinx.android.synthetic.main.tab_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainTabCurrent : Fragment() {

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    var listOfReports: ArrayList<Report> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //adding items in list
        for (i in 0..5) {
            val report = Report(1, Date(),Date(),"Černá skládka ve Vážanech", 1, "poznamka", "poznamka zamestnance", "Kroměříž", 1, 2,1, 1, 22.455, listOf("photo1", "photo2", "photo3"), Location(22.323, 55.345), 3, 2, true)
            listOfReports.add(report)
        }

        val view = inflater.inflate(R.layout.tab_main, container, false)

        mRecyclerView = view!!.findViewById(R.id.recycleView)
        mRecyclerView!!.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mAdapter = ReportAdapter(listOfReports)
        mRecyclerView!!.adapter = mAdapter

        return view
    }
}