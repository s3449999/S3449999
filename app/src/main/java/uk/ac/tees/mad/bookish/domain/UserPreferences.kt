package uk.ac.tees.mad.bookish.domain

data class UserPreferences(
    val userId: String,
    val name: String,
    val email: String,
    val photoUrl: String?,
)