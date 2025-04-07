package edu.ntnu.idatt2105.gr2.backend.model


class Image (
    val id: Int = -1,
    val data: ByteArray,
    val fileType: String,
    val isPrimary: Boolean = false,
    val altText: String? = null,
) {
    init {
        require(data.isNotEmpty()) { "Image data cannot be empty" }
        require(fileType.isNotBlank()) { "File type cannot be blank" }
    }

    fun base64(): String {
        return java.util.Base64.getEncoder().encodeToString(data)
            .let { "data:$fileType;base64,$it" }
    }
}