package cz.jankotas.bakalarka.common

import cz.jankotas.bakalarka.models.Location
import cz.jankotas.bakalarka.models.NewReport
import cz.jankotas.bakalarka.services.IMyAPI
import cz.jankotas.bakalarka.services.RetrofitClient

/**
 * Objekt Common uchovává globální proměnné aplikace a její nastavení. Uchovává pouze data, které je nutné sdílet mezi
 * více aktivitami, a které udržují stav aplikace.
 */
object Common {

    internal const val APP_NAME = "Bakalarka"

    // Kolpinghaus
    //private const val BASE_URL = "http://192.168.10.193/api/"

    // Uni
    //private const val BASE_URL = "http://172.22.9.72/api/"

    // AP
    //private const val BASE_URL = "http://192.168.43.132/api/"

    // Produkční URL adresa
    private const val BASE_URL = "https://www.jankotas.cz/api/"

    // velikost stránky s načtenými podněty (pro ViewPager)
    internal const val PAGE_SIZE = 5

    // proměnná značící, zda je uživatel přihlášen
    internal var login: Boolean = false

    // access_token potřebný pro komunikaci se serverem
    internal lateinit var token: String

    // identifikátor uživatele v systému
    internal var userID : Int? = null

    // aktuální poloha uživatele
    internal lateinit var location : Location

    // Objekt, který slouží při vytváření nového podnětu. Umožňuje mezi aktivitami procesu uchovávat historii.
    internal var newReport = NewReport(null, null, null, ArrayList(), null, null,null)

    // rozhranní pro síťovou komunikaci pomocí knihovny Retrofit
    internal val api: IMyAPI
        get() = RetrofitClient.getClient(BASE_URL).create(IMyAPI::class.java)
}