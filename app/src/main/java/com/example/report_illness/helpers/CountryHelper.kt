package com.example.report_illness.helpers

import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.database.DatabaseHelper
import com.example.report_illness.models.Country
import java.sql.ResultSet

object CountryHelper {

    fun insertCountry(country: Country, connectionDB: ConnectionDB): Boolean {
        val query = "INSERT INTO Country (id, name) VALUES (?, ?)"
        val parameters = listOf<Any>(
            country.id,
            country.name
        )
        return DatabaseHelper.executeUpdate(query, parameters, connectionDB) > 0
    }

    fun updateCountry(country: Country, connectionDB: ConnectionDB): Boolean {
        val query = "UPDATE Country SET name = ? WHERE id = ?"
        val parameters = listOf<Any>(
            country.name,
            country.id
        )
        return DatabaseHelper.executeUpdate(query, parameters, connectionDB) > 0
    }

    fun deleteCountry(id: Int, connectionDB: ConnectionDB): Boolean {
        val query = "DELETE FROM Country WHERE id = ?"
        return DatabaseHelper.executeUpdate(query, listOf(id), connectionDB) > 0
    }

    fun getAllCountries(connectionDB: ConnectionDB): List<Country> {
        val query = "SELECT * FROM Country"
        val resultSet = DatabaseHelper.executeQuery(query, connectionDB)
        val countries = mutableListOf<Country>()
        resultSet?.use { rs ->
            while (rs.next()) {
                val country = mapResultSetToCountry(rs)
                countries.add(country)
            }
        }
        return countries
    }

    fun getCountryById(id: Int, connectionDB: ConnectionDB): Country? {
        val query = "SELECT * FROM Country WHERE id = ?"
        val resultSet = DatabaseHelper.executeQuery(query, connectionDB, id)
        resultSet?.use { rs ->
            if (rs.next()) {
                return mapResultSetToCountry(rs)
            }
        }
        return null
    }

    private fun mapResultSetToCountry(resultSet: ResultSet): Country {
        return Country(
            resultSet.getInt("id"),
            resultSet.getString("name")
        )
    }
}
