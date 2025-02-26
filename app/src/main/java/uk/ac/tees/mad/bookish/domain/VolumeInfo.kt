package uk.ac.tees.mad.bookish.domain

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val description: String?,
    val imageLinks: ImageLinks?,
    val publishedDate: String?,
    val categories: List<String>?,
    val averageRating: Float?
)

data class ImageLinks(
    val thumbnail: String?,
    val smallThumbnail: String?
)
