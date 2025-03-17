package uk.ac.tees.mad.bookish.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import uk.ac.tees.mad.bookish.data.BooksRepository
import uk.ac.tees.mad.bookish.domain.BookItem

class FavoritesViewModel(
    private val booksRepository: BooksRepository = BooksRepository()
) : ViewModel() {
    private val _favoritesState = MutableStateFlow<FavoritesState>(FavoritesState.Loading)
    val favoritesState = _favoritesState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            booksRepository.getFavorites()
                .catch { e ->
                    _favoritesState.value =
                        FavoritesState.Error(e.message ?: "Failed to load favorites")
                }
                .collect { favorites ->
                    _favoritesState.value = if (favorites.isEmpty()) {
                        FavoritesState.Empty
                    } else {
                        FavoritesState.Success(favorites)
                    }
                }
        }
    }

    fun removeFromFavorites(bookId: String) {
        viewModelScope.launch {
            booksRepository.removeFromFavorites(bookId)
        }
    }
}

sealed class FavoritesState {
    object Loading : FavoritesState()
    object Empty : FavoritesState()
    data class Success(val books: List<BookItem>) : FavoritesState()
    data class Error(val message: String) : FavoritesState()
}