package com.example.report_illness.views.disease

import android.app.Activity
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

class CreateActivity : AppCompatActivity() {

    private lateinit var editTextId: EditText
    private lateinit var editTextNombre: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var editTextUrgencia: EditText
    private lateinit var checkBoxVacuna: CheckBox
    private lateinit var checkBoxTratamiento: CheckBox
    private lateinit var buttonSubmit: Button
    private lateinit var buttonCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_create)

        ConnectionDB.dbConn()

        initializeViews()
        setupSubmitButton()
        setupCancelButton()

        if (!isNewDisease()) {
            loadDiseaseData()
        }
    }

    private fun initializeViews() {
        editTextId = findViewById(R.id.editTextId)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)
        editTextUrgencia = findViewById(R.id.editTextUrgencia)
        checkBoxVacuna = findViewById(R.id.checkBoxVacuna)
        checkBoxTratamiento = findViewById(R.id.checkBoxTratamiento)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        buttonCancel = findViewById(R.id.buttonCancel)
    }

    private fun setupCancelButton() {
        buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun isNewDisease(): Boolean {
        return intent.getBooleanExtra("isNewDisease", true)
    }

    private fun loadDiseaseData() {
        editTextId.isEnabled = false
        val diseaseId = intent.getIntExtra("diseaseId", 0)
        val disease = DiseaseHelper.getDiseaseById(diseaseId, ConnectionDB)

        if (disease != null) {
            populateDiseaseFields(disease)
        } else {
            Toast.makeText(this, "Enfermedad no encontrada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateDiseaseFields(disease: Disease) {
        editTextId.setText(disease.id.toString())
        editTextNombre.setText(disease.name)
        editTextDescripcion.setText(disease.description)
        editTextUrgencia.setText(disease.urgency)
        checkBoxVacuna.isChecked = disease.vaccine
        checkBoxTratamiento.isChecked = disease.treatment
    }

    private fun setupSubmitButton() {
        buttonSubmit.setOnClickListener {
            if (validateFields()) {
                val disease = createDiseaseFromFields()
                if (DiseaseHelper.getDiseaseById(disease.id, ConnectionDB) != null) {
                    updateDisease(disease)
                } else {
                    insertDisease(disease)
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        val id = editTextId.text.toString().toIntOrNull()
        val nombre = editTextNombre.text.toString()
        val descripcion = editTextDescripcion.text.toString()
        val urgencia = editTextUrgencia.text.toString()

        return when {
            id == null -> {
                showToast("Por favor, ingrese un ID válido.")
                false
            }
            nombre.isEmpty() || descripcion.isEmpty() || urgencia.isEmpty() -> {
                showToast("Por favor, complete todos los campos.")
                false
            }
            else -> true
        }
    }

    private fun createDiseaseFromFields(): Disease {
        val id = editTextId.text.toString().toInt()
        val nombre = editTextNombre.text.toString()
        val descripcion = editTextDescripcion.text.toString()
        val urgencia = editTextUrgencia.text.toString()
        val tieneVacuna = checkBoxVacuna.isChecked
        val tieneTratamiento = checkBoxTratamiento.isChecked
        return Disease(id, nombre, descripcion, urgencia, tieneVacuna, tieneTratamiento)
    }

    private fun updateDisease(disease: Disease) {
        if (DiseaseHelper.updateDisease(disease, ConnectionDB)) {
            showToast("Enfermedad actualizada correctamente")
            setResultAndFinish("actualizado")
        } else {
            showToast("Error al actualizar la enfermedad")
        }
    }

    private fun insertDisease(disease: Disease) {
        if (DiseaseHelper.insertDisease(disease, ConnectionDB)) {
            showToast("Enfermedad insertada correctamente")
            setResultAndFinish("creado")
        } else {
            showToast("Error al insertar la enfermedad")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setResultAndFinish(action: String) {
        val resultIntent = Intent()
        resultIntent.putExtra(action, true)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}