package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.dto.SearchRequest
import edu.ntnu.idatt2105.gr2.backend.model.ItemStatus
import java.sql.PreparedStatement

fun SearchRequest.whereClause() = StringBuilder().apply {
    append("i.status = ?")
    if (!searchText.isNullOrBlank()) {
        append(" AND (LOWER(i.title) LIKE LOWER(?) OR LOWER(i.description) LIKE LOWER(?))")
    }
    if (categoryId != null) {
        append(" AND i.category_id = ?")
    }
    if (!county.isNullOrBlank()) {
        append(" AND pc.county = ?")
        if (!municipality.isNullOrBlank()) {
            append(" AND pc.municipality = ?")
            if (!city.isNullOrBlank()) {
                append(" AND pc.city = ?")
            }
        }
    }
    if (latitude != null && longitude != null && maxDistanceKm != null && maxDistanceKm > 0) {
        append(" AND i.location IS NOT NULL AND ST_Distance_Sphere(location, ST_PointFromText(?, 4326)) <= ?")
    }
}

fun SearchRequest.prepareWhereClause(stmt: PreparedStatement): Int {
    var index = 1
    params().forEach { param ->
        when (param) {
            is String -> stmt.setString(index++, param)
            is Int -> stmt.setInt(index++, param)
            is Double -> stmt.setDouble(index++, param)
        }
    }

    return index
}

private fun SearchRequest.params(): List<Any> {
    val params = mutableListOf<Any>(ItemStatus.Available.toString())
    if (!searchText.isNullOrBlank()) {
        params.add("%$searchText%")
        params.add("%$searchText%")
    }
    if (categoryId != null) {
        params.add(categoryId)
    }
    if (!county.isNullOrBlank()) {
        params.add(county)
        if (!municipality.isNullOrBlank()) {
            params.add(municipality)
            if (!city.isNullOrBlank()) {
                params.add(city)
            }
        }
    }
    if (latitude != null && longitude != null && maxDistanceKm != null && maxDistanceKm > 0) {
        params.add("POINT($longitude $latitude)")
        params.add(maxDistanceKm * 1000) // Convert km to meters
    }
    return params
}
