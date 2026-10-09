package com.example.stadio_library.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [BookEntity::class, BookingEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun libraryDao(): LibraryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "library_database"
                )
                .fallbackToDestructiveMigration() // Added to fulfill project spec requirement
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.libraryDao())
                }
            }
        }

        fun populateDatabase(dao: LibraryDao) {
            val sampleBooks = listOf(
                BookEntity(title = "Principles of Economics", author = "Mankiw, N. G.", category = "Economics"),
                BookEntity(title = "Clean Architecture", author = "Martin, R. C.", category = "Computer Science", isAvailable = false),
                BookEntity(title = "Managerial Economics", author = "Salvatore, D.", category = "Economics", isAvailable = false),
                BookEntity(title = "Kotlin in Action", author = "Jemerov, D. & Isakova, S.", category = "Computer Science", isAvailable = false),
                BookEntity(title = "Business Research Methods", author = "Bryman, A.", category = "Business", isAvailable = false),
                BookEntity(title = "Educational Psychology", author = "Woolfolk, A.", category = "Education", isAvailable = false)
            )
            dao.insertBooks(sampleBooks)
            
            val currentTime = System.currentTimeMillis()
            val dayInMillis = 24L * 60 * 60 * 1000

            dao.insertBooking(BookingEntity(bookOwnerId = 4, userName = "User", bookingDate = currentTime, returnDeadline = currentTime + 9 * dayInMillis, status = BookingStatus.ACTIVE))
            dao.insertBooking(BookingEntity(bookOwnerId = 5, userName = "User", bookingDate = currentTime - 5 * dayInMillis, returnDeadline = currentTime + 1 * dayInMillis, status = BookingStatus.ACTIVE))
            dao.insertBooking(BookingEntity(bookOwnerId = 6, userName = "User", bookingDate = currentTime, returnDeadline = currentTime + 17 * dayInMillis, status = BookingStatus.PENDING))
            dao.insertBooking(BookingEntity(bookOwnerId = 2, userName = "User", bookingDate = currentTime, returnDeadline = currentTime + 14 * dayInMillis, status = BookingStatus.ACTIVE))
            dao.insertBooking(BookingEntity(bookOwnerId = 3, userName = "User", bookingDate = currentTime, returnDeadline = currentTime + 14 * dayInMillis, status = BookingStatus.ACTIVE))
        }
    }
}
