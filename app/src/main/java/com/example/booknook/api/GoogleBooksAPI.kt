package com.example.booknook.api

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.Serializable

interface GoogleBooksAPI {
    @GET("books/v1/volumes")
    suspend fun getBooksFromSearch(@Query("q") search: String, @Query("maxResults") maxResults: Int):
            GoogleBooksAPIResponse

    data class GoogleBooksAPIResponse (
        @SerializedName("totalItems")
        val totalBookResults: Int,
        @SerializedName("items")
        val items: List<GoogleItems>
        ) : Serializable

    data class GoogleItems (
        @SerializedName("volumeInfo")
        val volumeInfo: GoogleBook,
    ) : Serializable

    companion object {
        private const val BASE_URL = "https://www.googleapis.com/"

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BASIC
            }).build()

        fun create(): GoogleBooksAPI {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleBooksAPI::class.java)
        }
    }
}