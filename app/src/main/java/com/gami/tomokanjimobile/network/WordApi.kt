package com.gami.tomokanjimobile.network

import com.gami.tomokanjimobile.data.Word
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface WordApi {

    @GET("tomokanji/api/words/")
    suspend fun getWords(): List<Word>

    @GET("tomokanji/api/words/level/{level}")
    suspend fun getWordsForLevel(@Path("level") level: Int): List<Word>

    @GET("tomokanji/api/users/{userId}/words")
    suspend fun getMasteredWordIds(@Path("userId") userId: Int): List<Int>

    @POST("tomokanji/api/users/{userId}/words/{kanjiId}")
    suspend fun masterWord(@Path("userId") userId: Int, @Path("kanjiId") kanjiId: Int): Boolean

    @DELETE("tomokanji/api/users/{userId}/words/{kanjiId}")
    suspend fun unmasterWord(@Path("userId") userId: Int, @Path("kanjiId") kanjiId: Int): Boolean

    @GET("tomokanji/api/words/search")
    suspend fun searchWords(@Query("query") query: String): List<Word>

    companion object {
        private const val BASE_URL = "http://tomokanji.top:8080/"

        val service: WordApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WordApi::class.java)
        }
    }
}