package cz.jankotas.bakalarka.services.reportFetcher

import android.util.Log
import androidx.annotation.NonNull
import androidx.paging.PageKeyedDataSource
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.APIReportsResponse
import cz.jankotas.bakalarka.models.Report
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Tato třída slouží k získávání a poskytování dat (aktuálních podnětů). Jelikož se pracuje se stránkami, je nutné vědět,
 * jakou stránku s podněty od serveru požadovat. Princip spočívá v tom, že pokud se posunujeme v seznamu podnětů směrem dolů,
 * narazíme na konec stránky, a proto chceme načíst data další stránku (volání metody loadAfter()). V opačném případě pokud
 * se pohybujeme směrem vzhůru, chceme předchozí již dříve načtené stránky. Stránky se indexují a proto pokud se zavolá metoda
 * loadAfter(), žádá se stránka PageIndex + 1. Aby byl posun plynulý, k volání dochází ještě před dosažením konce/začátku stránky.
 * K inicializaci (načtení první stránky) slouží metoda loadInitial().
 */
class ReportClosedDataSource : PageKeyedDataSource<Int, Report>() {

    private val FIRST_PAGE = 0

    // metoda se volá při inicializaci (k získání první stránky se seznamem podnětů)
    override fun loadInitial(@NonNull params: LoadInitialParams<Int>, @NonNull callback: LoadInitialCallback<Int, Report>) {

        // poslání požadavku na získání počátečního seznamu podnětů (odpovídající stránce s indexem 0)
        Common.api.getReports(Common.token, Common.location.lat, Common.location.lng, FIRST_PAGE, 1, null).enqueue(object :
            Callback<APIReportsResponse> {
            override fun onResponse(call: Call<APIReportsResponse>, response: Response<APIReportsResponse>) {

                // kontrola, že odpověď obsahuje data s podněty
                if (response.body() != null) {

                    callback.onResult(response.body()!!.reports, null, FIRST_PAGE + 1)

                    Log.d(Common.APP_NAME, "Closed reports data: " + response.body().toString())
                }
            }

            override fun onFailure(call: Call<APIReportsResponse>, t: Throwable) {
                Log.d(Common.APP_NAME, "Failure during getting reports data.")
                t.printStackTrace()
            }
        })
    }

    // metoda, která se volá pro získání předchozí stránky s podněty (PageIndex - 1)
    override fun loadBefore(@NonNull params: LoadParams<Int>, @NonNull callback: LoadCallback<Int, Report>) {

        // poslání požadavku na získání předchozího seznamu podnětů
        Common.api.getReports(Common.token, Common.location.lat, Common.location.lng, params.key, 1, null).enqueue(object :
            Callback<APIReportsResponse> {
            override fun onResponse(call: Call<APIReportsResponse>, response: Response<APIReportsResponse>) {

                // kontrola, že odpověď obsahuje data s podněty
                if (response.body() != null) {
                    val key = (if (params.key > 0) params.key - 1 else null)
                    callback.onResult(response.body()!!.reports, key)

                    Log.d(Common.APP_NAME, "Closed reports data: " + response.body().toString())
                }
            }

            override fun onFailure(call: Call<APIReportsResponse>, t: Throwable) {
                Log.d(Common.APP_NAME, "Failure during getting reports data.")
                t.printStackTrace()
            }
        })
    }

    // metoda, která se colá pro získání následující stránky s podněty (PageIndex + 1)
    override fun loadAfter(@NonNull params: LoadParams<Int>, @NonNull callback: LoadCallback<Int, Report>) {

        // poslání požadavku na získání následujícího seznamu podnětů
        Common.api.getReports(Common.token, Common.location.lat, Common.location.lng, params.key, 1, null).enqueue(object :
            Callback<APIReportsResponse> {
            override fun onResponse(call: Call<APIReportsResponse>, response: Response<APIReportsResponse>) {

                // kontrola, že odpověď obsahuje data s podněty
                if (response.body() != null) {
                    val key = (if (response.body()!!.reports.size == Common.PAGE_SIZE) params.key + 1 else null)
                    callback.onResult(response.body()!!.reports, key)

                    Log.d(Common.APP_NAME, "Closed reports data: " + response.body().toString())
                }
            }

            override fun onFailure(call: Call<APIReportsResponse>, t: Throwable) {
                Log.d(Common.APP_NAME, "Failure during getting reports data.")
                t.printStackTrace()
            }
        })
    }
}
