package uk.ac.tees.mad.bookish.data

import com.google.firebase.firestore.FirebaseFirestore
import uk.ac.tees.mad.bookish.domain.BooksResponse

class BooksRepository(
    private val api: GoogleBooksApi = NetworkProvider.provideGoogleBooksApi()
) {
    private val favoritesCollection = FirebaseFirestore.getInstance().collection("favorites")

    suspend fun searchBooks(query: String, page: Int = 0): Result<BooksResponse> {
        return try {
            val response = api.searchBooks(query, page * 20)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}