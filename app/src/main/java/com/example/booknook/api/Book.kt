package com.example.booknook.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Book(
    @SerializedName("amazon_product_url")
    val amazonProductUrl: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("book_image")
    val imageUrl: String,
    @SerializedName("book_image_width")
    val bookImageWidth: Int,
    @SerializedName("book_image_height")
    val bookImageHeight: Int,
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("primary_isbn10")
    val isbn10: String,
    @SerializedName("primary_isbn13")
    val isbn13: String,
    @SerializedName("rank")
    val nytRank: Int,
    @SerializedName("buy_links")
    val buyLinks: List<ProductLinks>
) : Serializable

data class ProductLinks(
    @SerializedName("name")
    val websiteName: String,
    @SerializedName("url")
    val productUrl: String
)