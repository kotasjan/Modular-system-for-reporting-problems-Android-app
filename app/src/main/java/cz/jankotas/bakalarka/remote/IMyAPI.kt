package cz.jankotas.bakalarka.remote;


import cz.jankotas.bakalarka.model.APIResponse
import retrofit2.Call
import retrofit2.http.*;
import java.math.BigInteger

interface IMyAPI {

    @FormUrlEncoded
    @POST("auth/register")
    fun registerUser(
        @Field("name") name: String, @Field("email") email: String, @Field("telephone") telephone: BigInteger, @Field("password") password: String
    ): Call<APIResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun loginUser(
        @Field("email") email: String, @Field("password") password: String
    ): Call<APIResponse>


}
