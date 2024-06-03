package com.example.report_illness.views.patient

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.report_illness.R
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.PatientHelper
import com.example.report_illness.models.Patient
import java.util.Calendar

class CreateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_create)

        ConnectionDB.dbConn()

        setupSubmitButton()

        val buttonCancel = findViewById<Button>(R.id.buttonCancel)
        buttonCancel.setOnClickListener {
            finish()
        }

        val isNewPatient = intent.getBooleanExtra("isNewPatient", true)

        if (!isNewPatient) {
            val editTextId = findViewById<EditText>(R.id.editTextId)
            editTextId.isEnabled = false

            val patientId = intent.getIntExtra("patientId", 0)
            Log.d("CreateActivity", "Editing patient with ID: $patientId")
            val patient = PatientHelper.getPatientById(patientId, ConnectionDB)

            // Llenar los campos con la información del paciente
            if (patient != null) {
                Log.d("CreateActivity", "Patient found: $patient")
                editTextId.setText(patient.id.toString())
                findViewById<EditText>(R.id.editTextNombre).setText(patient.names)
                findViewById<EditText>(R.id.editTextApellidos).setText(patient.lastNames)
                findViewById<EditText>(R.id.editTextContacto).setText(patient.contact)

                val spinnerGenero = findViewById<Spinner>(R.id.spinnerGenero)
                val generoArray = resources.getStringArray(R.array.genero_array)
                val generoIndex = generoArray.indexOf(patient.gender)
                spinnerGenero.setSelection(generoIndex)

                findViewById<EditText>(R.id.editTextCumpleanos).setText(patient.birthDate.substring(0, 10))
            } else {
                Log.e("CreateActivity", "Patient not found for ID: $patientId")
                Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun setupSubmitButton() {
        val editTextId: EditText = findViewById(R.id.editTextId)
        val editTextNombre: EditText = findViewById(R.id.editTextNombre)
        val editTextApellidos: EditText = findViewById(R.id.editTextApellidos)
        val editTextContacto: EditText = findViewById(R.id.editTextContacto)
        val spinnerGenero: Spinner = findViewById(R.id.spinnerGenero)
        val editTextCumpleanos: EditText = findViewById(R.id.editTextCumpleanos)
        val buttonSubmit: Button = findViewById(R.id.buttonSubmit)

        editTextCumpleanos.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Formatear la fecha
                    val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    // Establecer el texto en el EditText
                    editTextCumpleanos.setText(formattedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        buttonSubmit.setOnClickListener {
            val id = editTextId.text.toString().toIntOrNull()
            val nombre = editTextNombre.text.toString()
            val apellidos = editTextApellidos.text.toString()
            val contacto = editTextContacto.text.toString()
            val genero = spinnerGenero.selectedItem.toString()
            val cumpleanos = editTextCumpleanos.text.toString()

            // Validar campos
            if (id == null) {
                Toast.makeText(this, "Por favor, ingrese un ID válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (nombre.isEmpty() || apellidos.isEmpty() || contacto.isEmpty() || cumpleanos.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto Patient con los datos ingresados
            val patient = Patient(id, nombre, apellidos, contacto, cumpleanos, genero)

            // Insertar o actualizar el paciente según si ya existe o no
            if (PatientHelper.getPatientById(id, ConnectionDB) != null) {
                if (PatientHelper.updatePatient(patient, ConnectionDB)) {
                    Toast.makeText(this, "Paciente actualizado correctamente", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("actualizado", true)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al actualizar el paciente", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (PatientHelper.insertPatient(patient, ConnectionDB)) {
                    Toast.makeText(this, "Paciente insertado correctamente", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("creado", true)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al insertar el paciente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
