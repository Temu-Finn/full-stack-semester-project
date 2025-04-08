package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.Conversation
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class ConversationRepository(private val dataSource: DataSource) {

    fun save(conversation: Conversation): Conversation {
        val itemId = conversation.itemId
        val buyerId = conversation.buyerId
        dataSource.connection.use { conn ->
            val sql = "INSERT INTO conversations (item_id, buyer_id) VALUES (?, ?)"
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setInt(1, itemId)
                stmt.setInt(2, buyerId)

                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Creating conversation failed, no rows affected.")
                }

                stmt.generatedKeys.use { keys ->
                    if (keys.next()) {
                        return Conversation(
                            id = keys.getInt(1),
                            itemId = itemId,
                            buyerId = buyerId,
                            createdAt = conversation.createdAt,
                            updatedAt = conversation.updatedAt
                        )
                    } else {
                        throw RuntimeException("Creating conversation failed, no ID obtained.")
                    }
                }
            }
        }
    }

    fun findConversationById(id: Int): Conversation? {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM conversations WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return Conversation(
                            id = rows.getInt("id"),
                            itemId = rows.getInt("item_id"),
                            buyerId = rows.getInt("buyer_id"),
                            createdAt = rows.getTimestamp("created_at"),
                            updatedAt = rows.getTimestamp("updated_at")
                        )
                    }
                }
            }
        }

        return null
    }

    fun delete(id: Int) {
        dataSource.connection.use { conn ->
            val sql = "DELETE FROM conversations WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                var affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Deleting conversation failed, no rows affected.")
                }
            }
        }
    }
}