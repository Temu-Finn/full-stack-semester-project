package edu.ntnu.idatt2105.gr2.backend.service

import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import edu.ntnu.idatt2105.gr2.backend.model.Image
import edu.ntnu.idatt2105.gr2.backend.repository.ImageRepository
import org.springframework.web.multipart.MultipartFile

@Service
class ImageService(
    private val imageRepository: ImageRepository
) {
    private val logger = LoggerFactory.getLogger(ImageService::class.java)

    fun uploadImage(imageFile: MultipartFile, altText: String): Image {
        logger.info("Uploading image: ${imageFile.originalFilename}")
        val fileType = imageFile.contentType ?: "application/octet-stream"
        val imageData = imageFile.bytes
        val image = Image(
            data = imageData,
            fileType = fileType,
            altText = altText
        )
        return imageRepository.save()
    }
}
