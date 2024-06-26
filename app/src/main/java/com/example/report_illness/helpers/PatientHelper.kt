package com.example.report_illness.helpers

import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.database.DatabaseHelper
import com.example.report_illness.models.Patient
import java.sql.ResultSet

object PatientHelper {

    fun insertPatient(patient: Patient, connectionDB: ConnectionDB): Boolean {
        val query = "INSERT INTO Patient (id, names, lastnames, contact, birthday, gender) VALUES (?, ?, ?, ?, ?, ?)"
        val parameters = listOf<Any>(
            patient.id,
            patient.names,
            patient.lastNames,
            patient.contact,
            patient.birthDate,
            patient.gender
        )
        return DatabaseHelper.executeUpdate(query, parameters, connectionDB) > 0
    }

    fun updatePatient(patient: Patient, connectionDB: ConnectionDB): Boolean {
        val query = "UPDATE Patient SET names = ?, lastNames = ?, contact = ?, birthday = ?, gender = ? WHERE id = ?"
        val parameters = listOf<Any>(
            patient.names,
            patient.lastNames,
            patient.contact,
            patient.birthDate,
            patient.gender,
            patient.id
        )
        return DatabaseHelper.executeUpdate(query, parameters, connectionDB) > 0
    }

    fun deletePatient(id: Int, connectionDB: ConnectionDB): Boolean {
        val query = "DELETE FROM Patient WHERE id = ?"
        return DatabaseHelper.executeUpdate(query, listOf(id), connectionDB) > 0
    }

    fun getAllPatients(connectionDB: ConnectionDB): List<Patient> {
        val query = "SELECT * FROM Patient"
        val resultSet = DatabaseHelper.executeQuery(query, connectionDB)
        val patients = mutableListOf<Patient>()
        resultSet?.use { rs ->
            while (rs.next()) {
                val patient = mapResultSetToPatient(rs)
                patients.add(patient)
            }
        }
        return patients
    }

    fun getPatientById(id: Int, connectionDB: ConnectionDB): Patient? {
        val query = "SELECT * FROM Patient WHERE id = ?"
        val resultSet = DatabaseHelper.executeQuery(query, connectionDB, id)
        resultSet?.use { rs ->
            if (rs.next()) {
                return mapResultSetToPatient(rs)
            }
        }
        return null
    }

    private fun mapResultSetToPatient(resultSet: ResultSet): Patient {
        return Patient(
            resultSet.getInt("id"),
            resultSet.getString("names"),
            resultSet.getString("lastnames"),
            resultSet.getString("contact"),
            resultSet.getString("birthday"),
            resultSet.getString("gender")
        )
    }
}