package cz.jankotas.bakalarka.common

import android.content.Context
import android.graphics.drawable.Drawable
import com.nguyenhoanglam.imagepicker.model.Image
import cz.jankotas.bakalarka.R
import cz.jankotas.bakalarka.models.Category
import cz.jankotas.bakalarka.models.Location
import cz.jankotas.bakalarka.models.NewReport
import cz.jankotas.bakalarka.services.IMyAPI
import cz.jankotas.bakalarka.services.RetrofitClient

object Common {

    internal const val APP_NAME = "Bakalarka"

    // Kolpinghaus
    //private const val BASE_URL = "http://192.168.10.193/api/"

    // Uni
    //private const val BASE_URL = "http://172.22.9.72/api/"

    // AP
    private const val BASE_URL = "http://192.168.43.132/api/"

    internal const val PAGE_SIZE = 5

    internal var login: Boolean = false

    internal lateinit var token: String

    internal var userID : Int? = null

    internal lateinit var location : Location

    internal var newReport = NewReport(null, null, null, null, ArrayList(), null, null)

    internal var selectedImages : ArrayList<Image> = ArrayList()

    internal val api: IMyAPI
        get() = RetrofitClient.getClient(BASE_URL).create(IMyAPI::class.java)
}