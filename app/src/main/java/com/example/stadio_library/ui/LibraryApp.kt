package com.example.stadio_library.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stadio_library.data.BookEntity
import com.example.stadio_library.data.BookingWithBook
import com.example.stadio_library.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryApp(viewModel: LibraryViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceWhite,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Catalog") },
                    label = { Text("Catalog") },
                    selected = currentRoute == "catalog" || currentRoute?.startsWith("bookDetail") == true,
                    onClick = {
                        navController.navigate("catalog") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGold,
                        unselectedIconColor = TextGrey,
                        selectedTextColor = PrimaryGold,
                        unselectedTextColor = TextGrey,
                        indicatorColor = SurfaceWhite
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Reservations") },
                    label = { Text("Reservations") },
                    selected = currentRoute == "reservations",
                    onClick = {
                        navController.navigate("reservations") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGold,
                        unselectedIconColor = TextGrey,
                        selectedTextColor = PrimaryGold,
                        unselectedTextColor = TextGrey,
                        indicatorColor = SurfaceWhite
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Account") },
                    label = { Text("Account") },
                    selected = currentRoute == "account",
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGold,
                        unselectedIconColor = TextGrey,
                        selectedTextColor = PrimaryGold,
                        unselectedTextColor = TextGrey,
                        indicatorColor = SurfaceWhite
                    )
                )
            }
        },
        containerColor = BackgroundGrey
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "catalog",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("catalog") {
                CatalogScreen(viewModel = viewModel, onBookClick = { bookId ->
                    navController.navigate("bookDetail/$bookId")
                })
            }
            composable("reservations") {
                ReservationsScreen(viewModel = viewModel)
            }
            composable("bookDetail/{bookId}") { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull()
                if (bookId != null) {
                    BookDetailScreen(viewModel = viewModel, bookId = bookId, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(viewModel: LibraryViewModel, onBookClick: (Long) -> Unit) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Catalog", style = MaterialTheme.typography.headlineMedium)
                Text("STADIO LIBRARY", style = MaterialTheme.typography.labelSmall, color = TextGrey)
            }
            // Simple mock for Grid/List toggle
            Row(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(SurfaceWhite).padding(2.dp)) {
                Text("Grid", color = PrimaryGold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.background(Color(0xFFFCF5E3)).padding(horizontal = 12.dp, vertical = 6.dp))
                Text("List", color = TextGrey, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::updateSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search title or author", color = TextGrey) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGrey) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedBorderColor = PrimaryGold,
                unfocusedBorderColor = OutlineGrey
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterType.values().forEach { type ->
                val isSelected = filter == type
                Surface(
                    onClick = { viewModel.updateFilter(type) },
                    border = BorderStroke(1.dp, if (isSelected) PrimaryGold else OutlineGrey),
                    color = if (isSelected) Color(0xFFFCF5E3) else SurfaceWhite,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = type.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) PrimaryGold else TextGrey,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("${books.size} TITLES", style = MaterialTheme.typography.labelSmall, color = TextGrey)
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(books) { book ->
                BookCard(book, onClick = { onBookClick(book.bookId) })
            }
        }
    }
}

@Composable
fun BookCard(book: BookEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, OutlineGrey),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
                    .background(if (book.isAvailable) Color(0xFFFDECDA) else Color(0xFFF0F0F0)) // Placeholder for cover
            ) {
                Text(
                    text = "COVER",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGrey,
                    modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(SurfaceWhite.copy(alpha = 0.8f)).padding(2.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(book.title, style = MaterialTheme.typography.titleMedium.copy(color = TextDark))
            Spacer(modifier = Modifier.height(4.dp))
            Text(book.author, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(book.category.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextGrey)
            Spacer(modifier = Modifier.height(12.dp))
            
            val statusColor = if (book.isAvailable) AvailableGreen else BorrowedRed
            val statusText = if (book.isAvailable) "AVAILABLE" else "BORROWED"
            
            Surface(
                border = BorderStroke(1.dp, statusColor),
                color = SurfaceWhite,
                shape = RoundedCornerShape(2.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(viewModel: LibraryViewModel, bookId: Long, onBack: () -> Unit) {
    val bookState = remember(bookId) { viewModel.getBookById(bookId) }.collectAsStateWithLifecycle()
    val book = bookState.value ?: return

    var showReservationSheet by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("CATALOG / ${book.category.uppercase()}", style = MaterialTheme.typography.labelSmall, color = TextGrey) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextGrey)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundGrey)
        )
        
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(32.dp)
                    .background(Color(0xFFE0E0E0)) // Frame for cover
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp).background(if (book.isAvailable) Color(0xFFFDECDA) else Color(0xFFF0F0F0)))
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(book.title, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(book.author, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    border = BorderStroke(1.dp, if (book.isAvailable) AvailableGreen else BorrowedRed),
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text(
                        text = if (book.isAvailable) "AVAILABLE" else "BORROWED",
                        color = if (book.isAvailable) AvailableGreen else BorrowedRed,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = OutlineGrey)
            DetailRow("Category", book.category)
            HorizontalDivider(color = OutlineGrey)
            DetailRow("Shelf", "HB 172.5 MAN")
            HorizontalDivider(color = OutlineGrey)
            DetailRow("Copies", "3 of 5 on shelf")
            HorizontalDivider(color = OutlineGrey)
            DetailRow("Max duration", "21 days")
            HorizontalDivider(color = OutlineGrey)
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Set text for first-year economics modules. Prescribed for ECO101 and ECO112; the ninth edition matches the current study guide chapter numbering. Reserved copies are held at the pickup desk for 48 hours.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextDark
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            if (book.isAvailable) {
                Button(
                    onClick = { showReservationSheet = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = SurfaceWhite),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Reserve Copy")
                }
            }
        }
    }

    if (showReservationSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReservationSheet = false },
            containerColor = SurfaceWhite
        ) {
            ReservationSheetContent(
                book = book,
                onCancel = { showReservationSheet = false },
                onReserve = { duration ->
                    viewModel.reserveBook(book.bookId, duration)
                    showReservationSheet = false
                    onBack()
                }
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextGrey)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = TextDark)
    }
}

