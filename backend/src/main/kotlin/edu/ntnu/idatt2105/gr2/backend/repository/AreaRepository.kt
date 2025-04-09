package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.dto.SearchRequest
import edu.ntnu.idatt2105.gr2.backend.model.CountyMunicipalityCount
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class AreaRepository (private val dataSource: DataSource) {

    fun getCounties(request: SearchRequest): List<CountyMunicipalityCount> {
        val sql = """
            SELECT 
                pc.county, 
                pc.municipality, 
                COUNT(i.id) AS item_count
            FROM 
                postal_codes pc
            LEFT JOIN 
                items i ON pc.postal_code = i.postal_code AND i.status = 'Available'
            GROUP BY 
                pc.county, pc.municipality
            ORDER BY 
                pc.county, pc.municipality
        """.trimIndent()

        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.executeQuery().use { rs ->
                    val counties = mutableListOf<CountyMunicipalityCount>()
                    while (rs.next()) {
                        val county = rs.getString("county")
                        val municipality = rs.getString("municipality")
                        val count = rs.getInt("item_count")
                        counties.add(CountyMunicipalityCount(county, municipality, count))
                    }
                    counties
                }
            }
        }
    }
}