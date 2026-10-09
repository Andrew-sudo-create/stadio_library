package com.example.stadio_library.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromBookingStatus(status: BookingStatus): String {
        return status.name
    }

    @TypeConverter
    fun toBookingStatus(name: String): BookingStatus {
        return BookingStatus.valueOf(name)
    }
}
