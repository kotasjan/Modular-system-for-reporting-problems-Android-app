package cz.jankotas.bakalarka.services;


import cz.jankotas.bakalarka.models.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Rozhraní pro komunikaci prostřednictvím knihovny Retrofit
 */
interface IMyAPI {

    // registrace uživatele
    @FormUrlEncoded
    @POST("auth/register")
    fun registerUser(
        @Field("name") name: String, @Field("email") email: String, @Field("telephone") telephone: Int, @Field("password") password: String, @Field("password_confirmation") password_confirmation:String
    ): Call<APILoginResponse>

    // přihlášení uživatele
    @FormUrlEncoded
    @POST("auth/login")
    fun loginUser(
        @Field("email") email: String, @Field("password") password: String
    ): Call<APILoginResponse>

    // odhlášení uživatele
    @GET("auth/logout")
    fun logoutUser(
        @Header("Authorization") auth_token: String
    ): Call<APILoginResponse>

    // získání dat o konkrétním uživateli
    @GET("mobile/users/{id}")
    fun getUser(
        @Header("Authorization") auth_token: String,
        @Path("id") id: Int
    ): Call<User>

    // získání seznamu podnětů
    @FormUrlEncoded
    @POST("mobile/reports")
    fun getReports(
        @Header("Authorization") auth_token: String,
        @Field("lat") lat: Double,
        @Field("lng") lng: Double,
        @Field("page") page: Int,
        @Field("closed") closed: Int?,
        @Field("user") user: Int?
    ): Call<APIReportsResponse>

    // získání aktivních modulů pro konkrétní samosprávu
    @FormUrlEncoded
    @POST("mobile/modules")
    fun getModules(
        @Header("Authorization") auth_token: String,
        @Field("lat") lat: Double,
        @Field("lng") lng: Double,
        @Field("category_id") category_id: Int
    ): Call<APIModuleResponse>

    // poslání nového podnětu
    @POST("mobile/report")
    fun sendReport(
        @Header("Authorization") auth_token: String,
        @Body report: NewReportToSend
    ): Call<APIReportResponse>

    // poslání hlášení o chybě
    @POST("bugs")
    fun sendBugReport(
        @Body bug: BugPOJO
    ): Call<APIBugResponse>
}
