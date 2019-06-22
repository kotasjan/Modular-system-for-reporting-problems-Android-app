package cz.jankotas.bakalarka.services;


import cz.jankotas.bakalarka.models.*
import retrofit2.Call
import retrofit2.http.*

interface IMyAPI {

    @FormUrlEncoded
    @POST("auth/register")
    fun registerUser(
        @Field("name") name: String, @Field("email") email: String, @Field("telephone") telephone: Int, @Field("password") password: String, @Field("password_confirmation") password_confirmation:String
    ): Call<APILoginResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun loginUser(
        @Field("email") email: String, @Field("password") password: String
    ): Call<APILoginResponse>

    @GET("auth/logout")
    fun logoutUser(
        @Header("Authorization") auth_token: String
    ): Call<APILoginResponse>

    @GET("mobile/users/{id}")
    fun getUser(
        @Header("Authorization") auth_token: String,
        @Path("id") id: Int
    ): Call<User>

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

    @FormUrlEncoded
    @POST("mobile/modules")
    fun getModules(
        @Header("Authorization") auth_token: String,
        @Field("lat") lat: Double,
        @Field("lng") lng: Double,
        @Field("category_id") category_id: Int
    ): Call<APIModuleResponse>

    @POST("mobile/report")
    fun sendReport(
        @Header("Authorization") auth_token: String,
        @Body report: NewReportToSend
    ): Call<APIReportResponse>
}
