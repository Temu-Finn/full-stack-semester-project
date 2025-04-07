package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.dto.ItemCard
import edu.ntnu.idatt2105.gr2.backend.model.getImageDataUrl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class ItemRepository (
    private val dataSource: DataSource,
) {

    fun findRecommendedItems(userId: Int): List<ItemCard> {
        val sql = """
            SELECT
                i.id,
                i.title,
                i.price,
                pc.municipality,
                ii.image_data,
                ii.file_type
            FROM
                items i
            JOIN
                postal_codes pc ON i.postal_code = pc.postal_code
            LEFT JOIN
                item_images ii ON ii.id = i.primary_image_id
            ORDER BY
                RAND() -- Change to a more sophisticated recommendation algorithm
            LIMIT 10; -- Limit the number of items
        """

        val items = mutableListOf<ItemCard>()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        items.add(mapRowToItemCard(rs))
                    }
                }
            }
        }

        return items
    }

    private fun mapRowToItemCard(rs: ResultSet): ItemCard {
        return ItemCard(
            itemId = rs.getInt("id"),
            title = rs.getString("title"),
            price = rs.getDouble("price"),
            municipality = rs.getString("municipality"),
            imageBase64 = rs.getImageDataUrl()
        )
    }
}