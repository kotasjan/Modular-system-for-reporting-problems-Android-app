package cz.jankotas.bakalarka.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import cz.jankotas.bakalarka.models.Report
import cz.jankotas.bakalarka.models.User

object SharedPrefs {

    private const val PREFS_FILE = "cz.jankotas.bakalarka.mPrefs"
    private const val ACCESS_TOKEN = "access_token"

    private var prefs: SharedPreferences? = null

    // load content of shared preferences to variable
    fun initializeSharedPreferences(context: Context) {
        prefs = context.getSharedPreferences(PREFS_FILE, 0)
        loadAccessToken()
    }

    private fun loadAccessToken() {
        if (prefs != null && prefs!!.contains(ACCESS_TOKEN) && prefs!!.getString(ACCESS_TOKEN, null) != null) {
            Common.token = prefs?.getString(ACCESS_TOKEN, null)!!
            Log.d(Common.APP_NAME, "Access token loaded: " + Common.token)
        }
    }

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