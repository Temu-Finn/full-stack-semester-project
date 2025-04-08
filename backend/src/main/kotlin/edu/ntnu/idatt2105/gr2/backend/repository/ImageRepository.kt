package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.Image
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class ImageRepository(private val dataSource: DataSource) {
    fun save(image: Image): Image {
        val sql = """
            INSERT INTO item_images (item_id, image_data, file_type, alt_text)
            VALUES (?, ?, ?, ?)
        """.trimIndent()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, image.itemId)
                stmt.setBytes(2, image.data)
                stmt.setString(3, image.fileType)
                stmt.setString(4, image.altText)

                stmt.executeUpdate()
                
                // Get the last inserted ID
                val rs = conn.prepareStatement("SELECT LAST_INSERT_ID()").executeQuery()
                if (rs.next()) {
                    val id = rs.getInt(1)
                    return image.copy(id = id)
                } else {
                    throw RuntimeException("Failed to save image, no ID obtained.")
                }
            }
        }
    }

    fun getById(id: Int): Image {
        val sql = "SELECT * FROM item_images WHERE id = ?"

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    return Image(
                        id = rs.getInt("id"),
                        itemId = rs.getInt("item_id"),
                        data = rs.getBytes("image_data"),
                        fileType = rs.getString("file_type"),
                        altText = rs.getString("alt_text")
                    )
                } else {
                    throw IllegalArgumentException("Image with ID $id not found")
                }
            }
        }
    }

    fun getByItemId(itemId: Int): List<Image> {
        val sql = "SELECT * FROM item_images WHERE item_id = ?"

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, itemId)
                val rs = stmt.executeQuery()
                val images = mutableListOf<Image>()
                while (rs.next()) {
                    images.add(
                        Image(
                            id = rs.getInt("id"),
                            itemId = rs.getInt("item_id"),
                            data = rs.getBytes("image_data"),
                            fileType = rs.getString("file_type"),
                            altText = rs.getString("alt_text")
                        )
                    )
                }
                return images
            }
        }
    }
}