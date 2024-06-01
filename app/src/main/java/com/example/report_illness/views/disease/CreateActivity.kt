package com.example.report_illness.views.disease

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.report_illness.R
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.DiseaseHelper
import com.example.report_illness.models.Disease
import java.util.Calendar

class CreateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_create)

        ConnectionDB.dbConn()

        setupSubmitButton()

        val buttonCancel = findViewById<Button>(R.id.buttonCancel)
        buttonCancel.setOnClickListener {
            finish()
        }

        val isNewDisease = intent.getBooleanExtra("isNewDisease", true)

        if (!isNewDisease) {
            val editTextId = findViewById<EditText>(R.id.editTextId)
            editTextId.isEnabled = false

            val diseaseId = intent.getIntExtra("diseaseId", 0)
            val disease = DiseaseHelper.getDiseaseById(diseaseId, ConnectionDB)

            // Llenar los campos con la información de la enfermedad
            if (disease != null) {
                editTextId.setText(disease.id.toString())
                findViewById<EditText>(R.id.editTextNombre).setText(disease.name)
                findViewById<EditText>(R.id.editTextDescripcion).setText(disease.description)
                findViewById<EditText>(R.id.editTextUrgencia).setText(disease.urgency)
                findViewById<CheckBox>(R.id.checkBoxVacuna).isChecked = disease.vaccine
                findViewById<CheckBox>(R.id.checkBoxTratamiento).isChecked = disease.treatment
            } else {
                Toast.makeText(this, "Enfermedad no encontrada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun setupSubmitButton() {
        val editTextId: EditText = findViewById(R.id.editTextId)
        val editTextNombre: EditText = findViewById(R.id.editTextNombre)
        val editTextDescripcion: EditText = findViewById(R.id.editTextDescripcion)
        val editTextUrgencia: EditText = findViewById(R.id.editTextUrgencia)
        val checkBoxVacuna: CheckBox = findViewById(R.id.checkBoxVacuna)
        val checkBoxTratamiento: CheckBox = findViewById(R.id.checkBoxTratamiento)
        val buttonSubmit: Button = findViewById(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener {
            val id = editTextId.text.toString().toIntOrNull()
            val nombre = editTextNombre.text.toString()
            val descripcion = editTextDescripcion.text.toString()
            val urgencia = editTextUrgencia.text.toString()
            val tieneVacuna = checkBoxVacuna.isChecked
            val tieneTratamiento = checkBoxTratamiento.isChecked

            // Validar campos
            if (id == null) {
                Toast.makeText(this, "Por favor, ingrese un ID válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (nombre.isEmpty() || descripcion.isEmpty() || urgencia.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto Disease con los datos ingresados
            val disease = Disease(id, nombre, descripcion, urgencia, tieneVacuna, tieneTratamiento)

            // Insertar o actualizar la enfermedad según si ya existe o no
            if (DiseaseHelper.getDiseaseById(id, ConnectionDB) != null) {
                if (DiseaseHelper.updateDisease(disease, ConnectionDB)) {
                    Toast.makeText(this, "Enfermedad actualizada correctamente", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("actualizado", true)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al actualizar la enfermedad", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (DiseaseHelper.insertDisease(disease, ConnectionDB)) {
                    Toast.makeText(this, "Enfermedad insertada correctamente", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("creado", true)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al insertar la enfermedad", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
