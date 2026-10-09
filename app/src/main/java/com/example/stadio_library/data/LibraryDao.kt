package com.example.stadio_library.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooks(books: List<BookEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooking(booking: BookingEntity): Long

    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Transaction
    @Query("SELECT * FROM bookings WHERE status != 'RETURNED'")
    fun getActiveBookingsWithBooks(): Flow<List<BookingWithBook>>

    @Query("UPDATE bookings SET returnDeadline = :newDeadline WHERE bookingId = :bookingId")
    fun extendRental(bookingId: Long, newDeadline: Long): Int

    @Query("UPDATE bookings SET status = :status WHERE bookingId = :bookingId")
    fun updateBookingStatus(bookingId: Long, status: BookingStatus): Int

    @Query("UPDATE books SET isAvailable = :isAvailable WHERE bookId = :bookId")
    fun updateBookAvailability(bookId: Long, isAvailable: Boolean): Int

    @Query("DELETE FROM bookings WHERE bookingId = :bookingId AND status = 'PENDING'")
    fun cancelPendingBooking(bookingId: Long): Int

    @Query("SELECT * FROM books WHERE bookId = :bookId")
    fun getBookById(bookId: Long): Flow<BookEntity?>
}
