package com.example.albumapp.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.albumapp.ui.screens.createNewAlbum.ImageSavingException
import java.io.File
import java.io.IOException

fun saveImagePathLocally(path: String, context: Context, tableId: Int, aim: String): Result<Uri> {
    val uri: Uri
    return try {
        uri = if (path.isNotEmpty()) {
            Uri.parse(path)
        } else {
            throw IllegalArgumentException("Image cover URI is empty") // Throw exception for empty URI
        }
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "${aim}_$tableId.png")

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        Result.success(Uri.fromFile(file))
    } catch (e: IOException) {
        Result.failure(ImageSavingException("Failed to save image: ${e.message}"))
    }
}