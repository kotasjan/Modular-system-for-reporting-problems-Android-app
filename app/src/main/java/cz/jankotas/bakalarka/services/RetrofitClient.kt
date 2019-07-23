package cz.jankotas.bakalarka.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import com.google.gson.Gson

/**
 * Objekt, který slouží jako klient knihovny Retrofit
 */
object RetrofitClient {

    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {

        if (retrofit == null) {

            val gson = GsonBuilder().setDateFormat("yyyy-mm-dd' 'hh:mm:ss").create()

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }
}
