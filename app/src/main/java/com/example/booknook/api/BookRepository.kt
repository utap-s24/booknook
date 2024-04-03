package com.example.booknook.api

class BookRepository(private val newYorkTimesAPI: NewYorkTimesAPI) {

    // returns books from all listings/genres
    private fun unpackBooksResponse(response: NewYorkTimesAPI.NYTBooksApiResponse): List<Book> {
        val books = mutableListOf<Book>()
        response.results.lists.forEach { list ->
            list.books.forEach {
                books.add(it)
            }
        }
        return books
    }

    suspend fun getBooks(apiKey: String): List<Book> {
        val response = newYorkTimesAPI.getBooks(apiKey)
        return unpackBooksResponse(response)
    }

    suspend fun getFictionBooks(apiKey: String): List<Book> {
        val response = newYorkTimesAPI.getHardCoverFictionBooks(apiKey)
        return response.results.books
    }
}