package cz.jankotas.bakalarka

import android.app.Dialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.common.SharedPrefs
import cz.jankotas.bakalarka.fragments.MainTabClosed
import cz.jankotas.bakalarka.fragments.MainTabCurrent
import cz.jankotas.bakalarka.fragments.MainTabOwn
import cz.jankotas.bakalarka.models.APILoginResponse
import cz.jankotas.bakalarka.models.User
import cz.jankotas.bakalarka.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.full_view_progress_bar.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import mumayank.com.airlocationlibrary.AirLocation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Hlavní aktivita aplikace, která se spouští po úspěšném přihlášení. Obsahuje tři fragmenty se seznamy nahlášených podnětů,
 * postranní menu a Fab tlačítko pro přidání nového podnětu.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // dialog pro načítání
    private lateinit var dialog: Dialog

    // objekt pro uchování aktuální polohy uživatele
    private var airLocation: AirLocation? = null

    // onCreate metoda inicializuje aktivitu (nastavení view)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setNavigationDrawer(nav_view.getHeaderView(0))

        // zobrazit dialog s progress barem
        val view = this.layoutInflater.inflate(R.layout.full_view_progress_bar, null)
        view.progress_text.text = getString(R.string.waiting_for_location)
        tabs.visibility = View.GONE // v průvěhy načítání skrýt taby pro přepínání se mezi fragmenty

        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.show()

        getCurrentLocation() // požadavek na získání aktuální polohy uživatele

        // po kliknutí na fab tlačítko spustit proces přidávání nového podnětu
        fab.setOnClickListener {
            startActivity(Intent(this, ReportGetPhotosActivity::class.java))
        }
    }

    // onStop metoda se volá na konci životního cyklu aktivity a lze v ní uložit aplikační data
    override fun onStop() {
        super.onStop()
        Log.d(Common.APP_NAME, "onStop()")
        SharedPrefs.saveAccessToken()
    }

    // onDestroy metoda se volá stejně jako onStop na konci životního cyklu aktivity s tím, že je volána i v případě pádu
    // aplikace, proto je dobré se pojistit i touto metodou
    override fun onDestroy() {
        super.onDestroy()
        Log.d(Common.APP_NAME, "onDestroy()")
        SharedPrefs.saveAccessToken()
    }

    // při stisknutí tlačítka zpět se volá tato funkce
    // v mém případě jsem chtěl zajistit, aby se uživatel odhlásil
    override fun onBackPressed() {
        super.onBackPressed()
        logoutUser(Common.token)
    }

    // zobrazení menu po kliknutí na ikonu tří teček v pravém horním rohu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    // definování akcí vzhledem k výběru položky z menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                getCurrentLocation() // refresh
                true
            }
            R.id.action_report_bug -> {
                startActivity(Intent(this, ReportBugActivity::class.java)) // hlášení chyby
                true
            }
            R.id.action_logout -> {
                logoutUser(Common.token) // logout
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // definování akcí po výběru jedné z položek v postraním menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.menu_home -> {
                // Handle home menu action
            }
            /*
            R.id.menu_map -> {}
            R.id.menu_stats -> {}
            R.id.menu_profile -> {}
            R.id.menu_messages -> {}
            */
            R.id.menu_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
            R.id.menu_help -> {
                startActivity(Intent(this, HelpActivity::class.java))
            }
            R.id.menu_report_bug -> {
                startActivity(Intent(this, ReportBugActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    // metoda se volá po získání aktuální polohy
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        airLocation?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    // požadavek na získání povolení k získávání polohy
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        airLocation?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // volání knihovní funkce k získání aktuální polohy zařízení
    private fun getCurrentLocation() {
        // k získání polohy se používá knihovna AirLocation, která kombinuje všechny metody k rychlému určení přesné polohy zařízení
        airLocation = AirLocation(this, true, true, object : AirLocation.Callbacks {

            // v případě úspěchy se data zpracují
            override fun onSuccess(location: Location) {
                Common.location = cz.jankotas.bakalarka.models.Location(location.latitude, location.longitude)
                viewpager_main.adapter = SectionsPageAdapter(supportFragmentManager)
                viewpager_main.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
                tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewpager_main))
                tabs.setupWithViewPager(viewpager_main)
                tabs.visibility = View.VISIBLE
                dialog.dismiss()
            }

            // v případě neúspěchu následuje další pokus
            override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                getCurrentLocation()
                Toast.makeText(this@MainActivity, locationFailedEnum.name, Toast.LENGTH_SHORT).show()
            }
        })
    }

    // nastavení postranního menu
    private fun setNavigationDrawer(headerView: View) {

        // nastavení observeru pro data uživatele (pro získání ikony fotografie, emailu a jména)
        ViewModelProviders.of(this).get(UserViewModel::class.java).getUser().observe(this,
            androidx.lifecycle.Observer<User> { user ->
                Glide.with(this).load(user.avatarURL).into(headerView.profile_image)
                headerView.username_hamburger.text = user.name
                headerView.email_hamburger.text = user.email
            })

        // přidání tlačítka na otevírání menu
        val toggle = ActionBarDrawerToggle(this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        // nastavení aktivné položky v menu
        nav_view.setNavigationItemSelectedListener(this)
    }

    // odhlášení uživatele
    private fun logoutUser(token: String) {
        Common.api.logoutUser(token).enqueue(object : Callback<APILoginResponse> {
            override fun onFailure(call: Call<APILoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<APILoginResponse>, response: Response<APILoginResponse>) {

                // v případě HTTP 200 bylo odhlášení úspěšné
                if (response.code() == 200) {
                    Common.login = false
                    Toast.makeText(this@MainActivity, getString(R.string.successful_logout), Toast.LENGTH_LONG).show()
                    finish() // ukončení aktivity
                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.err_login_4), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    // nastavení přepínání sekcí / fragmentů
    inner class SectionsPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

        // určení názvů sekcí pro dané indexy
        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.currrentTabTitle)
                1 -> getString(R.string.closedTabTitle)
                2 -> getString(R.string.ownTabTitle)
                else -> null
            }
        }

        // určení fragmentů pro dané indexy sekcí
        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> MainTabCurrent()
                1 -> MainTabClosed()
                2 -> MainTabOwn()
                else -> null
            }
        }

        // funkce vrací celkový počet sekcí
        override fun getCount(): Int {
            return 3
        }
    }
}
