package edu.ntnu.idatt2105.gr2.backend.model

import edu.ntnu.idatt2105.gr2.backend.dto.Location
import java.sql.ResultSet

fun ResultSet.getImageDataUrl(): String? {
    val imageBlobBytes = getBytes("image_data")
    val imageType = getString("file_type")
    return imageBlobBytes?.let { java.util.Base64.getEncoder().encodeToString(it) }
        ?.let { "data:$imageType;base64,$it" }
}

fun ResultSet.getDoubleOrNull(columnLabel: String): Double? {
    val value = getDouble(columnLabel)
    return if (wasNull()) null else value
}

fun ResultSet.getIntOrNull(columnLabel: String): Int? {
    val value = getInt(columnLabel)
    return if (wasNull()) null else value
}

fun ResultSet.getLocation(): Location? {
    val latitude = getDouble("latitude")
    val longitude = getDouble("longitude")
    return if (wasNull()) null else Location(latitude, longitude)
}

fun ResultSet.getItemStatus(): ItemStatus {
    val status = getString("status")
    return ItemStatus.fromString(status)
}