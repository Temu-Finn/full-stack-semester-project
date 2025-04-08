package edu.ntnu.idatt2105.gr2.backend.configs

import edu.ntnu.idatt2105.gr2.backend.model.Category
import edu.ntnu.idatt2105.gr2.backend.model.User
import edu.ntnu.idatt2105.gr2.backend.repository.CategoryRepository
import edu.ntnu.idatt2105.gr2.backend.repository.UserRepository
import edu.ntnu.idatt2105.gr2.backend.service.CategoryService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
class TestDataLoader(private val passwordEncoder: PasswordEncoder) {

    @Bean
    fun loadCategoryTestData(categoryRepository: CategoryRepository): CommandLineRunner {
        return CommandLineRunner {
            val category1 = Category(name ="BOOKS",description = "Books and literature")
            val category2 = Category(name = "ELECTRONICS",description = "Electronics and gadgets")
            val category3 = Category(name = "FURNITURE",description = "Furniture and home decor")
            val category4 = Category(name = "CLOTHING",description = "Clothing and fashion")

            categoryRepository.save(category1)
            categoryRepository.save(category2)
            categoryRepository.save(category3)
            categoryRepository.save(category4)
        }
    }

    /*
    @Bean
    fun createAdminUser(userRepository: UserRepository): CommandLineRunner {
        return CommandLineRunner {
            val adminUser = User(
                name = "Admin",
                email = "admin@mail.com",
                passwordHashed = passwordEncoder.encode("admin123")
            )

            userRepository.save(adminUser)

            val adminId: Int = userRepository.findByEmail("admin@mail.com")?.userId?:
                throw IllegalStateException("Admin user not found after creation")
            userRepository.setAdmin(adminId)
        }
    }
    @Bean
     */
}