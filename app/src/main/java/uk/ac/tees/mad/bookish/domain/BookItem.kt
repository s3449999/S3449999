package uk.ac.tees.mad.bookish.domain

data class BookItem(
    val id: String = "",
    val volumeInfo: VolumeInfo = VolumeInfo()
)