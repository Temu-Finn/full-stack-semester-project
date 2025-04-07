package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.CreateImageRequest
import edu.ntnu.idatt2105.gr2.backend.dto.ImageResponse
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import edu.ntnu.idatt2105.gr2.backend.model.Image
import edu.ntnu.idatt2105.gr2.backend.repository.ImageRepository

@Service
class ImageService(
    private val imageRepository: ImageRepository
) {
    private val logger = LoggerFactory.getLogger(ImageService::class.java)

    fun upload(itemId: Int, imageRequest: CreateImageRequest): ImageResponse {
        val imageFile = imageRequest.imageFile
        val altText = imageRequest.altText

        logger.info("Uploading image: ${imageFile.originalFilename}")
        val fileType = imageFile.contentType ?: "application/octet-stream"
        val imageData = imageFile.bytes
        val image = Image(
            itemId = itemId,
            data = imageData,
            fileType = fileType,
            altText = altText
        )

        if (imageRepository.save(image) != image) {
            throw IllegalStateException("Image upload failed")
        }

        return ImageResponse(
            id = image.id,
            dataURL = image.base64()
        )
    }
}
