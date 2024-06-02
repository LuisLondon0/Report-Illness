package com.example.report_illness.views.country

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.report_illness.R
import com.example.report_illness.adapters.CountryAdapter
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.CountryHelper
import androidx.activity.result.contract.ActivityResultContracts

class ListActivity : AppCompatActivity() {

    private lateinit var recyclerViewCountries: RecyclerView
    private lateinit var countryAdapter: CountryAdapter

    companion object {
        const val UPDATE_LIST_REQUEST_CODE = 2
        const val CREATE_LIST_REQUEST_CODE = 1
    }

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_list)

        ConnectionDB.dbConn()

        recyclerViewCountries = findViewById(R.id.recyclerViewCountries)
        recyclerViewCountries.layoutManager = LinearLayoutManager(this)

        setupCountryList()

        val buttonGoToMenu = findViewById<Button>(R.id.buttonGoToMenu)
        buttonGoToMenu.setOnClickListener {
            finish()
        }

        val buttonCreateCountry = findViewById<Button>(R.id.buttonCreateCountry)
        buttonCreateCountry.setOnClickListener {
            val intent = Intent(this@ListActivity, CreateActivity::class.java)
            startActivityForResult(intent, CREATE_LIST_REQUEST_CODE)
        }

        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val actualizado = data?.getBooleanExtra("actualizado", false) ?: false
                val creado = data?.getBooleanExtra("creado", false) ?: false
                if (actualizado || creado) {
                    updateCountryList()
                }
            }
        }
    }

    private fun setupCountryList() {
        val allCountries = CountryHelper.getAllCountries(ConnectionDB)
        countryAdapter = CountryAdapter(allCountries)
        recyclerViewCountries.adapter = countryAdapter
    }

    fun updateCountryList() {
        val updatedCountries = CountryHelper.getAllCountries(ConnectionDB)
        countryAdapter.updateList(updatedCountries)
        recyclerViewCountries.adapter = countryAdapter // Vuelve a asignar el adaptador al RecyclerView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPDATE_LIST_REQUEST_CODE || requestCode == CREATE_LIST_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val actualizado = data?.getBooleanExtra("actualizado", false) ?: false
                val creado = data?.getBooleanExtra("creado", false) ?: false
                if (actualizado || creado) {
                    updateCountryList()
                }
            }
        }
    }
}
