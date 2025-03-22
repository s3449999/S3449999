package uk.ac.tees.mad.bookish.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.bookish.data.BooksRepository
import uk.ac.tees.mad.bookish.domain.BookItem

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val booksRepository: BooksRepository = BooksRepository(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _homeState = MutableStateFlow<HomeScreenState>(HomeScreenState.Initial)
    val homeState = _homeState.asStateFlow()

    private var searchJob: Job? = null

    init {
        fetchTrendingBooks()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            if (query.isNotEmpty()) {
                searchBooks(query)
            } else {
                fetchTrendingBooks()
            }
        }
    }

    private fun searchBooks(query: String) {
        viewModelScope.launch {
            _homeState.value = HomeScreenState.Loading
            val result = booksRepository.searchBooks(query)
            if (result.isSuccess) {
                _homeState.value = HomeScreenState.Success(
                    searchResults = result.getOrNull()?.items ?: emptyList(),
                    trendingBooks = emptyList()
                )
            } else {
                _homeState.value = HomeScreenState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown error occurred"
                )
            }
        }
    }

    private fun fetchTrendingBooks() {
        viewModelScope.launch {
            _homeState.value = HomeScreenState.Loading
            val categories = listOf("fiction", "science", "history", "biography")
            val trendingBooks = mutableListOf<BookItem>()

            categories.forEach { category ->
                val result = booksRepository.searchBooks(category, page = 0)
                when {
                    result.isSuccess -> {
                        trendingBooks.addAll(result.getOrNull()?.items ?: emptyList())
                        Log.d(
                            "HomeViewModel",
                            "Fetched ${result.getOrNull()?.items?.size} books for category: $category"
                        )
                    }

                    else -> {
                        _homeState.value = HomeScreenState.Error(
                            result.exceptionOrNull()?.message ?: "Failed to fetch trending books"
                        )
                        return@launch
                    }
                }
            }

            _homeState.value = HomeScreenState.Success(
                searchResults = emptyList(),
                trendingBooks = trendingBooks.shuffled()
            )
        }
    }
}

sealed class HomeScreenState {
    data object Initial : HomeScreenState()
    data object Loading : HomeScreenState()
    data class Success(
        val searchResults: List<BookItem>,
        val trendingBooks: List<BookItem>
    ) : HomeScreenState()

    data class Error(val message: String) : HomeScreenState()
}