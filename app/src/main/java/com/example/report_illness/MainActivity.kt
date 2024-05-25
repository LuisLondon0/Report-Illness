package com.example.report_illness

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.report_illness.adapters.PatientAdapter
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.PatientHelper
import com.example.report_illness.models.Patient
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewPatients: RecyclerView
    private lateinit var patientAdapter: PatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create)

        ConnectionDB.dbConn()

        recyclerViewPatients = findViewById(R.id.recyclerViewPatients)
        recyclerViewPatients.layoutManager = LinearLayoutManager(this)

        setupPatientList()
        setupSubmitButton()
    }

    private fun setupPatientList() {
        val allPatients = PatientHelper.getAllPatients(ConnectionDB)
        patientAdapter = PatientAdapter(allPatients)
        recyclerViewPatients.adapter = patientAdapter
    }

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
                    // Update EditText with selected date
                    editTextCumpleanos.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
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
                } else {
                    Toast.makeText(this, "Error al actualizar el paciente", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (PatientHelper.insertPatient(patient, ConnectionDB)) {
                    Toast.makeText(this, "Paciente insertado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al insertar el paciente", Toast.LENGTH_SHORT).show()
                }
            }

            // Actualizar la lista de pacientes después de insertar o actualizar
            updatePatientList()
        }
    }

    private fun updatePatientList() {
        val updatedPatients = PatientHelper.getAllPatients(ConnectionDB)
        patientAdapter.updateList(updatedPatients)
    }
}