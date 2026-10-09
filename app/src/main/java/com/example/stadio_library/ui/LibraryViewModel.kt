package com.example.stadio_library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stadio_library.data.BookEntity
import com.example.stadio_library.data.BookingWithBook
import com.example.stadio_library.domain.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: LibraryRepository) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filter = MutableStateFlow(FilterType.ALL)
    val filter: StateFlow<FilterType> = _filter

    val books: StateFlow<List<BookEntity>> = combine(
        _searchQuery,
        _filter,
        repository.allBooks
    ) { query, filter, allBooks ->
        var list = allBooks
        if (query.isNotBlank()) {
            list = list.filter { it.title.contains(query, ignoreCase = true) || it.author.contains(query, ignoreCase = true) }
        }
        when (filter) {
            FilterType.ALL -> list
            FilterType.AVAILABLE -> list.filter { it.isAvailable }
            FilterType.BORROWED -> list.filter { !it.isAvailable }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBookings: StateFlow<List<BookingWithBook>> = repository.activeBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilter(filterType: FilterType) {
        _filter.value = filterType
    }

    fun reserveBook(bookId: Long, durationDays: Int) {
        viewModelScope.launch {
            repository.reserveBook(bookId, durationDays)
        }
    }

    fun returnBook(bookingId: Long, bookId: Long) {
        viewModelScope.launch {
            repository.returnBook(bookingId, bookId)
        }
    }

    fun renewBooking(bookingId: Long, currentDeadline: Long) {
        viewModelScope.launch {
            repository.renewBooking(bookingId, currentDeadline)
        }
    }
    
    fun getBookById(bookId: Long): StateFlow<BookEntity?> {
        return repository.getBookById(bookId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }
}

enum class FilterType {
    ALL, AVAILABLE, BORROWED
}

class LibraryViewModelFactory(private val repository: LibraryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LibraryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
