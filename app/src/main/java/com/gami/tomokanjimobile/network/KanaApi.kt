package com.gami.tomokanjimobile.network

import com.gami.tomokanjimobile.data.Hiragana
import com.gami.tomokanjimobile.data.Katakana
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface KanaApi {

    @GET("kanas/hiraganas")
    suspend fun getHiraganas(): List<Hiragana>

    @GET("users/{userId}/hiraganas")
    suspend fun getMasteredHiraganaIds(@Path("userId") userId: Int): List<Int>

    @POST("users/{userId}/hiraganas/{hiraganaId}")
    suspend fun masterHiragana(@Path("userId") userId: Int, @Path("hiraganaId") hiraganaId: Int): Boolean

    @DELETE("users/{userId}/hiraganas/{hiraganaId}")
    suspend fun unmasterHiragana(@Path("userId") userId: Int, @Path("hiraganaId") hiraganaId: Int): Boolean

    @GET("kanas/katakanas")
    suspend fun getKatakanas(): List<Katakana>

    @GET("users/{userId}/katakanas")
    suspend fun getMasteredKatakanaIds(@Path("userId") userId: Int): List<Int>

    @POST("users/{userId}/katakanas/{katakanaId}")
    suspend fun masterKatakana(@Path("userId") userId: Int, @Path("katakanaId") katakanaId: Int): Boolean

    @DELETE("users/{userId}/katakanas/{katakanaId}")
    suspend fun unmasterKatakana(@Path("userId") userId: Int, @Path("katakanaId") katakanaId: Int): Boolean

    companion object {
        private const val BASE_URL = "http://tomokanji.top:8080/tomokanji/api/"

        val service: KanaApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KanaApi::class.java)
        }
    }
}