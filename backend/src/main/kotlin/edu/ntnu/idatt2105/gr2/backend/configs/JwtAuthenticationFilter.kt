package edu.ntnu.idatt2105.gr2.backend.configs

import edu.ntnu.idatt2105.gr2.backend.service.JwtService
import edu.ntnu.idatt2105.gr2.backend.service.UserContextService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
    private val handlerExceptionResolver: HandlerExceptionResolver,
    private val userContextService: UserContextService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val jwt = authHeader.substring(7)
            val email: String? = jwtService.extractEmailFromToken(jwt)

            val authentication: Authentication? = SecurityContextHolder.getContext().authentication

            if (email != null && authentication == null) {
                val userDetails = userDetailsService.loadUserByUsername(email)

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    val userId =
                        jwtService.extractUserIdFromToken(jwt)
                            ?: throw IllegalStateException("UserID not found in valid token")

                    userContextService.setCurrentUserId(userId)
                    val authToken =
                        UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.authorities,
                        )

                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }

            filterChain.doFilter(request, response)
        } catch (exception: Exception) {
            logger.error("Error occurred while processing JWT token: ${exception.message}")
            SecurityContextHolder.clearContext()
            handlerExceptionResolver.resolveException(request, response, null, exception)
        }
    }
}
