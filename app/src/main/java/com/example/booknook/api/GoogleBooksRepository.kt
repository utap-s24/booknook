package com.example.booknook.api

class GoogleBooksRepository(private val googleBooksAPI: GoogleBooksAPI) {
    private fun unpackBookResultsData(googleBooksResponse : GoogleBooksAPI.GoogleBooksAPIResponse)
        : List<GoogleBook> {
        val books = mutableListOf<GoogleBook>()
        googleBooksResponse.items.forEach {
            books.add(it.volumeInfo)
        }
        return books
    }
    suspend fun getBooksFromSearch(search: String): List<GoogleBook> {
        val response = googleBooksAPI.getBooksFromSearch(search, 40)
        return unpackBookResultsData(response)
    }
}