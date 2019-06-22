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

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var dialog: Dialog

    // Declare your airLocation object on top
    private var airLocation: AirLocation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setNavigationDrawer(nav_view.getHeaderView(0))

        val view = this.layoutInflater.inflate(R.layout.full_view_progress_bar, null)
        view.progress_text.text = getString(R.string.waiting_for_location)
        tabs.visibility = View.GONE

        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.show()

        getCurrentLocation()

        // Set onClickListener for add floating button
        fab.setOnClickListener {
            startActivity(Intent(this, ReportGetPhotosActivity::class.java))
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(Common.APP_NAME, "onStop()")
        SharedPrefs.saveAccessToken()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(Common.APP_NAME, "onDestroy()")
        SharedPrefs.saveAccessToken()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        logoutUser(Common.token)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu. This adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /* Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.*/
        return when (item.itemId) {
            R.id.action_refresh -> {
                getCurrentLocation()
                true
            }
            R.id.action_logout -> {
                logoutUser(Common.token)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

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
            R.id.menu_settings -> {
                startActivity(Intent(this, HelpActivity::class.java))
            }
            R.id.menu_help -> {

            }
            R.id.menu_report_bug -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        airLocation?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        airLocation?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getCurrentLocation() {
        airLocation = AirLocation(this, true, true, object : AirLocation.Callbacks {
            override fun onSuccess(location: Location) {
                // Adapter for connecting viewPager with tabs
                Common.location = cz.jankotas.bakalarka.models.Location(location.latitude, location.longitude)
                viewpager_main.adapter = SectionsPageAdapter(supportFragmentManager)
                viewpager_main.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
                tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewpager_main))
                tabs.setupWithViewPager(viewpager_main)
                tabs.visibility = View.VISIBLE
                dialog.dismiss()
            }

            override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                getCurrentLocation()
                // either do nothing, or show error which is received as locationFailedEnum
                Toast.makeText(this@MainActivity, locationFailedEnum.name, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setNavigationDrawer(headerView: View) {

        // ViewModel setting observer for User changes
        ViewModelProviders.of(this).get(UserViewModel::class.java).getUser().observe(this,
            androidx.lifecycle.Observer<User> { user ->
                Glide.with(this).load(user.avatarURL).into(headerView.profile_image)
                // Set username and user email
                headerView.username_hamburger.text = user.name
                headerView.email_hamburger.text = user.email
            })

        // Toggle button for menu drawer
        val toggle = ActionBarDrawerToggle(this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        // Setting active menu item
        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun logoutUser(token: String) {
        Common.api.logoutUser(token).enqueue(object : Callback<APILoginResponse> {
            override fun onFailure(call: Call<APILoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<APILoginResponse>, response: Response<APILoginResponse>) {

                // If response code is success, logout was successful
                if (response.code() == 200) {

                    Common.login = false
                    Toast.makeText(this@MainActivity, getString(R.string.successful_logout), Toast.LENGTH_LONG).show()
                    finish()

                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.err_login_4), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    inner class SectionsPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.currrentTabTitle)
                1 -> getString(R.string.closedTabTitle)
                2 -> getString(R.string.ownTabTitle)
                else -> null
            }
        }

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> MainTabCurrent()
                1 -> MainTabClosed()
                2 -> MainTabOwn()
                else -> null
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
