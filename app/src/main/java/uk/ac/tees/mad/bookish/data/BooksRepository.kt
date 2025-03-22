package uk.ac.tees.mad.bookish.data

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.bookish.domain.BookItem
import uk.ac.tees.mad.bookish.domain.BooksResponse

class BooksRepository(
    context: Context
) {
    private val api: GoogleBooksApi = NetworkProvider.provideGoogleBooksApi()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val bookDao: BookDao = DatabaseProvider.getDatabase(context).bookDao()

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

    suspend fun addToFavorites(book: BookItem) {
        bookDao.insertBook(book.toBookEntity(isFavorite = true))
        try {
            auth.currentUser?.uid?.let { userId ->
                firestore.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .document(book.id)
                    .set(book)
            }
        } catch (e: Exception) {
            Log.e("BooksRepository", "Failed to sync favorite to Firestore", e)
        }
    }

    suspend fun removeFromFavorites(bookId: String) {
        bookDao.updateFavoriteStatus(bookId, false)
        try {
            auth.currentUser?.uid?.let { userId ->
                firestore.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .document(bookId)
                    .delete()
            }
        } catch (e: Exception) {
            Log.e("BooksRepository", "Failed to remove favorite from Firestore", e)
        }
    }

    fun getFavorites(): Flow<List<BookItem>> = flow {

        bookDao.getFavoriteBooks()
            .map { entities ->
                entities.map { it.toBookItem() }
            }
            .collect { emit(it) }


        try {
            auth.currentUser?.uid?.let { userId ->
                val firestoreBooks = firestore.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { it.toObject(BookItem::class.java) }

                firestoreBooks.forEach { book ->
                    bookDao.insertBook(book.toBookEntity(isFavorite = true))
                }
            }
        } catch (e: Exception) {
            Log.e("BooksRepository", "Failed to sync with Firestore", e)
        }
    }

}