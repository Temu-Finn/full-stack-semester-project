package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.Message
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class MessageRepository(
    private val dataSource: DataSource,
) {
    fun save(message: Message): Message {
        val conversationId = message.conversationId
        val senderId = message.senderId
        val content = message.content
        val isRead = message.isRead

        dataSource.connection.use { conn ->
            val sql = "INSERT INTO messages (conversation_id, sender_id, content, is_read) VALUES (?, ?, ?, ?)"
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setInt(1, conversationId)
                stmt.setInt(2, senderId)
                stmt.setString(3, content)
                stmt.setBoolean(4, isRead)

                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Creating message failed, no rows affected.")
                }

                stmt.generatedKeys.use { keys ->
                    if (keys.next()) {
                        return message.copy(id = keys.getInt(1))
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
                        return mapRowToMessage(rows)
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
                        return mapRowToMessage(rows)
                    }
                }
            }
        }
        return null
    }

    fun getAllMessagesInConversation(conversationId: Int): MutableList<Message> {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM messages WHERE conversation_id = ? ORDER BY sent_at DESC"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, conversationId)
                stmt.executeQuery().use { rows ->
                    val messages = mutableListOf<Message>()
                    while (rows.next()) {
                        messages.add(mapRowToMessage(rows))
                    }
                    return messages
                }
            }
        }
    }

    fun mapRowToMessage(rs: ResultSet): Message =
        Message(
            id = rs.getInt("id"),
            conversationId = rs.getInt("conversation_id"),
            senderId = rs.getInt("sender_id"),
            content = rs.getString("content"),
            sentAt = rs.getTimestamp("sent_at").toInstant(),
            isRead = rs.getBoolean("is_read"),
        )
}
