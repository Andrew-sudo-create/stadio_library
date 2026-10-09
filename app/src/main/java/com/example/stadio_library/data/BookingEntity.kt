package com.example.stadio_library.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class BookingStatus {
    PENDING, ACTIVE, RETURNED
}

@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["bookId"],
            childColumns = ["bookOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val bookingId: Long = 0,
    val bookOwnerId: Long,
    val userName: String,
    val bookingDate: Long,
    val returnDeadline: Long,
    val status: BookingStatus = BookingStatus.PENDING
)
