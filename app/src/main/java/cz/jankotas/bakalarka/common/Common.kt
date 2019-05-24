package cz.jankotas.bakalarka.common

import cz.jankotas.bakalarka.models.Location
import cz.jankotas.bakalarka.models.Report
import cz.jankotas.bakalarka.services.IMyAPI
import cz.jankotas.bakalarka.services.RetrofitClient

object Common {

    internal const val APP_NAME = "Bakalarka"

    // Kolpinghaus
    //private const val BASE_URL = "http://192.168.10.193/api/"

    // Uni
    private const val BASE_URL = "http://172.22.9.72/api/"

    internal const val PAGE_SIZE = 5

    internal var login: Boolean = false

    internal lateinit var token: String

    internal var userID : Int? = null

    internal var location = Location(49.9384738, 64.293848)

    internal val api: IMyAPI
        get() = RetrofitClient.getClient(BASE_URL).create(IMyAPI::class.java)
}