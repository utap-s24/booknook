package com.example.booknook.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.Serializable
import java.lang.reflect.Type

interface NewYorkTimesAPI {
    @GET("svc/books/v3/lists/current/hardcover-fiction.json")
    suspend fun getHardCoverFictionBooks(@Query("api-key") apiKey: String): NYTHardCoverBooksApiResponse

    @GET("svc/books/v3/lists/overview.json")
    suspend fun getBooks(@Query("api-key") apiKey: String): NYTBooksApiResponse

    // GET BOOKS CLASSES
    data class NYTBooksApiResponse(
        @SerializedName("status")
        val status: String,
        @SerializedName("num_results")
        val numResults: Int,
        @SerializedName("results")
        val results: NYTBooksResults
    ) : Serializable

    data class NYTBooksResults(
        @SerializedName("bestsellers_date")
        val bestsellersDate: String,
        @SerializedName("lists")
        val lists: List<NYTBookList>
    ) : Serializable

    data class NYTBookList(
        @SerializedName("list_id")
        val listId: Int,
        @SerializedName("list_name")
        val listName: String,
        @SerializedName("books")
        val books: List<Book>
    ) : Serializable

    // GET HARDCOVER BOOKS
    data class NYTHardCoverBooksApiResponse(
        @SerializedName("status")
        val status: String,
        @SerializedName("num_results")
        val numResults: Int,
        @SerializedName("results")
        val results: NYTHardCoverBooksResults
    ) : Serializable

    data class NYTHardCoverBooksResults(
        @SerializedName("list_name")
        val listName: String,
        @SerializedName("books")
        val books: List<Book>
    ) : Serializable


    companion object {
        private const val BASE_URL = "https://api.nytimes.com/"

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BASIC
            }).build()

        fun create(): NewYorkTimesAPI {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewYorkTimesAPI::class.java)
        }
    }
}