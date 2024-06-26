package com.example.report_illness.database

import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import com.example.report_illness.BuildConfig

object ConnectionDB {
    private var connection: Connection? = null

    init {
        // Initialize the connection upon the first access
        getConnection()
    }

    private fun getConnection() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var conn: Connection? = null
        val host = BuildConfig.DB_HOST
        val dbname = BuildConfig.DB_NAME
        val user = BuildConfig.DB_USER
        val password = BuildConfig.DB_PASSWORD

        val connString: String

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connString = "jdbc:jtds:sqlserver://$host:1433;databaseName=$dbname;user=$user@report-illness;password=$password;encrypt=true;trustServerCertificate=true;ssl=require;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
            conn = DriverManager.getConnection(connString)
            Log.d("Success: ", "Connected to database")
        } catch (ex: SQLException) {
            Log.e("Error: ", ex.message!!)
        } catch (ex1: ClassNotFoundException) {
            Log.e("Error: ", ex1.message!!)
        } catch (ex2: Exception) {
            Log.e("Error: ", ex2.message!!)
        }

        connection = conn
    }

    fun dbConn(): Connection? {
        return connection
    }
}