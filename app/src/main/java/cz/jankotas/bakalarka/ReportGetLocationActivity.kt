package cz.jankotas.bakalarka

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

/**
 * Tato aktivita je součástí procesu přidávání nového podnětu. Slouží k získání přesné pozice místa, kde se problém
 * nachází. K tomu využívá Google Maps API a geolokace pomocí dostupných prostředků.
 */
class ReportGetLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    // vytvoření reference na objekt mapy knihovny GoogleMap
    private lateinit var map: GoogleMap

    // vytvoření reference na objekt airLocation knihovny AirLocation
    private var airLocation: AirLocation? = null

    // snackbar pro zobrazení informace o zjišťování polohy
    private lateinit var snackbar: Snackbar

    // onCreate metoda inicializuje aktivitu (nastavení view)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_get_location)

        // nastavení toolbaru a tlačítka zrušit (close icon)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)

        // získání podpůrného fragmentu a získání notifikace, když je mapa připravena
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // po kliknutí na Fab tlačítko se spustí lokalizace zařízení
        location_fab.setOnClickListener {
            getCurrentLocation(it)
        }

        // zlačítko pokaračovat v procesu přidávání nového podnětu
        btn_continue.setOnClickListener {
            Common.newReport.address = getAddress(Common.newReport.location!!)
            val intent = Intent(this, ReportGetCategoryActivity::class.java)
            startActivity(intent)
        }

        // tlačítko zpět v procesu přidávání nového podnětu
        btn_back.setOnClickListener {
            finish()
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

        // v případě, že lokace ještě nebyla určena, použije se lokace, která byla určena při startu hlavní aktivity
        if (Common.newReport.location == null) {
            // kamera se přesune na souřadnice, které byly zjištěny v hlavní aktivitě
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(Common.location.lat, Common.location.lng), 18f))
            Common.newReport.location = Common.location
        } else {
            // kamera se přesune na souřadnice, které byly v minulosti určeny
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(Common.newReport.location!!.lat, Common.newReport.location!!.lng), 18f))
            location_fab.setImageResource(R.drawable.ic_location_current)
        }

        // pokud se mapa posune, změní se obrázek ikony ve Fab tlačítku
        map.setOnCameraMoveStartedListener {
            location_fab.setImageResource(R.drawable.ic_location_searching)
        }

        // listener, který čeká na ustálení pozice mapy
        map.setOnCameraIdleListener {
            // Cleaning all the markers.
            map.clear()
            val location = map.cameraPosition.target
            Common.newReport.location = cz.jankotas.bakalarka.models.Location(location.latitude, location.longitude)
        }
    }

    // zobrazení dialogu zda si uživatel opravdu přeje opustit proces přidávání podnětu
    override fun onSupportNavigateUp(): Boolean {
        showDialog()
        return true
    }

    // zobrazení dialogu před opuštěním procesu přidávání nového hlášení
    private fun showDialog() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage(getString(R.string.warning_closing_report))
        builder1.setCancelable(true)

        // potvrzení akce
        builder1.setPositiveButton("OK") { dialog, _ ->
            run {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                Common.newReport.clearData()
                dialog.cancel()
            }
        }

        // zrušení akce
        builder1.setNegativeButton("Cancel") { dialog, _ ->
            run {
                dialog.cancel()
            }
        }

        val alert11 = builder1.create()
        alert11.show()
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
    private fun getCurrentLocation(view: View) {
        // k získání polohy se používá knihovna AirLocation, která kombinuje všechny metody k rychlému určení přesné polohy zařízení
        airLocation = AirLocation(this, true, true, object : AirLocation.Callbacks {

            // v případě úspěchy se data zpracují
            override fun onSuccess(location: Location) {
                Common.newReport.location = cz.jankotas.bakalarka.models.Location(location.latitude, location.longitude)
                Common.location = Common.newReport.location!!

                // vycentrovat mapu na pozici získaných souřadnic
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

            // v případě neúspěchu zobrazit chybové hlášení
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

    // získání jména města/obce ze získaných souřadnic
    private fun getAddress(location: cz.jankotas.bakalarka.models.Location): String {
        val gcd = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = gcd.getFromLocation(location.lat, location.lng, 1) // získání adresy ze souřadnic
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (addresses != null && addresses.isNotEmpty() && addresses[0].locality != null) {
            return addresses[0].locality // vracení názvu obce/města
        }
        return ""
    }
}
