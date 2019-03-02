package cz.jankotas.bakalarka.remote;


import cz.jankotas.bakalarka.model.APIResponse
import retrofit2.Call
import retrofit2.http.*

interface IMyAPI {

    @FormUrlEncoded
    @POST("auth/register")
    fun registerUser(
        @Field("name") name: String, @Field("email") email: String, @Field("telephone") telephone: Int, @Field("password") password: String, @Field("password_confirmation") password_confirmation:String
    ): Call<APIResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun loginUser(
        @Field("email") email: String, @Field("password") password: String
    ): Call<APIResponse>

    @GET("auth/logout")
    fun logoutUser(
        @Header("Authorization") auth_token: String
    ): Call<APIResponse>

    @GET("auth/user")
    fun getUser(
        @Header("Authorization") auth_token: String
    ): Call<APIResponse>

}
