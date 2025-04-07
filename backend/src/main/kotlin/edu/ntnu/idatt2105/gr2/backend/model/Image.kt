package edu.ntnu.idatt2105.gr2.backend.model


data class Image(
    val id: Int = -1,
    val data: ByteArray,
    val fileType: String,
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (id != other.id) return false
        if (!data.contentEquals(other.data)) return false
        if (fileType != other.fileType) return false
        if (altText != other.altText) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + data.contentHashCode()
        result = 31 * result + fileType.hashCode()
        result = 31 * result + (altText?.hashCode() ?: 0)
        return result
    }
}