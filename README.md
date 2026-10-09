# STADIO Library Book Rental & Reservation System

## 📖 Project Overview
The STADIO Library Book Rental & Reservation System is a native Android application developed as part of a third-year mobile development module. It enables users to browse a catalog of books, reserve titles, manage their active rentals (renew, return, or cancel), and seamlessly track return deadlines. 

The application has been built from the ground up utilizing modern Android development practices, emphasizing a clean, responsive, and reactive user interface without the use of legacy XML layouts.

## ✨ Features
*   **Reactive UI:** Real-time UI updates powered by Jetpack Compose and Kotlin Flows. Returning a book immediately updates its catalog status.
*   **Full CRUD Functionality:** Powered by a local SQLite Room Database to handle inserting, reading, updating, and deleting book and reservation records.
*   **Search & Filter:** Search for books by title or author, and filter by "All", "Available", or "Borrowed".
*   **Reservation Management:** Users can select rental durations (7, 14, or 21 days), renew existing rentals, or cancel pending pick-ups.
*   **Single-Activity Architecture:** Smooth, fragment-less navigation utilizing Jetpack Navigation Compose.

## 🛠️ Architecture & Tech Stack
This project strictly follows the **MVVM (Model-View-ViewModel)** architectural pattern to ensure clean separation of concerns:

*   **UI Layer (`ui/`)**: Built entirely with **Jetpack Compose (Material 3)**. The UI observes state changes reactively via `StateFlow` and `collectAsStateWithLifecycle()`.
*   **Presentation Layer (`ui/LibraryViewModel`)**: Manages UI state, handles user intents, and communicates with the domain layer. Runs all asynchronous tasks within `viewModelScope`.
*   **Domain Layer (`domain/LibraryRepository`)**: Acts as a single source of truth, abstracting the data layer and ensuring all database transactions execute safely off the Main Thread using Coroutine `Dispatchers.IO`.
*   **Data Layer (`data/`)**: Implemented using **Room Database**. It features two relational entities (`BookEntity` and `BookingEntity`) connected via Foreign Keys with cascading deletes.

**Libraries Used:**
*   Kotlin & Kotlin Coroutines / Flow
*   Jetpack Compose (UI, Material 3, Tooling)
*   Jetpack Navigation Compose
*   Jetpack Lifecycle (ViewModel, Runtime Compose)
*   Room Database & KSP (Kotlin Symbol Processing)

## 🚀 Setup & Installation Instructions

### Prerequisites
*   **Android Studio**: Ladybug (or newer recommended).
*   **JDK**: Version 17+.
*   **Device**: Android Emulator or physical device running Android API 26 or higher.

### Running the App
1.  **Clone the repository:**
    ```bash
    git clone <your_github_repo_link_here>
    ```
2.  **Open in Android Studio:**
    *   Launch Android Studio and select **File > Open**.
    *   Navigate to the cloned `STADIO_Library` directory and select it.
3.  **Sync Gradle:**
    *   Wait for Android Studio to automatically download dependencies and sync the Gradle files.
4.  **Run the Application:**
    *   Select your emulator or connected physical device from the target dropdown menu in the top toolbar.
    *   Click the green **Run** (Play) button (or press `Shift + F10`).

*Note: The local Room database is pre-populated with sample books and reservations on its initial creation via `RoomDatabase.Callback()`.*

## 📸 Screenshots
*(Add your screenshots or GIFs here showing the Catalog, Reservation flow, and My Reservations screen)*

---
**Developed for Mobile Application Development (MADB372/SF1)**
