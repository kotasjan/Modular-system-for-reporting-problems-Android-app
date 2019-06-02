package cz.jankotas.bakalarka

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

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

class ReportOnMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var location: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_on_map)

        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

        // Add a marker in Sydney and move the camera
        val loc = LatLng(location.lat, location.lng)
        map.addMarker(MarkerOptions().position(loc))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 18.0f))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
