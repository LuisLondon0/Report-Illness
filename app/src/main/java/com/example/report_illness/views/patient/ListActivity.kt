package com.example.report_illness.views.patient

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.report_illness.R
import com.example.report_illness.adapters.PatientAdapter
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.PatientHelper

class ListActivity : AppCompatActivity() {

    private lateinit var recyclerViewPatients: RecyclerView
    private lateinit var patientAdapter: PatientAdapter

    companion object {
        const val UPDATE_LIST_REQUEST_CODE = 2
        const val CREATE_LIST_REQUEST_CODE = 1
    }

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            handleActivityResult(result.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)

        ConnectionDB.dbConn()
        initializeViews()
        setupPatientList()
        setupButtons()
    }

    private fun initializeViews() {
        recyclerViewPatients = findViewById(R.id.recyclerViewPatients)
        recyclerViewPatients.layoutManager = LinearLayoutManager(this)
    }

    private fun setupPatientList() {
        val allPatients = PatientHelper.getAllPatients(ConnectionDB)
        patientAdapter = PatientAdapter(allPatients)
        recyclerViewPatients.adapter = patientAdapter
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.buttonGoToMenu).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.buttonCreatePatient).setOnClickListener {
            val intent = Intent(this@ListActivity, CreateActivity::class.java)
            startActivityForResult(intent, CREATE_LIST_REQUEST_CODE)
        }
    }

    private fun handleActivityResult(data: Intent?) {
        val isUpdated = data?.getBooleanExtra("actualizado", false) ?: false
        val isCreated = data?.getBooleanExtra("creado", false) ?: false
        if (isUpdated || isCreated) {
            updatePatientList()
        }
    }

    fun updatePatientList() {
        val updatedPatients = PatientHelper.getAllPatients(ConnectionDB)
        patientAdapter.updateList(updatedPatients)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == UPDATE_LIST_REQUEST_CODE || requestCode == CREATE_LIST_REQUEST_CODE) &&
            resultCode == Activity.RESULT_OK) {
            handleActivityResult(data)
        }
    }
}