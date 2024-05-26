package com.example.report_illness.database

import android.util.Log
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

object DatabaseHelper {

    fun executeQuery(query: String, connectionDB: ConnectionDB): ResultSet? {
        val conn = connectionDB.dbConn() ?: run {
            Log.e("Error: ", "Check your internet connection")
            return null
        }
        try {
            val statement: Statement = conn.createStatement()
            return statement.executeQuery(query)
        } catch (e: SQLException) {
            Log.e("Error: ", e.message ?: "Unknown error")
            return null
        }
    }

    fun executeUpdate(query: String, parameters: List<Any>, connectionDB: ConnectionDB): Int {
        val conn = connectionDB.dbConn() ?: return 0
        return try {
            val preparedStatement: PreparedStatement = conn.prepareStatement(query)
            for ((index, parameter) in parameters.withIndex()) {
                preparedStatement.setObject(index + 1, parameter)
            }
            preparedStatement.executeUpdate()
        } catch (e: SQLException) {
            Log.e("Error: ", e.message!!)
            0
        }
    }
}