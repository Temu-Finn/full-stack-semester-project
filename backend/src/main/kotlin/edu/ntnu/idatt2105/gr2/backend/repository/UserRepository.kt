package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.User
import org.springframework.stereotype.Repository
import java.sql.Statement
import javax.sql.DataSource

@Repository
class UserRepository(private val dataSource: DataSource) {

    fun save(user: User): User {
        val username = user.username
        val email = user.email
        val password = user.password
        dataSource.connection.use { conn ->
            val sql = "INSERT INTO users (email, name, password) VALUES (?, ?, ?)"
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setString(1, username)
                stmt.setString(2, email)
                // To be replaced with JWT token
                stmt.setString(3, password)

                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Creating user failed, no rows affected.")
                }

                stmt.generatedKeys.use { keys ->
                    if (keys.next()) {
                        return User(username, email, password)
                    } else {
                        throw RuntimeException("Creating user failed, no ID obtained.")
                    }
                }
            }
        }
    }

    fun existsByUsername(username: String): Boolean {
        dataSource.connection.use { conn ->
            val sql = "SELECT COUNT(*) FROM users WHERE username = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, username)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return rows.getInt(1) > 0
                    }
                }
            }
        }

        return false
    }

    fun findByUsername(username: String): User? {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM users WHERE username = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, username)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return User(
                            rows.getString("username"),
                            rows.getString("email"),
                            rows.getString("password")
                        )
                    }
                }
            }
        }

        return null
    }
}