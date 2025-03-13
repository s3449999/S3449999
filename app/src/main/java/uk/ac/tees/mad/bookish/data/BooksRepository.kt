package uk.ac.tees.mad.bookish.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import uk.ac.tees.mad.bookish.domain.BookItem
import uk.ac.tees.mad.bookish.domain.BooksResponse

class BooksRepository(
    private val api: GoogleBooksApi = NetworkProvider.provideGoogleBooksApi(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
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

    suspend fun getBookDetails(bookId: String): Result<BookItem> {
        return try {
            val response = api.getBookDetails(bookId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun addToFavorites(book: BookItem) {
        auth.currentUser?.uid?.let { userId ->
            favoritesCollection.document(userId)
                .collection("books")
                .document(book.id)
                .set(book)
        }
    }

    fun removeFromFavorites(bookId: String) {
        auth.currentUser?.uid?.let { userId ->
            favoritesCollection.document(userId)
                .collection("books")
                .document(bookId)
                .delete()
        }
    }

}