package uk.ac.tees.mad.bookish.domain

data class BooksResponse(
    val items: List<BookItem>,
    val totalItems: Int
)