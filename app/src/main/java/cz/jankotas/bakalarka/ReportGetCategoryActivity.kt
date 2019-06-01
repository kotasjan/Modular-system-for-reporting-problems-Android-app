package cz.jankotas.bakalarka

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    private var selectedCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_get_category)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)

        btn_continue.setOnClickListener {
            getModules()
        }

        btn_back.setOnClickListener {
            finish()
        }

        if (Category.categories.isEmpty()) Category.setCategories(this)

        mRecyclerView = findViewById(R.id.recycleView_categories)
        mRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mRecyclerView.setHasFixedSize(true)

        val adapter = CategoryAdapter(this, Category.categories, onClickListener = { viewCard, category ->
            run {
                selectedCategory = category
                btn_continue.visibility = View.VISIBLE
            }
        })

        recycleView_categories.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        showDialog()
        return true
    }

    private fun showDialog() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage(getString(R.string.warning_closing_report))
        builder1.setCancelable(true)

        builder1.setPositiveButton("OK") { dialog, id -> run {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            Common.newReport.clearData()
            Common.selectedImages.clear()
            dialog.cancel()
        }}

        builder1.setNegativeButton("Cancel") { dialog, id -> run {
            dialog.cancel()
        }}

        val alert11 = builder1.create()
        alert11.show()
    }

    private fun getModules() {

        Common.api.getModules(Common.token,
            Common.newReport.location!!.lat,
            Common.newReport.location!!.lng,
            selectedCategory!!.id).enqueue(object : Callback<APIModuleResponse> {
            override fun onResponse(call: Call<APIModuleResponse>, response: Response<APIModuleResponse>) {
                if (response.body() != null) {
                    val modules = response.body()!!.modules
                    Log.d(Common.APP_NAME, "All modules data: " + modules.toString())
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
        Common.newReport.category_id = selectedCategory!!.id
        val intent = Intent(this, ReportGetDescriptionActivity::class.java)
        intent.putParcelableArrayListExtra("modules", modules)
        startActivity(intent)
    }
}
