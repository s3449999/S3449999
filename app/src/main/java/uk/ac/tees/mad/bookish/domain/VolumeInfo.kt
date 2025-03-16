package uk.ac.tees.mad.bookish.domain

data class VolumeInfo(
    val title: String = "",
    val authors: List<String>? = emptyList(),
    val description: String? = "",
    val imageLinks: ImageLinks? = ImageLinks(),
    val publishedDate: String? = "",
    val categories: List<String>? = emptyList(),
    val averageRating: Float? = 0f
)

data class ImageLinks(
    val thumbnail: String? = "",
    val smallThumbnail: String? = ""
)