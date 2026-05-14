package com.example.auris // Ensure this matches your package

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auris.data.AurisDatabase
import com.example.auris.data.BookEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AurisDatabase.getDatabase(application)
    private val dao = db.bookDao()

    // This StateFlow automatically updates the UI whenever the database changes!
    val books: StateFlow<List<BookEntity>> = dao.getAllBooks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun handlePdfSelection(uri: Uri) {
        val context = getApplication<Application>().applicationContext

        // CRITICAL: We must take permanent read permissions, otherwise Android
        // revokes access to the PDF the moment the app closes.
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
        context.contentResolver.takePersistableUriPermission(uri, takeFlags)

        // Extract the actual file name from the URI
        var fileName = "Unknown Document"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                    // Remove the ".pdf" extension for a cleaner UI
                    fileName = fileName.removeSuffix(".pdf")
                }
            }
        }

        // Save it to our Room database
        viewModelScope.launch {
            dao.insertBook(BookEntity(title = fileName, fileUri = uri.toString()))
        }
    }
}