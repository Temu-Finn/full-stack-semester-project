package edu.ntnu.idatt2105.gr2.backend.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant

data class User(
    val id: Int = -1,
    val name: String,
    val email: String,
    val joinedAt: Instant = Instant.now(),
    val isAdmin: Boolean = false,
    private var passwordHashed: String,
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

    override fun getPassword(): String = passwordHashed

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
