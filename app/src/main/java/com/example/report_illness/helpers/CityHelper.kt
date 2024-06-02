package com.example.report_illness.helpers

import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.database.DatabaseHelper
import com.example.report_illness.models.City
import java.sql.ResultSet

object CityHelper {

    fun insertCity(city: City, connectionDB: ConnectionDB): Boolean {
        val query = "INSERT INTO City (id, name, latitude, longitude, country_id) VALUES (?, ?, ?, ?, ?)"
        val parameters = listOf<Any>(
            city.id,
            city.name,
            city.latitude,
            city.longitude,
            city.countryId
        )
        return DatabaseHelper.executeUpdate(query, parameters, connectionDB) > 0
    }

    fun updateCity(city: City, connectionDB: ConnectionDB): Boolean {
        val query = "UPDATE City SET name = ?, latitude = ?, longitude = ?, country_id = ? WHERE id = ?"
        val parameters = listOf<Any>(
            city.name,
            city.latitude,
            city.longitude,
            city.countryId,
            city.id
        )
        return DatabaseHelper.executeUpdate(query, parameters, connectionDB) > 0
    }

    fun deleteCity(id: Int, connectionDB: ConnectionDB): Boolean {
        val query = "DELETE FROM City WHERE id = ?"
        return DatabaseHelper.executeUpdate(query, listOf(id), connectionDB) > 0
    }

    fun getAllCities(connectionDB: ConnectionDB): List<City> {
        val query = "SELECT * FROM City"
        val resultSet = DatabaseHelper.executeQuery(query, connectionDB)
        val cities = mutableListOf<City>()
        resultSet?.use { rs ->
            while (rs.next()) {
                val city = mapResultSetToCity(rs)
                cities.add(city)
            }
        }
        return cities
    }

    fun getCityById(id: Int, connectionDB: ConnectionDB): City? {
        val query = "SELECT * FROM City WHERE id = ?"
        val resultSet = DatabaseHelper.executeQuery(query, connectionDB, id)
        resultSet?.use { rs ->
            if (rs.next()) {
                return mapResultSetToCity(rs)
            }
        }
        return null
    }

    private fun mapResultSetToCity(resultSet: ResultSet): City {
        return City(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            resultSet.getDouble("latitude"),
            resultSet.getDouble("longitude"),
            resultSet.getInt("country_id")
        )
    }
}
