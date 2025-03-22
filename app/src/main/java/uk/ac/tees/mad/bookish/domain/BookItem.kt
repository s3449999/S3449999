package uk.ac.tees.mad.bookish.domain

import uk.ac.tees.mad.bookish.data.BookEntity

data class BookItem(
    val id: String = "",
    val volumeInfo: VolumeInfo = VolumeInfo()
) {
    fun toBookEntity(isFavorite: Boolean): BookEntity {
        return BookEntity(
            id = id,
            title = volumeInfo.title,
            authors = volumeInfo.authors?.joinToString(", "),
            description = volumeInfo.description,
            thumbnailUrl = volumeInfo.imageLinks?.thumbnail,
            publishedDate = volumeInfo.publishedDate,
            categories = volumeInfo.categories?.joinToString(", "),
            rating = volumeInfo.averageRating,
            isFavorite = isFavorite
        )
    }
}