package edu.ntnu.idatt2105.gr2.backend.service

import org.springframework.stereotype.Service
import org.springframework.web.context.annotation.RequestScope

@Service
@RequestScope
class UserContextService {
    private var username: String? = null

    fun getCurrentUsername(): String {
        return username ?: throw IllegalStateException("No user id set in current context")
    }

    fun setCurrentUsername(username: String) {
        this.username = username
    }
}