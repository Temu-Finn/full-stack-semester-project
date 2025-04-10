package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.User
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

@Repository
class UserRepository(
    private val dataSource: DataSource,
) {
    fun save(user: User): User {
        val email = user.email
        val name = user.name
        val password = user.password
        dataSource.connection.use { conn ->
            val sql = "INSERT INTO users (email, name, password) VALUES (?, ?, ?)"
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setString(1, email)
                stmt.setString(2, name)
                stmt.setString(3, password)

                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Creating user failed, no rows affected.")
                }

                stmt.generatedKeys.use { keys ->
                    if (keys.next()) {
                        return user.copy(id = keys.getInt(1))
                    } else {
                        throw RuntimeException("Creating user failed, no ID obtained.")
                    }
                }
            }
        }
    }

    fun findById(id: Int): User? {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM users WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) return mapRowToUser(rs)
                }
            }
        }
        return null
    }

    fun findByEmail(email: String): User? {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM users WHERE email = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return mapRowToUser(rows)
                    }
                }
            }
        }

        return null
    }

    fun mapRowToUser(rs: ResultSet): User =
        User(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            email = rs.getString("email"),
            joinedAt = rs.getTimestamp("created_at").toInstant(),
            isAdmin = rs.getBoolean("is_admin"),
            passwordHashed = rs.getString("password"),
        )

    fun findUserById(id: Int): User? {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM users WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return User(
                            rows.getInt("id"),
                            rows.getString("name"),
                            rows.getString("email"),
                            rows.getTimestamp("created_at").toInstant(),
                            rows.getBoolean("is_admin"),
                            rows.getString("password"),
                        )
                    }
                }
            }
        }
        return null
    }

    fun isAdmin(userId: Int): Boolean {
        dataSource.connection.use { conn ->
            val sql = "SELECT is_admin FROM users WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, userId)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return rows.getBoolean("is_admin")
                    }
                }
            }
        }
        return false
    }

    fun updateName(
        userId: Int,
        newName: String,
    ): Boolean {
        dataSource.connection.use { conn ->
            val sql = "UPDATE users SET name = ? WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, newName)
                stmt.setInt(2, userId)
                return stmt.executeUpdate() > 0
            }
        }
    }

    fun updateEmail(
        userId: Int,
        newEmail: String,
    ): Boolean {
        dataSource.connection.use { conn ->
            val sql = "UPDATE users SET email = ? WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, newEmail)
                stmt.setInt(2, userId)
                return stmt.executeUpdate() > 0
            }
        }
    }

    fun updatePassword(
        userId: Int,
        hashedPassword: String,
    ): Boolean {
        dataSource.connection.use { conn ->
            val sql = "UPDATE users SET password = ? WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, hashedPassword)
                stmt.setInt(2, userId)
                return stmt.executeUpdate() > 0
            }
        }
    }
}
