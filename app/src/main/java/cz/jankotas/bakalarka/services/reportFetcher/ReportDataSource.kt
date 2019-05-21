package cz.jankotas.bakalarka.services.reportFetcher

import android.util.Log
import androidx.annotation.NonNull
import androidx.paging.PageKeyedDataSource
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.APIReportResponse
import cz.jankotas.bakalarka.models.Report
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportDataSource : PageKeyedDataSource<Int, Report>() {

    private val FIRST_PAGE = 1

    override fun loadInitial(@NonNull params: LoadInitialParams<Int>, @NonNull callback: LoadInitialCallback<Int, Report>) {

        Common.api.getReports(Common.token, Common.location, FIRST_PAGE, null).enqueue(object : Callback<APIReportResponse> {
            override fun onResponse(call: Call<APIReportResponse>, response: Response<APIReportResponse>) {

                if (response.body() != null) {

                    callback.onResult(response.body()!!.reports, null, FIRST_PAGE + 1)

                    Log.d(Common.APP_NAME, "Reports data: " + response.body().toString())
                }
            }

            override fun onFailure(call: Call<APIReportResponse>, t: Throwable) {
                Log.d(Common.APP_NAME, "Failure during getting reports data.")
                t.printStackTrace()
            }
        })
    }

    override fun loadBefore(@NonNull params: LoadParams<Int>, @NonNull callback: LoadCallback<Int, Report>) {

        Common.api.getReports(Common.token, Common.location, params.key, null).enqueue(object :
            Callback<APIReportResponse> {
            override fun onResponse(call: Call<APIReportResponse>, response: Response<APIReportResponse>) {


                if (response.body() != null) {
                    val key = params.key + 1
                    callback.onResult(response.body()!!.reports, key)

                    Log.d(Common.APP_NAME, "Reports data: " + response.body().toString())
                }
            }

            override fun onFailure(call: Call<APIReportResponse>, t: Throwable) {
                Log.d(Common.APP_NAME, "Failure during getting reports data.")
                t.printStackTrace()
            }
        })
    }

    override fun loadAfter(@NonNull params: LoadParams<Int>, @NonNull callback: LoadCallback<Int, Report>) {

        Common.api.getReports(Common.token, Common.location, params.key, null).enqueue(object :
            Callback<APIReportResponse> {
            override fun onResponse(call: Call<APIReportResponse>, response: Response<APIReportResponse>) {

                if (response.body() != null) {
                    val key = params.key + 1
                    callback.onResult(response.body()!!.reports, key)

                    Log.d(Common.APP_NAME, "Reports data: " + response.body().toString())
                }
            }

            override fun onFailure(call: Call<APIReportResponse>, t: Throwable) {
                Log.d(Common.APP_NAME, "Failure during getting reports data.")
                t.printStackTrace()
            }
        })
    }

    companion object {
        const val PAGE_SIZE = 5
    }
}
