package com.example.report_illness.views.reported_case

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.report_illness.R
import com.example.report_illness.adapters.ReportedCaseAdapter
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.ReportedCaseHelper
import com.example.report_illness.models.ReportedCase
import androidx.activity.result.contract.ActivityResultContracts

class ListActivity : AppCompatActivity() {

    private lateinit var recyclerViewReportedCases: RecyclerView
    private lateinit var reportedCaseAdapter: ReportedCaseAdapter

    companion object {
        const val UPDATE_LIST_REQUEST_CODE = 2
        const val CREATE_LIST_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reported_case_list)

        ConnectionDB.dbConn()

        recyclerViewReportedCases = findViewById(R.id.recyclerViewReportedCases)
        recyclerViewReportedCases.layoutManager = LinearLayoutManager(this)

        setupReportedCaseList()

        val buttonGoToMenu = findViewById<Button>(R.id.buttonGoToMenu)
        buttonGoToMenu.setOnClickListener {
            finish()
        }

        val buttonCreateReportedCase = findViewById<Button>(R.id.buttonCreateReportedCase)
        buttonCreateReportedCase.setOnClickListener {
            val intent = Intent(this@ListActivity, CreateActivity::class.java)
            startActivityForResult(intent, CREATE_LIST_REQUEST_CODE)
        }
    }

    private fun setupReportedCaseList() {
        val allReportedCases = ReportedCaseHelper.getAllReportedCases(ConnectionDB)
        reportedCaseAdapter = ReportedCaseAdapter(allReportedCases)
        recyclerViewReportedCases.adapter = reportedCaseAdapter
    }

    fun updateReportedCaseList() {
        val updatedReportedCases = ReportedCaseHelper.getAllReportedCases(ConnectionDB)
        reportedCaseAdapter.updateList(updatedReportedCases)
        recyclerViewReportedCases.adapter = reportedCaseAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPDATE_LIST_REQUEST_CODE || requestCode == CREATE_LIST_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val actualizado = data?.getBooleanExtra("actualizado", false) ?: false
                val creado = data?.getBooleanExtra("creado", false) ?: false
                if (actualizado || creado) {
                    updateReportedCaseList()
                }
            }
        }
    }
}
