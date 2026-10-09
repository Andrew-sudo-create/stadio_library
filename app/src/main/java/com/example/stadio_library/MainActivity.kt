package com.example.stadio_library

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stadio_library.data.AppDatabase
import com.example.stadio_library.domain.LibraryRepository
import com.example.stadio_library.ui.LibraryApp
import com.example.stadio_library.ui.LibraryViewModel
import com.example.stadio_library.ui.LibraryViewModelFactory
import com.example.stadio_library.ui.theme.STADIO_LibraryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            STADIO_LibraryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val database = AppDatabase.getDatabase(context)
                    val repository = LibraryRepository(database.libraryDao())
                    val viewModel: LibraryViewModel = viewModel(
                        factory = LibraryViewModelFactory(repository)
                    )
                    LibraryApp(viewModel)
                }
            }
        }
    }
}
