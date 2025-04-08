package edu.ntnu.idatt2105.gr2.backend.config

import edu.ntnu.idatt2105.gr2.backend.service.JwtService
import edu.ntnu.idatt2105.gr2.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor
import org.springframework.web.util.UriComponentsBuilder
import java.lang.Exception
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) : WebSocketMessageBrokerConfigurer {

    private val logger = LoggerFactory.getLogger(WebSocketConfig::class.java)

    @Override
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        logger.info("Configuring message broker")
        config.enableSimpleBroker("/topic")
        config.setApplicationDestinationPrefixes("/app")
    }

    @Override
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        logger.info("Registering STOMP endpoints")
        registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:63342", "http://localhost:5173")
            .addInterceptors(JwtHandShakeInterceptor(jwtService, userRepository))
            .withSockJS()
    }
}

@Component
class JwtHandShakeInterceptor (
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) : HandshakeInterceptor {

    private val logger = LoggerFactory.getLogger(JwtHandShakeInterceptor::class.java)

    @Override
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        logger.info("WebSocket handshake initiated")
        println("WebSocket handshake initiated")

        val uri = request.uri
        val query = uri.query // e.g. "token=abc.xyz.123"
        val token = query?.split("&")
            ?.map { it.split("=") }
            ?.find { it[0] == "token" }
            ?.getOrNull(1)
            ?.removePrefix("Bearer ")?.trim()


        val email: String? = token?.let { jwtService.extractEmailFromToken(token) }
        val user = email?.let { userRepository.findByEmail(it) }

        logger.info("Extracted email: $email")
        logger.info("User found: $user")
        logger.info("Token: $token")

        return if (user != null && jwtService.isTokenValid(token, user)) {
            val userId = jwtService.extractUserIdFromToken(token) ?: return false
            attributes["userId"] = userId
            true
        } else {
            logger.info("Invalid token or user not found")
            false
        }
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        logger.info("WebSocket handshake completed")
    }
}