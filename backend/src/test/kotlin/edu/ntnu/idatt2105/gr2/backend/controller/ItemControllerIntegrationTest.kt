package edu.ntnu.idatt2105.gr2.backend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import edu.ntnu.idatt2105.gr2.backend.config.TestConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig::class)
@ActiveProfiles("test")
class ItemControllerIntegrationTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setup() {
        jdbcTemplate.execute("DELETE FROM items")
        jdbcTemplate.update(
            """
            INSERT INTO items 
            (seller_id, category_id, postal_code, title, description, price, status) 
            VALUES (1, 2, '7014', 'Item 1', 'desc', 100.0, 'available')
            """.trimIndent(),
        )
        jdbcTemplate.update(
            """
            INSERT INTO items 
            (seller_id, category_id, postal_code, title, description, price, status) 
            VALUES (1, 2, '7014', 'Item 2', 'desc', 150.0, 'available')
            """.trimIndent(),
        )
    }

    @Test
    @DisplayName("Should get items by category id")
    fun `should get items by category id`() {
        mockMvc
            .perform(get("/api/items/category/2"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Item 1"))
            .andExpect(jsonPath("$[1].title").value("Item 2"))
    }
}
