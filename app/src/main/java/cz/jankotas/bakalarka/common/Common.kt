package cz.jankotas.bakalarka.common

import cz.jankotas.bakalarka.models.Report
import cz.jankotas.bakalarka.models.User
import cz.jankotas.bakalarka.services.remote.IMyAPI
import cz.jankotas.bakalarka.services.remote.RetrofitClient

object Common {

    internal const val APP_NAME = "WeatherApp"

    // Kolpinghaus
    // private const val BASE_URL = "http://192.168.10.193/api/"

    // Uni
    private const val BASE_URL = "http://172.22.9.223/api/"

    internal var login: Boolean = false

    internal lateinit var token: String

    //lateinit var mUser: User

    var mReportList: MutableList<Report> = arrayListOf()

    internal val api: IMyAPI
        get() = RetrofitClient.getClient(BASE_URL).create(IMyAPI::class.java)
}