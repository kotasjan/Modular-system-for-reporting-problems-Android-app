package cz.jankotas.bakalarka

import android.graphics.drawable.Drawable
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
import com.google.android.material.snackbar.Snackbar
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
import kotlinx.android.synthetic.main.nav_header_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        setNavigationDrawer(nav_view.getHeaderView(0))

        // Adapter for connecting viewPager with tabs
        viewpager_main.adapter = SectionsPageAdapter(supportFragmentManager)
        viewpager_main.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewpager_main))
        tabs.setupWithViewPager(viewpager_main)

        // Set onClickListener for add floating button
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
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
            R.id.action_filter -> true
            R.id.action_sort -> true
            R.id.action_refresh -> true
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
            R.id.menu_map -> {

            }
            R.id.menu_stats -> {

            }
            R.id.menu_profile -> {

            }
            R.id.menu_messages -> {

            }
            R.id.menu_settings -> {

            }
            R.id.menu_help -> {

            }
            R.id.menu_report_bug -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setNavigationDrawer(headerView: View) {

        // ViewModel setting observer for User changes
        ViewModelProviders.of(this).get(UserViewModel::class.java).getUser().observe(this, androidx.lifecycle.Observer<User> {user ->
            // Load image from internal storage
            val file = File(this.filesDir, "profile.jpg")
            // Set current profile image of authorized user
            //headerView.profile_image.setImageDrawable(Drawable.createFromPath(file.toString()))

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
