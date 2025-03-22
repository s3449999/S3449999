package uk.ac.tees.mad.bookish.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var INSTANCE: BookDatabase? = null

    fun getDatabase(context: Context): BookDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                BookDatabase::class.java,
                "bookish_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
