package cz.jankotas.bakalarka.common

import cz.jankotas.bakalarka.remote.IMyAPI
import cz.jankotas.bakalarka.remote.RetrofitClient

object Common {
    val BASE_URL = "http://192.168.1.100/api/"

    val api: IMyAPI
        get() = RetrofitClient.getClient(BASE_URL).create(IMyAPI::class.java)
}