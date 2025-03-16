package uk.ac.tees.mad.bookish.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    fun getFavorites(): Flow<List<BookItem>> = callbackFlow {
        auth.currentUser?.uid?.let { userId ->
            val subscription = favoritesCollection.document(userId)
                .collection("books")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val books = snapshot?.documents?.mapNotNull {
                        it.toObject(BookItem::class.java)
                    } ?: emptyList()
                    trySend(books)
                }

            awaitClose { subscription.remove() }
        } ?: run {
            trySend(emptyList())
            close()
        }
    }

}