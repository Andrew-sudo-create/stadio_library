package com.example.stadio_library.domain

import com.example.stadio_library.data.BookEntity
import com.example.stadio_library.data.BookingEntity
import com.example.stadio_library.data.BookingStatus
import com.example.stadio_library.data.BookingWithBook
import com.example.stadio_library.data.LibraryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LibraryRepository(private val dao: LibraryDao) {
    val allBooks: Flow<List<BookEntity>> = dao.getAllBooks()
    
    fun searchBooks(query: String): Flow<List<BookEntity>> {
        return dao.searchBooks(query)
    }

    val activeBookings: Flow<List<BookingWithBook>> = dao.getActiveBookingsWithBooks()

    suspend fun reserveBook(bookId: Long, durationDays: Int, userName: String = "Current User") = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        val deadline = currentTime + durationDays * 24L * 60 * 60 * 1000
        val booking = BookingEntity(
            bookOwnerId = bookId,
            userName = userName,
            bookingDate = currentTime,
            returnDeadline = deadline,
            status = BookingStatus.PENDING
        )
        dao.insertBooking(booking)
        dao.updateBookAvailability(bookId, false)
    }

    suspend fun returnBook(bookingId: Long, bookId: Long) = withContext(Dispatchers.IO) {
        dao.updateBookingStatus(bookingId, BookingStatus.RETURNED)
        dao.updateBookAvailability(bookId, true)
    }

    suspend fun renewBooking(bookingId: Long, currentDeadline: Long, additionalDays: Int = 7) = withContext(Dispatchers.IO) {
        val newDeadline = currentDeadline + additionalDays * 24L * 60 * 60 * 1000
        dao.extendRental(bookingId, newDeadline)
    }
    
    suspend fun cancelBooking(bookingId: Long, bookId: Long) = withContext(Dispatchers.IO) {
        dao.cancelPendingBooking(bookingId)
        dao.updateBookAvailability(bookId, true)
    }
    
    fun getBookById(bookId: Long): Flow<BookEntity?> {
        return dao.getBookById(bookId)
    }
}
