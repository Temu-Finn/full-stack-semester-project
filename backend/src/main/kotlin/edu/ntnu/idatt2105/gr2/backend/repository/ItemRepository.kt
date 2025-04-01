package edu.ntnu.idatt2105.gr2.backend.repository

import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class ItemRepository(private val dataSource: DataSource) {
}