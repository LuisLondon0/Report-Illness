package com.example.report_illness.views.country


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.report_illness.R
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.CountryHelper
import com.example.report_illness.models.Country


class CreateActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_create)


        ConnectionDB.dbConn()
        setupSubmitButton()
        setupCancelButton()
        populateFieldsIfEditing()
    }


    private fun setupCancelButton() {
        val buttonCancel = findViewById<Button>(R.id.buttonCancel)
        buttonCancel.setOnClickListener {
            finish()
        }
    }


    private fun populateFieldsIfEditing() {
        val isNewCountry = intent.getBooleanExtra("isNewCountry", true)


        if (!isNewCountry) {
            val editTextId = findViewById<EditText>(R.id.editTextId)
            editTextId.isEnabled = false


            val countryId = intent.getIntExtra("countryId", 0)
            Log.d("CreateActivity", "Editing country with ID: $countryId")
            val country = CountryHelper.getCountryById(countryId, ConnectionDB)


            if (country != null) {
                Log.d("CreateActivity", "Country found: $country")
                editTextId.setText(country.id.toString())
                findViewById<EditText>(R.id.editTextNombre).setText(country.name)
            } else {
                Log.e("CreateActivity", "Country not found for ID: $countryId")
                Toast.makeText(this, "Country not found", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupSubmitButton() {
        val editTextId: EditText = findViewById(R.id.editTextId)
        val editTextName: EditText = findViewById(R.id.editTextNombre)
        val buttonSubmit: Button = findViewById(R.id.buttonSubmit)


        buttonSubmit.setOnClickListener {
            val id = editTextId.text.toString().toIntOrNull()
            val name = editTextName.text.toString()


            if (id == null) {
                Toast.makeText(this, "Por favor, ingrese un ID válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (name.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese el nombre del país.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val country = Country(id, name)


            if (CountryHelper.getCountryById(id, ConnectionDB) != null) {
                if (CountryHelper.updateCountry(country, ConnectionDB)) {
                    Toast.makeText(this, "País actualizado correctamente", Toast.LENGTH_SHORT).show()
                    setResultAndFinish(true)
                } else {
                    Toast.makeText(this, "Error al actualizar el país", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (CountryHelper.insertCountry(country, ConnectionDB)) {
                    Toast.makeText(this, "País insertado correctamente", Toast.LENGTH_SHORT).show()
                    setResultAndFinish(false)
                } else {
                    Toast.makeText(this, "Error al insertar el país", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setResultAndFinish(isUpdated: Boolean) {
        val resultIntent = Intent()
        resultIntent.putExtra(if (isUpdated) "actualizado" else "creado", true)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
