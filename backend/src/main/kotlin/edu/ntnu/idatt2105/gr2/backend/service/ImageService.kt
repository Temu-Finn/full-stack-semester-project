package edu.ntnu.idatt2105.gr2.backend.service

import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import edu.ntnu.idatt2105.gr2.backend.model.Image
import edu.ntnu.idatt2105.gr2.backend.repository.ImageRepository
import org.springframework.web.multipart.MultipartFile

@Service
class ImageService {
    private val logger = LoggerFactory.getLogger(ImageService::class.java)

    fun uploadImage(image: MultipartFile): Image {
        logger.info("Uploading image: ${image.originalFilename}")
        return imageRepository.save(image)
    }
}
