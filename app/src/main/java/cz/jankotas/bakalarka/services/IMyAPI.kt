package cz.jankotas.bakalarka.services;


import cz.jankotas.bakalarka.models.APILoginResponse
import cz.jankotas.bakalarka.models.APIReportResponse
import cz.jankotas.bakalarka.models.Location
import cz.jankotas.bakalarka.models.User
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

    @GET("auth/user")
    fun getUser(
        @Header("Authorization") auth_token: String
    ): Call<APILoginResponse>

    @GET("reports")
    fun getReports(
        @Header("Authorization") auth_token: String,
        @Query("location") location: Location,
        @Query("page") page: Int,
        @Query("user") user: User?
    ): Call<APIReportResponse>

}
