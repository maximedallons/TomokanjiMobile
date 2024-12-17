package com.gami.tomokanjimobile.network

import com.gami.tomokanjimobile.data.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface LoginApi {

    @GET("tomokanji/api/users/{username}/{password}")
    suspend fun login(@Path("username") username: String, @Path("password") password: String): User

    companion object {
        private const val BASE_URL = "http://tomokanji.top:8080/"

        val service: LoginApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LoginApi::class.java)
        }
    }
}