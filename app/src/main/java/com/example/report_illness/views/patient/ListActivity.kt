package com.example.report_illness.views.patient

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.report_illness.MainActivity
import com.example.report_illness.R
import com.example.report_illness.adapters.PatientAdapter
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.PatientHelper
import androidx.activity.result.contract.ActivityResultContracts

class ListActivity : AppCompatActivity() {

    private lateinit var recyclerViewPatients: RecyclerView
    private lateinit var patientAdapter: PatientAdapter

    companion object {
        const val UPDATE_LIST_REQUEST_CODE = 2
        const val CREATE_LIST_REQUEST_CODE = 1
    }

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)

        ConnectionDB.dbConn()

        recyclerViewPatients = findViewById(R.id.recyclerViewPatients)
        recyclerViewPatients.layoutManager = LinearLayoutManager(this)

        setupPatientList()

        val buttonGoToMenu = findViewById<Button>(R.id.buttonGoToMenu)
        buttonGoToMenu.setOnClickListener {
            finish()
        }

        val buttonCreatePatient = findViewById<Button>(R.id.buttonCreatePatient)
        buttonCreatePatient.setOnClickListener {
            val intent = Intent(this@ListActivity, CreateActivity::class.java)
            startActivityForResult(intent, CREATE_LIST_REQUEST_CODE)
        }

        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val actualizado = data?.getBooleanExtra("actualizado", false) ?: false
                val creado = data?.getBooleanExtra("creado", false) ?: false
                if (actualizado || creado) {
                    updatePatientList()
                }
            }
        }
    }

    private fun setupPatientList() {
        val allPatients = PatientHelper.getAllPatients(ConnectionDB)
        patientAdapter = PatientAdapter(allPatients)
        recyclerViewPatients.adapter = patientAdapter
    }

    fun updatePatientList() {
        val updatedPatients = PatientHelper.getAllPatients(ConnectionDB)
        patientAdapter.updateList(updatedPatients)
        recyclerViewPatients.adapter = patientAdapter // Vuelve a asignar el adaptador al RecyclerView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPDATE_LIST_REQUEST_CODE || requestCode == CREATE_LIST_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val actualizado = data?.getBooleanExtra("actualizado", false) ?: false
                val creado = data?.getBooleanExtra("creado", false) ?: false
                if (actualizado || creado) {
                    updatePatientList()
                }
            }
        }
    }
}