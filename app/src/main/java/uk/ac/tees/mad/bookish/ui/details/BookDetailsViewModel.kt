package uk.ac.tees.mad.bookish.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uk.ac.tees.mad.bookish.data.BooksRepository
import uk.ac.tees.mad.bookish.domain.BookItem

class BookDetailsViewModel(
    private val booksRepository: BooksRepository = BooksRepository()
) : ViewModel() {
    private val _bookState = MutableStateFlow<BookDetailsState>(BookDetailsState.Loading)
    val bookState = _bookState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    fun loadBookDetails(bookId: String) {
        viewModelScope.launch {
            try {
                val bookDetails = booksRepository.getBookDetails(bookId).getOrThrow()
                _bookState.value = BookDetailsState.Success(bookDetails)
                checkIfFavorite(bookId)
            } catch (e: Exception) {
                _bookState.value =
                    BookDetailsState.Error(e.message ?: "Failed to load book details")
            }
        }
    }

    private fun checkIfFavorite(bookId: String) {
        viewModelScope.launch {
            booksRepository.getFavorites()
                .map { favorites -> favorites.any { it.id == bookId } }
                .collect { isFavorite ->
                    _isFavorite.value = isFavorite
                }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentState = bookState.value
            if (currentState is BookDetailsState.Success) {
                val book = currentState.book
                if (_isFavorite.value) {
                    booksRepository.removeFromFavorites(book.id)
                } else {
                    booksRepository.addToFavorites(book)
                }
            }
        }
    }
}

sealed class BookDetailsState {
    object Loading : BookDetailsState()
    data class Success(val book: BookItem) : BookDetailsState()
    data class Error(val message: String) : BookDetailsState()
}
