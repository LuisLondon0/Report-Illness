package com.example.report_illness.helpers

import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.database.DatabaseHelper
import com.example.report_illness.models.ReportedCase
import java.sql.ResultSet

object ReportedCaseHelper {

    fun insertReportedCase(reportedCase: ReportedCase, connectionDB: ConnectionDB): Boolean {
        val query = "INSERT INTO reported_case (id, reported_date, patient_id, disease_id, city_id) VALUES (?, ?, ?, ?, ?)"
        val parameters = listOf<Any>(
            reportedCase.id,
            reportedCase.reportedDate,
            reportedCase.patientId,
            reportedCase.diseaseId,
            reportedCase.cityId
        )
        return DatabaseHelper.executeUpdate(query, parameters, connectionDB) > 0
    }

    fun updateReportedCase(reportedCase: ReportedCase, connectionDB: ConnectionDB): Boolean {
        val query = "UPDATE reported_case SET reported_date = ?, patient_id = ?, disease_id = ?, city_id = ? WHERE id = ?"
        val parameters = listOf<Any>(
            reportedCase.reportedDate,
            reportedCase.patientId,
            reportedCase.diseaseId,
            reportedCase.cityId,
            reportedCase.id
        )
        return DatabaseHelper.executeUpdate(query, parameters, connectionDB) > 0
    }

    fun deleteReportedCase(id: Int, connectionDB: ConnectionDB): Boolean {
        val query = "DELETE FROM reported_case WHERE id = ?"
        return DatabaseHelper.executeUpdate(query, listOf(id), connectionDB) > 0
    }

    fun getAllReportedCases(connectionDB: ConnectionDB): List<ReportedCase> {
        val query = "SELECT * FROM reported_case"
        val resultSet = DatabaseHelper.executeQuery(query, connectionDB)
        val reportedCases = mutableListOf<ReportedCase>()
        resultSet?.use { rs ->
            while (rs.next()) {
                val reportedCase = mapResultSetToReportedCase(rs)
                reportedCases.add(reportedCase)
            }
        }
        return reportedCases
    }

    fun getReportedCaseById(id: Int, connectionDB: ConnectionDB): ReportedCase? {
        val query = "SELECT * FROM reported_case WHERE id = ?"
        val resultSet = DatabaseHelper.executeQuery(query, connectionDB, id)
        resultSet?.use { rs ->
            if (rs.next()) {
                return mapResultSetToReportedCase(rs)
            }
        }
        return null
    }

    fun getReportedCasesByYearAndCountry(year: Int, countryName: String, connectionDB: ConnectionDB): List<Map<String, Any>> {
        val query = """
        SELECT 
            YEAR(reported_date) AS Year,
            MONTH(reported_date) AS Month,
            COUNT(*) AS TotalCases
        FROM 
            dbo.reported_case rc
        JOIN 
            dbo.city c ON rc.city_id = c.id
        JOIN 
            dbo.country co ON c.country_id = co.id
        WHERE 
            co.name = ?
            AND YEAR(reported_date) = ?
        GROUP BY 
            YEAR(reported_date),
            MONTH(reported_date)
        ORDER BY 
            YEAR(reported_date),
            MONTH(reported_date)
    """.trimIndent()

        val resultSet = DatabaseHelper.executeQuery(query, connectionDB, countryName, year)
        val reportedCases = mutableListOf<Map<String, Any>>()

        resultSet?.use { rs ->
            while (rs.next()) {
                val month = rs.getInt("Month")
                val totalCases = rs.getInt("TotalCases")
                val map = mapOf(
                    "Year" to year,
                    "Month" to month,
                    "TotalCases" to totalCases
                )
                reportedCases.add(map)
            }
        }

        return reportedCases
    }

    private fun mapResultSetToReportedCase(resultSet: ResultSet): ReportedCase {
        return ReportedCase(
            resultSet.getInt("id"),
            resultSet.getString("reported_date"),
            resultSet.getInt("patient_id"),
            resultSet.getInt("disease_id"),
            resultSet.getInt("city_id")
        )
    }
}
