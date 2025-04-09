package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.Message
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class MessageRepository (private val dataSource: DataSource) {

    fun save(message: Message): Message {
        val id = message.id
        val conversationId = message.conversationId
        val senderId = message.senderId
        val content = message.content
        val sentAt = message.sentAt
        val isRead = message.isRead

        dataSource.connection.use { conn ->
            val sql = "INSERT INTO messages (conversationId, senderId, content, sentAt, isRead) VALUES (?, ?, ?, ?, ?)"
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setInt(1, conversationId)
                stmt.setInt(2, senderId)
                stmt.setString(3, content)
                stmt.setTimestamp(4, sentAt)
                stmt.setBoolean(5, isRead)


                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Creating message failed, no rows affected.")
                }

                stmt.generatedKeys.use { keys ->
                    if (keys.next()) {
                        return Message(
                            id = keys.getInt(1),
                            conversationId = message.conversationId,
                            senderId = message.senderId,
                            content = message.content,
                            sentAt = message.sentAt,
                            isRead = message.isRead
                        )
                    } else {
                        throw RuntimeException("Creating message failed, no ID obtained.")
                    }
                }
            }
        }
    }

    fun findMessageById(id: Int): Message? {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM messages WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return Message(
                            id = rows.getInt("id"),
                            conversationId = rows.getInt("conversationId"),
                            senderId = rows.getInt("senderId"),
                            content = rows.getString("content"),
                            sentAt = rows.getTimestamp("sentAt"),
                            isRead = rows.getBoolean("isRead")
                        )
                    }
                }
            }
        }
        return null
    }

    fun getLatestMessage(conversationId: Int): Message? {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM messages WHERE conversation_id = ? ORDER BY sent_at DESC LIMIT 1"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, conversationId)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return Message(
                            id = rows.getInt("id"),
                            conversationId = rows.getInt("conversationId"),
                            senderId = rows.getInt("senderId"),
                            content = rows.getString("content"),
                            sentAt = rows.getTimestamp("sentAt"),
                            isRead = rows.getBoolean("isRead")
                        )
                    }
                }
            }
        }
        return null
    }
}