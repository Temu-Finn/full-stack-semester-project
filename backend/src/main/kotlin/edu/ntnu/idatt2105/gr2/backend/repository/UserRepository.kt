package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.User
import org.springframework.stereotype.Repository
import java.sql.Statement
import javax.sql.DataSource

@Repository
class UserRepository(private val dataSource: DataSource) {

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
                        return User(keys.getInt(1), name, email, password)
                    } else {
                        throw RuntimeException("Creating user failed, no ID obtained.")
                    }
                }
            }
        }
    }

    fun findByEmail(email: String): User? {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM users WHERE email = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return User(
                            rows.getInt("id"),
                            rows.getString("name"),
                            rows.getString("email"),
                            rows.getString("password"),
                        )
                    }
                }
            }
        }

        return null
    }

    fun findById(id: Int): User? {
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
}