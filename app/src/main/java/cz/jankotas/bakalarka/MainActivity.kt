package cz.jankotas.bakalarka

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.fragments.MainTabClosed
import cz.jankotas.bakalarka.fragments.MainTabCurrent
import cz.jankotas.bakalarka.fragments.MainTabOwn
import cz.jankotas.bakalarka.model.APIResponse
import cz.jankotas.bakalarka.model.User
import cz.jankotas.bakalarka.remote.IMyAPI
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val PREFS_FILE = "cz.jankotas.bakalarka.prefs"

    private val ACCESS_TOKEN = "access_token"

    var prefs: SharedPreferences? = null

    private var token: String? = null

    private lateinit var mService: IMyAPI

    private var mSectionsPageAdapter: SectionsPageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        prefs = this.getSharedPreferences(PREFS_FILE, 0)
        token = prefs!!.getString(ACCESS_TOKEN, null)

        if (token == null) {
            Toast.makeText(this@MainActivity, "Chybějící token", Toast.LENGTH_SHORT).show()
            finish()
        }

        val gson = Gson()
        val json = prefs!!.getString("user", "")
        val user = gson.fromJson<User>(json, User::class.java)

        // Change hamburger menu header details (username and email)
        val headerView: View = nav_view.getHeaderView(0)

        // Load image from internal storage
        val file = File(this.filesDir, "profile.jpg")

        // Set current profile image of authorized user
        headerView.profile_image.setImageDrawable(Drawable.createFromPath(file.toString()))

        // Set username and user email
        headerView.username_hamburger.text = user.name
        headerView.email_hamburger.text = user.email

        mService = Common.api


        mSectionsPageAdapter = SectionsPageAdapter(supportFragmentManager)

        viewpager_main.adapter = mSectionsPageAdapter

        viewpager_main.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs_main))
        tabs_main.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewpager_main))
        tabs_main.setupWithViewPager(viewpager_main)

        // Set onClickListener for add floating button
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }

        // Toggle button for menu drawer
        val toggle = ActionBarDrawerToggle(this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            // TODO logout pripadne ukonceni aplikace v pripade, ze se jedna o posledni aktivitu pred loginem
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_filter -> true
            R.id.action_sort -> true
            R.id.action_refresh -> true
            R.id.action_logout -> {
                logoutUser(token!!)
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

    // Logout function

    private fun logoutUser(token: String) {
        mService.logoutUser(token).enqueue(object : Callback<APIResponse> {
            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {

                if (response.code() == 200) {

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
            return when(position) {
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
