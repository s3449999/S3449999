package uk.ac.tees.mad.bookish.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import uk.ac.tees.mad.bookish.domain.BookItem
import uk.ac.tees.mad.bookish.domain.ImageLinks
import uk.ac.tees.mad.bookish.domain.VolumeInfo

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val authors: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val publishedDate: String?,
    val categories: String?,
    val rating: Float?,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toBookItem() = BookItem(
        id = id,
        volumeInfo = VolumeInfo(
            title = title,
            authors = authors?.split(", "),
            description = description,
            imageLinks = ImageLinks(thumbnail = thumbnailUrl),
            publishedDate = publishedDate,
            categories = categories?.split(", "),
            averageRating = rating
        )
    )

}