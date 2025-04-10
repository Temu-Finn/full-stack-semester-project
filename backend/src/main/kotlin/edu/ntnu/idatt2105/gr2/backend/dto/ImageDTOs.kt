package edu.ntnu.idatt2105.gr2.backend.dto

import org.springframework.web.multipart.MultipartFile

/**
 * Represents an item image with its data and metadata
 */
data class ImageResponse(
    val id: Int,
    val dataURL: String, // base64 encoded image
    val altText: String? = null,
)

data class CreateImageRequest(
    val imageFile: MultipartFile,
    val altText: String? = null,
)
