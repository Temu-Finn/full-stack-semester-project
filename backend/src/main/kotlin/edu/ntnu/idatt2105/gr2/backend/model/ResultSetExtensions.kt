package edu.ntnu.idatt2105.gr2.backend.model

import java.sql.ResultSet

fun ResultSet.getImageDataUrl(): String? {
    val imageBlobBytes = getBytes("image_data")
    val imageType = getString("file_type")
    return imageBlobBytes?.let { java.util.Base64.getEncoder().encodeToString(it) }
        ?.let { "data:$imageType;base64,$it" }
} 