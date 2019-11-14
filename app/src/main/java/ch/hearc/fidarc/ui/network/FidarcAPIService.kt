package ch.hearc.fidarc.ui.network


import ch.hearc.fidarc.ui.data.model.CompanyCollection
import ch.hearc.fidarc.ui.data.model.FidelityCardCollection
import ch.hearc.fidarc.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

import ch.hearc.fidarc.ui.data.model.Token
import ch.hearc.fidarc.ui.data.model.User
import retrofit2.Response
import retrofit2.http.*

private const val BASE_URL = "https://fidarc.srvz-webapp.he-arc.ch/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface FidarcAPIService {


    @GET("/api/companies")
    suspend fun getCompaniesInfo(): CompanyCollection

    @GET("/api/fidelityCards")
    suspend fun getFidelityCards(@Header("authorization") token: String): FidelityCardCollection

    @GET("/api/user")
    suspend fun getUser(@Header("authorization") token: String): Response<User>

    @FormUrlEncoded
    @POST("/oauth/token")
    suspend fun login(@Field("grant_type") grantType: String = "password",
                      @Field("client_id") clientId: Int = 1,
                      @Field("client_secret") clientSecret: String = BuildConfig.CLIENT_SECRET,
                      @Field("username") username: String,
                      @Field("password") password: String): Response<Token>
}

object FidarcAPI {
    val retrofitService : FidarcAPIService by lazy { retrofit.create(FidarcAPIService::class.java) }
}