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
import cz.jankotas.bakalarka.adapters.ReportCurrentAdapter
import cz.jankotas.bakalarka.models.Report
import cz.jankotas.bakalarka.viewmodels.ReportCurrentViewModel

/**
 * Fragment hlavní aktivity, který zobrazuje aktuální podněty
 */
class MainTabCurrent : Fragment() {

    // RecyclerView, který zobrazuje seznam podnětů
    private lateinit var mRecyclerView: RecyclerView

    // inicializace fragmentu (stejné jako na počátku každé aktivity)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.tab_main, container, false) // view fragmentu, které se předá rodičovské aktivitě

        mRecyclerView = view.findViewById(R.id.recycleView) // přiřazení komponenty z layoutu
        mRecyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        mRecyclerView.setHasFixedSize(true)

        // přidělení adaptéru k RecyclerView, po kliknutí na položku podnětu zobrazit aktivitu jeho detailu
        val adapter = ReportCurrentAdapter(view.context, onClickListener = { viewCard, report ->
            run {
                val intent = Intent(viewCard.context, ReportActivity::class.java)
                intent.putExtra("report", report) // poslání objektu podnětu aktivitě detailu
                startActivity(intent)
            }
        })

        // přiřazení viewmodelu obsahujího data podnětů
        val reportViewModel = ViewModelProviders.of(this).get(ReportCurrentViewModel::class.java)

        // naslouchání změnám dat ve viewmodelu
        reportViewModel.itemPagedList.observe(this, androidx.lifecycle.Observer<PagedList<Report>> { pagedList ->
            adapter.submitList(pagedList) // v případě změny dat je předat adapteru
        })

        // přiřazení adaptéru k recylerview
        mRecyclerView.adapter = adapter

        return view
    }
}