package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.Image
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class ImageRepository(private val dataSource: DataSource) {
    fun save(image: Image): Image {
        val sql = """
            INSERT INTO item_images (image_data, file_type, alt_text)
            VALUES (?, ?, ?)
            RETURNING id
        """.trimIndent()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setBytes(1, image.data)
                stmt.setString(2, image.fileType)
                stmt.setString(3, image.altText)

                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val id = rs.getInt("id")
                    return image.copy(id = id)
                } else {
                    throw RuntimeException("Failed to save image, no ID obtained.")
                }
            }
        }
    }

    fun getById(id: Int): Image? {
        val sql = "SELECT * FROM item_images WHERE id = ?"

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                return if (rs.next()) {
                    Image(
                        id = rs.getInt("id"),
                        data = rs.getBytes("image_data"),
                        fileType = rs.getString("file_type"),
                        altText = rs.getString("alt_text")
                    )
                } else {
                    null
                }
            }
        }
    }
}