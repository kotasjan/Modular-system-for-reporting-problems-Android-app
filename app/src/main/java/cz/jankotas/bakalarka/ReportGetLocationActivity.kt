package cz.jankotas.bakalarka

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import cz.jankotas.bakalarka.common.Common
import kotlinx.android.synthetic.main.activity_report_get_location.*
import mumayank.com.airlocationlibrary.AirLocation
import java.io.IOException
import java.util.*

class ReportGetLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private var airLocation: AirLocation? = null

    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_get_location)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set onClickListener for add floating button
        location_fab.setOnClickListener {
            getCurrentLocation(it)
        }

        btn_back.setOnClickListener {
            finish()
        }

        btn_continue.setOnClickListener {
            Common.newReport.address = getAddress(Common.newReport.location!!)
            val intent = Intent(this, ReportGetCategoryActivity::class.java)
            startActivity(intent)
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

        if (Common.newReport.location == null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(Common.location.lat, Common.location.lng), 18f))
            Common.newReport.location = Common.location
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(Common.newReport.location!!.lat, Common.newReport.location!!.lng), 18f))
            location_fab.setImageResource(R.drawable.ic_location_current)
        }

        map.setOnCameraMoveStartedListener {
            location_fab.setImageResource(R.drawable.ic_location_searching)
        }

        map.setOnCameraIdleListener {
            // Cleaning all the markers.
            map.clear()
            val location = map.cameraPosition.target
            Common.newReport.location = cz.jankotas.bakalarka.models.Location(location.latitude, location.longitude)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        showDialog()
        return true
    }

    private fun showDialog() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage(getString(R.string.warning_closing_report))
        builder1.setCancelable(true)

        builder1.setPositiveButton("OK") { dialog, _ ->
            run {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                Common.newReport.clearData()
                dialog.cancel()
            }
        }

        builder1.setNegativeButton("Cancel") { dialog, _ ->
            run {
                dialog.cancel()
            }
        }

        val alert11 = builder1.create()
        alert11.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        airLocation?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        airLocation?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getCurrentLocation(view: View) {
        airLocation = AirLocation(this, true, true, object : AirLocation.Callbacks {
            override fun onSuccess(location: Location) {
                Common.newReport.location = cz.jankotas.bakalarka.models.Location(location.latitude, location.longitude)
                Common.location = Common.newReport.location!!

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude),
                    18.0f), 2000, object : GoogleMap.CancelableCallback {
                    override fun onFinish() {
                        location_fab.setImageResource(R.drawable.ic_location_current)
                    }

                    override fun onCancel() {
                        location_fab.setImageResource(R.drawable.ic_location_searching)
                    }
                })
            }

            override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                snackbar.dismiss()
                snackbar = Snackbar.make(view,
                    getString(R.string.unsuccessful_searching_for_location),
                    Snackbar.LENGTH_SHORT)
                snackbar.show()
                location_fab.setImageResource(R.drawable.ic_location_searching)
            }
        })

        snackbar = Snackbar.make(view, getString(R.string.waiting_for_location), Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

    private fun getAddress(location: cz.jankotas.bakalarka.models.Location): String {
        val gcd = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = gcd.getFromLocation(location.lat, location.lng, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (addresses != null && addresses.isNotEmpty()) {
            return addresses[0].locality
        }
        return ""
    }
}
