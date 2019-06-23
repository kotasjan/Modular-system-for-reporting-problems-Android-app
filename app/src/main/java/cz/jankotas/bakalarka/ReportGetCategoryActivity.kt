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

class ReportGetCategoryActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView

    private var category_id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_get_category)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)

        category_id = Common.newReport.category_id

        btn_continue.setOnClickListener {
            if (Common.newReport.category_id != category_id) {
                Common.newReport.moduleData?.clear()
                Common.newReport.category_id = category_id
            }
            getModules()
        }

        btn_back.setOnClickListener {
            Common.newReport.category_id = category_id
            finish()
        }

        mRecyclerView = findViewById(R.id.recycleView_categories)
        mRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mRecyclerView.setHasFixedSize(true)

        val adapter = CategoryAdapter(this, Category.categories, onClickListener = { _, category ->
            run {
                category_id = category.id
                btn_continue.visibility = View.VISIBLE
            }
        })

        recycleView_categories.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu. This adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_report_bug, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /* Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.*/
        return when (item.itemId) {
            R.id.action_report_bug -> {
                startActivity(Intent(this, ReportBugActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        showDialog()
        return true
    }

    private fun showDialog() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage(getString(R.string.warning_closing_report))
        builder1.setCancelable(true)

        builder1.setPositiveButton("OK") { dialog, _ -> run {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            Common.newReport.clearData()
            dialog.cancel()
        }}

        builder1.setNegativeButton("Cancel") { dialog, _ -> run {
            dialog.cancel()
        }}

        val alert11 = builder1.create()
        alert11.show()
    }

    private fun getModules() {

        Common.api.getModules(Common.token,
            Common.newReport.location!!.lat,
            Common.newReport.location!!.lng,
            Common.newReport.category_id!!).enqueue(object : Callback<APIModuleResponse> {
            override fun onResponse(call: Call<APIModuleResponse>, response: Response<APIModuleResponse>) {
                if (response.body() != null) {
                    val modules = response.body()!!.modules
                    Log.d(Common.APP_NAME, "All modules data: $modules")
                    startNextActivity(modules)
                }
            }

            override fun onFailure(call: Call<APIModuleResponse>, t: Throwable) {
                Log.d(Common.APP_NAME, "Failure during getting modules data.")
                t.printStackTrace()
                startNextActivity(null)
            }
        })
    }

    private fun startNextActivity(modules: ArrayList<Module>?) {
        val intent = Intent(this, ReportGetDescriptionActivity::class.java)
        intent.putParcelableArrayListExtra("modules", modules)
        startActivity(intent)
    }
}
