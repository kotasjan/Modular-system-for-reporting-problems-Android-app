package cz.jankotas.bakalarka.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import cz.jankotas.bakalarka.models.Report
import cz.jankotas.bakalarka.models.User

/**
 * Objekt, který se stará o ukládání informací do SharedPreferences.
 */
object SharedPrefs {

    private const val PREFS_FILE = "cz.jankotas.bakalarka.mPrefs" // jméno souboru, kde jsou data uložena
    private const val ACCESS_TOKEN = "access_token"

    private var prefs: SharedPreferences? = null

    // načte obsah uloženého souboru do proměnné prefs
    fun initializeSharedPreferences(context: Context) {
        prefs = context.getSharedPreferences(PREFS_FILE, 0)
        loadAccessToken()
    }

    // získání přístupového tokenu uloženého ve sdílených preferencích
    private fun loadAccessToken() {
        if (prefs != null && prefs!!.contains(ACCESS_TOKEN) && prefs!!.getString(ACCESS_TOKEN, null) != null) {
            Common.token = prefs?.getString(ACCESS_TOKEN, null)!!
            Log.d(Common.APP_NAME, "Access token loaded: " + Common.token)
        }
    }

    // uložení tokenu do sdílených preferencí
    fun saveAccessToken() {
        if (prefs != null) {
            val editor = prefs!!.edit()
            editor.putString(ACCESS_TOKEN, Common.token)
            editor.apply()
            Log.d(Common.APP_NAME, "Access token saved")
        }
    }

/*    fun saveFirstRun() {
        if (prefs != null) {
            val editor = prefs!!.edit()
            editor.putBoolean("firstRun", false)
            editor.apply()
            Log.d(Common.APP_NAME, "First run saved as FALSE")
        }
    }

    fun isFirstRun() : Boolean {
        if (prefs != null && prefs!!.contains(FIRST_RUN)) {
            return prefs?.getBoolean(FIRST_RUN, true)!!
        }
        return true
    }*/
}