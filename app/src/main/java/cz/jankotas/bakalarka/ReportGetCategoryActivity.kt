package cz.jankotas.bakalarka

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.jankotas.bakalarka.adapters.CategoryAdapter
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.APIModuleResponse
import cz.jankotas.bakalarka.models.Category
import cz.jankotas.bakalarka.models.Module
import kotlinx.android.synthetic.main.activity_report_get_category.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Aktivita je součástí procesu vytváření nového podnětu a slouží k určení kategorie hlášeného problému.
 */
class ReportGetCategoryActivity : AppCompatActivity() {

    // recyclerView pro zobrazení seznamu kategorií
    private lateinit var mRecyclerView: RecyclerView

    // id vybrané kategorie
    private var category_id: Int? = null

    // onCreate metoda inicializuje aktivitu (nastavení view)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_get_category)

        // nastavení toolbaru a tlačítka zrušit (close icon)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)

        category_id = Common.newReport.category_id

        // zlačítko pokaračovat v procesu přidávání nového podnětu
        btn_continue.setOnClickListener {
            if (Common.newReport.category_id != category_id) {
                Common.newReport.moduleData?.clear()
                Common.newReport.category_id = category_id
            }
            getModules()
        }

        // tlačítko zpět v procesu přidávání nového podnětu
        btn_back.setOnClickListener {
            Common.newReport.category_id = category_id
            finish()
        }

        // inicializace komponenty recyclerView
        mRecyclerView = findViewById(R.id.recycleView_categories)
        mRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mRecyclerView.setHasFixedSize(true)

        // vytvoření adaptéru pro zprostředkování položek / kategorií
        val adapter = CategoryAdapter(this, Category.categories, onClickListener = { _, category ->
            run {
                category_id = category.id
                btn_continue.visibility = View.VISIBLE
            }
        })

        recycleView_categories.adapter = adapter // přiřazení adaptéru komponentě recyclerView
    }

    // zobrazení menu po kliknutí na ikonu tří teček v pravém horním rohu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_report_bug, menu)
        return true
    }

    // definování akcí vzhledem k výběru položky z menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_report_bug -> {
                // spustit aktivitu ReportBugActivity
                startActivity(Intent(this, ReportBugActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // zobrazení dialogu zda si uživatel opravdu přeje opustit proces přidávání podnětu
    override fun onSupportNavigateUp(): Boolean {
        showDialog()
        return true
    }

    // zobrazení dialogu před opuštěním procesu přidávání nového hlášení
    private fun showDialog() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage(getString(R.string.warning_closing_report))
        builder1.setCancelable(true)

        // potvrzení akce
        builder1.setPositiveButton("OK") { dialog, _ -> run {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            Common.newReport.clearData()
            dialog.cancel()
        }}

        // zrušení akce
        builder1.setNegativeButton("Cancel") { dialog, _ -> run {
            dialog.cancel()
        }}

        val alert11 = builder1.create()
        alert11.show()
    }

    // To potvrzení kategorie kliknutím na tlačítko pokračovat se aplikace serveru dotáže, které moduly má pro
    // danou kategorii aktivované. Tyto moduly se následně předají další aktivitě, která je zbrazí a zpracuje.
    private fun getModules() {

        // dotaz na moduly
        Common.api.getModules(Common.token,
            Common.newReport.location!!.lat,
            Common.newReport.location!!.lng,
            Common.newReport.category_id!!).enqueue(object : Callback<APIModuleResponse> {
            override fun onResponse(call: Call<APIModuleResponse>, response: Response<APIModuleResponse>) {
                if (response.body() != null) {
                    val modules = response.body()!!.modules // uložit získané moduly
                    Log.d(Common.APP_NAME, "All modules data: $modules")
                    startNextActivity(modules)
                }
            }

            // pokud dojde k selhání, spustit další aktivitu bez modulů
            override fun onFailure(call: Call<APIModuleResponse>, t: Throwable) {
                Log.d(Common.APP_NAME, "Failure during getting modules data.")
                t.printStackTrace()
                startNextActivity(null)
            }
        })
    }

    // spustit následující aktivitu v procesu
    private fun startNextActivity(modules: ArrayList<Module>?) {
        val intent = Intent(this, ReportGetDescriptionActivity::class.java)
        intent.putParcelableArrayListExtra("modules", modules)
        startActivity(intent)
    }
}
