package cz.jankotas.bakalarka

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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


    private fun getModules() {

        Common.api.getModules(Common.token,
            Common.newReport.location!!.lat,
            Common.newReport.location!!.lng,
            selectedCategory!!.id).enqueue(object : Callback<APIModuleResponse> {
            override fun onResponse(call: Call<APIModuleResponse>, response: Response<APIModuleResponse>) {
                if (response.body() != null) {
                    val modules = response.body()!!.modules
                    Log.d(Common.APP_NAME, "All modules data: " + modules.toString())
                    startActivityModules(modules)
                }
            }

            override fun onFailure(call: Call<APIModuleResponse>, t: Throwable) {
                Log.d(Common.APP_NAME, "Failure during getting modules data.")
                t.printStackTrace()
                startActivityFinish()
            }
        })
    }

    private fun startActivityModules(modules: ArrayList<Module>) {
        Log.d(Common.APP_NAME, modules.toString())
        val intent = Intent(this, ReportModuleDataActivity::class.java)
        intent.putParcelableArrayListExtra("modules", modules)
        startActivity(intent)
    }

    private fun startActivityFinish() {

    }
}
