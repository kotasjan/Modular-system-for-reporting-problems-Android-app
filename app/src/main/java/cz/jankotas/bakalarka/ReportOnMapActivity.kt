package cz.jankotas.bakalarka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.common.Common.location
import cz.jankotas.bakalarka.models.Location
import cz.jankotas.bakalarka.models.Report

/**
 * Aktivita zobrazuje pozici podnětu na mapě
 */
class ReportOnMapActivity : AppCompatActivity(), OnMapReadyCallback {

    // vytvoření reference na objekt mapy knihovny GoogleMap
    private lateinit var map: GoogleMap

    // vytvoření reference na objekt se souřadnicemi
    private lateinit var location: Location

    // onCreate metoda inicializuje aktivitu (nastavení view)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_on_map)

        // nastavení toolbaru a tlačítka zpět
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // získání podpůrného fragmentu a získání notifikace, když je mapa připravena
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // získání lokace z předchozí aktivity
        val bundle: Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
                val loc: Location? = getParcelable("location")
                if (loc != null) {
                    location = loc
                    Log.d(Common.APP_NAME, "Location: $location")
                } else finish()
            }
        }
    }

    // zobrazení menu po kliknutí na ikonu tří teček v pravém horním rohu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_report_bug, menu)
        return true
    }

    // definování akcí vzhledem k výběru položky z menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_report_bug -> {
                // spustit aktivitu ReportBugActivity
                startActivity(Intent(this, ReportBugActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ukončení aktivity po stisku tlačítka zpět
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // přidání značky s místem podnětu do mapy
        val loc = LatLng(location.lat, location.lng)
        map.addMarker(MarkerOptions().position(loc))

        // vycentrování kamery na místo podnětu
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 18.0f))
    }
}
