package com.example.report_illness.views.city

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.report_illness.R
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.CityHelper
import com.example.report_illness.helpers.CountryHelper
import com.example.report_illness.models.City

class CreateActivity : AppCompatActivity() {

    private lateinit var countryNames: List<String>
    private lateinit var countryIds: List<Int>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_create)

        ConnectionDB.dbConn()

        setupCountrySpinner()
        setupSubmitButton()

        val buttonCancel = findViewById<Button>(R.id.buttonCancel)
        buttonCancel.setOnClickListener {
            finish()
        }

        val isNewCity = intent.getBooleanExtra("isNewCity", true)

        if (!isNewCity) {
            val editTextId = findViewById<EditText>(R.id.editTextId)
            editTextId.isEnabled = false

            val cityId = intent.getIntExtra("cityId", 0)
            Log.d("CreateActivity", "Editing city with ID: $cityId")
            val city = CityHelper.getCityById(cityId, ConnectionDB)

            // Llenar los campos con la información de la ciudad
            if (city != null) {
                Log.d("CreateActivity", "City found: $city")
                editTextId.setText(city.id.toString())
                findViewById<EditText>(R.id.editTextNombre).setText(city.name)
                findViewById<EditText>(R.id.editTextLatitud).setText(city.latitude.toString())
                findViewById<EditText>(R.id.editTextLongitud).setText(city.longitude.toString())

                val spinnerCountry = findViewById<Spinner>(R.id.spinnerPaisId)
                val countryIndex = countryIds.indexOf(city.countryId)
                spinnerCountry.setSelection(countryIndex)
            } else {
                Log.e("CreateActivity", "City not found for ID: $cityId")
                Toast.makeText(this, "City not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCountrySpinner() {
        val countries = CountryHelper.getAllCountries(ConnectionDB)
        countryNames = countries.map { it.name }
        countryIds = countries.map { it.id }

        val spinnerCountry = findViewById<Spinner>(R.id.spinnerPaisId)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCountry.adapter = adapter
    }

    @SuppressLint("DefaultLocale")
    private fun setupSubmitButton() {
        val editTextId: EditText = findViewById(R.id.editTextId)
        val editTextNombre: EditText = findViewById(R.id.editTextNombre)
        val editTextLatitud: EditText = findViewById(R.id.editTextLatitud)
        val editTextLongitud: EditText = findViewById(R.id.editTextLongitud)
        val spinnerCountry: Spinner = findViewById(R.id.spinnerPaisId)
        val buttonSubmit: Button = findViewById(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener {
            val id = editTextId.text.toString().toIntOrNull()
            val nombre = editTextNombre.text.toString()
            val latitud = editTextLatitud.text.toString().toDoubleOrNull()
            val longitud = editTextLongitud.text.toString().toDoubleOrNull()
            val countryIndex = spinnerCountry.selectedItemPosition
            val countryId = countryIds[countryIndex]

            // Validar campos
            if (id == null) {
                Toast.makeText(this, "Por favor, ingrese un ID válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (nombre.isEmpty() || latitud == null || longitud == null) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto City con los datos ingresados
            val city = City(id, nombre, latitud, longitud, countryId)

            // Insertar o actualizar la ciudad según si ya existe o no
            if (CityHelper.getCityById(id, ConnectionDB) != null) {
                if (CityHelper.updateCity(city, ConnectionDB)) {
                    Toast.makeText(this, "Ciudad actualizada correctamente", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("actualizado", true)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al actualizar la ciudad", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (CityHelper.insertCity(city, ConnectionDB)) {
                    Toast.makeText(this, "Ciudad insertada correctamente", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("creado", true)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al insertar la ciudad", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
