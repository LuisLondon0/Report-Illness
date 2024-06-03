package com.example.report_illness.views.reported_case

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.report_illness.R
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.CityHelper
import com.example.report_illness.helpers.DiseaseHelper
import com.example.report_illness.helpers.PatientHelper
import com.example.report_illness.helpers.ReportedCaseHelper
import com.example.report_illness.models.ReportedCase
import java.util.Calendar

class CreateActivity : AppCompatActivity() {

    private lateinit var patientNames: List<String>
    private lateinit var patientIds: List<Int>

    private lateinit var cityNames: List<String>
    private lateinit var cityIds: List<Int>

    private lateinit var diseaseNames: List<String>
    private lateinit var diseaseIds: List<Int>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reported_case_create)

        ConnectionDB.dbConn()

        setupPatientSpinner()
        setupCitySpinner()
        setupDiseaseSpinner()
        setupSubmitButton()

        val buttonCancel = findViewById<Button>(R.id.buttonCancel)
        buttonCancel.setOnClickListener {
            finish()
        }

        val isNewReportedCase = intent.getBooleanExtra("isNewReportedCase", true)

        if (!isNewReportedCase) {
            val editTextId = findViewById<EditText>(R.id.editTextId)
            editTextId.isEnabled = false

            val reportedCaseId = intent.getIntExtra("reportedCaseId", 0)
            val reportedCase = ReportedCaseHelper.getReportedCaseById(reportedCaseId, ConnectionDB)

            // Llenar los campos con la información del caso reportado
            reportedCase?.let {
                editTextId.setText(it.id.toString())
                findViewById<EditText>(R.id.editTextReportedDate).setText(it.reportedDate.substring(0, 10))
                val spinnerPatient = findViewById<Spinner>(R.id.spinnerPatientId)
                val patientIndex = patientIds.indexOf(it.patientId)
                spinnerPatient.setSelection(patientIndex)
                val spinnerCity = findViewById<Spinner>(R.id.spinnerCityId)
                val cityIndex = cityIds.indexOf(it.cityId)
                spinnerCity.setSelection(cityIndex)
                val spinnerDisease = findViewById<Spinner>(R.id.spinnerDiseaseId)
                val diseaseIndex = diseaseIds.indexOf(it.diseaseId)
                spinnerDisease.setSelection(diseaseIndex)
            }
        }

        setupReportedDateField()
    }

    private fun setupPatientSpinner() {
        val patients = PatientHelper.getAllPatients(ConnectionDB)
        patientNames = patients.map { "${it.id} - ${it.names} ${it.lastNames}" }
        patientIds = patients.map { it.id }

        val spinnerPatient = findViewById<Spinner>(R.id.spinnerPatientId)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, patientNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPatient.adapter = adapter
    }

    private fun setupCitySpinner() {
        val cities = CityHelper.getAllCities(ConnectionDB)
        cityNames = cities.map { it.name }
        cityIds = cities.map { it.id }

        val spinnerCity = findViewById<Spinner>(R.id.spinnerCityId)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cityNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCity.adapter = adapter
    }

    private fun setupDiseaseSpinner() {
        val diseases = DiseaseHelper.getAllDiseases(ConnectionDB)
        diseaseNames = diseases.map { it.name }
        diseaseIds = diseases.map { it.id }

        val spinnerDisease = findViewById<Spinner>(R.id.spinnerDiseaseId)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, diseaseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDisease.adapter = adapter
    }

    private fun setupReportedDateField() {
        val editTextReportedDate: EditText = findViewById(R.id.editTextReportedDate)
        editTextReportedDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    editTextReportedDate.setText(formattedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun setupSubmitButton() {
        val editTextId: EditText = findViewById(R.id.editTextId)
        val editTextReportedDate: EditText = findViewById(R.id.editTextReportedDate)
        val spinnerPatient: Spinner = findViewById(R.id.spinnerPatientId)
        val spinnerCity: Spinner = findViewById(R.id.spinnerCityId)
        val spinnerDisease: Spinner = findViewById(R.id.spinnerDiseaseId)
        val buttonSubmit: Button = findViewById(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener {
            val id = editTextId.text.toString().toIntOrNull()
            val reportedDate = editTextReportedDate.text.toString()
            val patientIndex = spinnerPatient.selectedItemPosition
            val patientId = patientIds[patientIndex]
            val cityIndex = spinnerCity.selectedItemPosition
            val cityId = cityIds[cityIndex]
            val diseaseIndex = spinnerDisease.selectedItemPosition
            val diseaseId = diseaseIds[diseaseIndex]

            // Validar campos
            if (id == null) {
                Toast.makeText(this, "Por favor, ingrese un ID válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (reportedDate.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto ReportedCase con los datos ingresados
            val reportedCase = ReportedCase(id, reportedDate, patientId, diseaseId, cityId)

            // Insertar o actualizar el caso reportado según si ya existe o no
            if (ReportedCaseHelper.getReportedCaseById(id, ConnectionDB) != null) {
                if (ReportedCaseHelper.updateReportedCase(reportedCase, ConnectionDB)) {
                    Toast.makeText(this, "Caso reportado actualizado correctamente", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("actualizado", true)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al actualizar el caso reportado", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (ReportedCaseHelper.insertReportedCase(reportedCase, ConnectionDB)) {
                    Toast.makeText(this, "Caso reportado insertado correctamente", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("creado", true)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al insertar el caso reportado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
