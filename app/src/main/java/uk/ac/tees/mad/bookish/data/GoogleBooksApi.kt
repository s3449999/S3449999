package uk.ac.tees.mad.bookish.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uk.ac.tees.mad.bookish.domain.BookItem
import uk.ac.tees.mad.bookish.domain.BooksResponse

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("startIndex") startIndex: Int = 0,
        @Query("maxResults") maxResults: Int = 20
    ): BooksResponse

    @GET("volumes/{bookId}")
    suspend fun getBookDetails(@Path("bookId") bookId: String): BookItem
}
