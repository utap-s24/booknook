package com.example.booknook.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GoogleBook (
    @SerializedName("title")
    val title: String,
    @SerializedName("subtitle")
    val subtitle: String?,
    @SerializedName("authors")
    val authors: List<String>?,
    @SerializedName("publishedDate")
    val datePublished: String?,
    @SerializedName("industryIdentifiers")
    val isbn: List<IsbnValue>?,
    @SerializedName("pageCount")
    val pageCount: Int,
    @SerializedName("description")
    val description: String?,
    @SerializedName("imageLinks")
    val imageData: GoogleImageData?,
    @SerializedName("language")
    val language: String?,
    @SerializedName("categories")
    val genres: List<String>?,
) : Serializable

data class GoogleImageData (
    @SerializedName("thumbnail")
    val bookCover: String,
    @SerializedName("smallThumbnail")
    val smallBookCover: String,
) : Serializable

data class IsbnValue(
    @SerializedName("type")
    val isbnType: String,
    @SerializedName("identifier")
    val identifier: String
) : Serializable