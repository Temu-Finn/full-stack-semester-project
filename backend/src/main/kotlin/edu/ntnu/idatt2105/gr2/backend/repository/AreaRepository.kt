package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.dto.SearchRequest
import edu.ntnu.idatt2105.gr2.backend.model.CountyMunicipalityCount
import org.springframework.stereotype.Repository
import java.sql.ResultSet
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
                items i ON pc.postal_code = i.postal_code
            WHERE i.id IS NULL || (${request.whereClause()})
            GROUP BY 
                pc.county, pc.municipality
            ORDER BY 
                pc.county, pc.municipality
        """.trimIndent()

        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                request.prepareWhereClause(stmt)
                stmt.executeQuery().use { rs ->
                    buildList {
                        while (rs.next()) {
                            add(mapRowToCountyMunicipalityCount(rs))
                        }
                    }
                }
            }
        }
    }

    fun mapRowToCountyMunicipalityCount(rs: ResultSet): CountyMunicipalityCount {
        return CountyMunicipalityCount(
            county = rs.getString("county"),
            municipality = rs.getString("municipality"),
            count = rs.getInt("item_count")
        )
    }
}