package com.gami.tomokanjimobile.network

import com.gami.tomokanjimobile.data.Kanji
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface KanjiApi {

    @GET("tomokanji/api/kanjis/")
    suspend fun getKanjis(): List<Kanji>

    @GET("tomokanji/api/kanjis/level/{level}")
    suspend fun getKanjisForLevel(@Path("level") level: Int): List<Kanji>

    @GET("tomokanji/api/users/{userId}/kanjis")
    suspend fun getMasteredKanjiIds(@Path("userId") userId: Int): List<Int>

    @POST("tomokanji/api/users/{userId}/kanjis/{kanjiId}")
    suspend fun masterKanji(@Path("userId") userId: Int, @Path("kanjiId") kanjiId: Int): Boolean

    @DELETE("tomokanji/api/users/{userId}/kanjis/{kanjiId}")
    suspend fun unmasterKanji(@Path("userId") userId: Int, @Path("kanjiId") kanjiId: Int): Boolean

    @GET("tomokanji/api/kanjis/search")
    suspend fun searchKanjis(@Query("query") query: String): List<Kanji>

    companion object {
        private const val BASE_URL = "http://tomokanji.top:8080/"

        val service: KanjiApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KanjiApi::class.java)
        }
    }
}