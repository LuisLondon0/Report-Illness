package com.example.report_illness.helpers

import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.database.DatabaseHelper
import com.example.report_illness.models.Disease
import java.sql.ResultSet

object DiseaseHelper {
    fun insertDisease(disease: Disease, connectionDB: ConnectionDB): Boolean {
        val query = "INSERT INTO Disease (id, name, description, urgency, vaccine, treatment) VALUES (?, ?, ?, ?, ?, ?)"
        val parameters = listOf<Any>(
            disease.id,
            disease.name,
            disease.description,
            disease.urgency,
            disease.vaccine,
            disease.treatment
        )
        return DatabaseHelper.executeUpdate(query, parameters, connectionDB) > 0
    }

    fun updateDisease(disease: Disease, connectionDB: ConnectionDB): Boolean {
        val query = "UPDATE Disease SET name = ?, description = ?, urgency = ?, vaccine = ?, treatment = ? WHERE id = ?"
        val parameters = listOf<Any>(
            disease.name,
            disease.description,
            disease.urgency,
            disease.vaccine,
            disease.treatment,
            disease.id
        )
        return DatabaseHelper.executeUpdate(query, parameters, connectionDB) > 0
    }

    fun deleteDisease(id: Int, connectionDB: ConnectionDB): Boolean {
        val query = "DELETE FROM Disease WHERE id = ?"
        return DatabaseHelper.executeUpdate(query, listOf(id), connectionDB) > 0
    }

    fun getAllDiseases(connectionDB: ConnectionDB): List<Disease> {
        val query = "SELECT * FROM Disease"
        val resultSet = DatabaseHelper.executeQuery(query, connectionDB)
        val diseases = mutableListOf<Disease>()
        resultSet?.use { rs ->
            while (rs.next()) {
                val disease = mapResultSetToDisease(rs)
                diseases.add(disease)
            }
        }
        return diseases
    }

    fun getDiseaseById(id: Int, connectionDB: ConnectionDB): Disease? {
        val query = "SELECT * FROM Disease WHERE id = ?"
        val resultSet = DatabaseHelper.executeQuery(query, connectionDB, id)
        resultSet?.use { rs ->
            if (rs.next()) {
                return mapResultSetToDisease(rs)
            }
        }
        return null
    }

    private fun mapResultSetToDisease(resultSet: ResultSet): Disease {
        return Disease(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            resultSet.getString("description"),
            resultSet.getString("urgency"),
            resultSet.getBoolean("vaccine"),
            resultSet.getBoolean("treatment")
        )
    }
}