@Composable
fun ReservationSheetContent(book: BookEntity, onCancel: () -> Unit, onReserve: (Int) -> Unit) {
    var selectedDuration by remember { mutableStateOf(14) }
    
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val currentTime = System.currentTimeMillis()

    Column(modifier = Modifier.padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp).background(Color(0xFFFDECDA))) // Mock cover
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(book.title, style = MaterialTheme.typography.titleMedium.copy(color = TextDark))
                Text("${book.author} · 9th ed.", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    border = BorderStroke(1.dp, AvailableGreen),
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text(
                        text = "3 COPIES AVAILABLE",
                        color = AvailableGreen,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp).padding(2.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = OutlineGrey)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("RENTAL DURATION", style = MaterialTheme.typography.labelSmall, color = TextGrey)
        Spacer(modifier = Modifier.height(8.dp))
        
        listOf(7, 14, 21).forEach { days ->
            val dueText = "due " + dateFormat.format(Date(currentTime + days * 24L * 60 * 60 * 1000))
            Row(
                modifier = Modifier.fillMaxWidth().clickable { selectedDuration = days }.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedDuration == days,
                    onClick = { selectedDuration = days },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                )
                Text("$days days", style = MaterialTheme.typography.bodyLarge, color = TextDark)
                Spacer(modifier = Modifier.weight(1f))
                Text(dueText, style = MaterialTheme.typography.bodySmall)
            }
            HorizontalDivider(color = OutlineGrey)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("PICKUP", style = MaterialTheme.typography.labelSmall, color = TextGrey)
            Text("Main Campus Desk B", style = MaterialTheme.typography.bodyMedium, color = TextDark)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(50.dp),
                border = BorderStroke(1.dp, OutlineGrey),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = { onReserve(selectedDuration) },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceWhite, contentColor = PrimaryGold),
                border = BorderStroke(1.dp, PrimaryGold),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Reserve")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ReservationsScreen(viewModel: LibraryViewModel) {
    val bookings by viewModel.activeBookings.collectAsStateWithLifecycle()
    val activeCount = bookings.size

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("My Reservations", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("$activeCount ACTIVE", style = MaterialTheme.typography.labelSmall, color = TextGrey)
            Spacer(modifier = Modifier.width(16.dp))
            Text("1 PENDING PICKUP", style = MaterialTheme.typography.labelSmall, color = TextGrey)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(bookings) { bookingInfo ->
                ReservationCard(
                    bookingWithBook = bookingInfo,
                    onRenew = { viewModel.renewBooking(bookingInfo.booking.bookingId, bookingInfo.booking.returnDeadline) },
                    onReturn = { viewModel.returnBook(bookingInfo.booking.bookingId, bookingInfo.booking.bookOwnerId) }
                )
            }
        }
    }
}

@Composable
fun ReservationCard(bookingWithBook: BookingWithBook, onRenew: () -> Unit, onReturn: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val bookingDate = dateFormat.format(Date(bookingWithBook.booking.bookingDate))
    val dueDate = dateFormat.format(Date(bookingWithBook.booking.returnDeadline))
    val daysLeft = ((bookingWithBook.booking.returnDeadline - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)

    val isUrgent = daysLeft <= 3
    val borderColor = if (isUrgent) BorrowedRed else OutlineGrey

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(50.dp).background(Color(0xFFFDECDA))) // Mock cover
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(bookingWithBook.book.title, style = MaterialTheme.typography.titleMedium.copy(color = TextDark))
                        Text(bookingWithBook.book.author, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("$daysLeft", style = MaterialTheme.typography.headlineMedium, color = if (isUrgent) BorrowedRed else AvailableGreen)
                    Text(if (daysLeft == 1L) "DAY LEFT" else "DAYS LEFT", color = if (isUrgent) BorrowedRed else TextGrey, style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text("Reserved $bookingDate", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Due $dueDate", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = OutlineGrey)
            Spacer(modifier = Modifier.height(12.dp))
            if (isUrgent) {
                Text("Overdue fine of R5.00 per day applies from 09 Sep.", color = BorrowedRed, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(12.dp))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onRenew,
                    modifier = Modifier.weight(1f).height(44.dp),
                    border = BorderStroke(1.dp, PrimaryGold),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGold)
                ) {
                    Text("Renew")
                }
                OutlinedButton(
                    onClick = onReturn,
                    modifier = Modifier.weight(1f).height(44.dp),
                    border = BorderStroke(1.dp, OutlineGrey),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGrey)
                ) {
                    Text("Return")
                }
            }
        }
    }
}
