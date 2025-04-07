package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.Image
import org.springframework.stereotype.Repository

@Repository
class ImageRepository {
    fun save(image: Image): Image {
        return image
    }
}