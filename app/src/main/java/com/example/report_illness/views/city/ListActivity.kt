package com.example.report_illness.views.city


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.report_illness.R
import com.example.report_illness.adapters.CityAdapter
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.CityHelper


class ListActivity : AppCompatActivity() {


    private lateinit var recyclerViewCities: RecyclerView
    private lateinit var cityAdapter: CityAdapter


    companion object {
        const val UPDATE_LIST_REQUEST_CODE = 2
        const val CREATE_LIST_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_list)


        ConnectionDB.dbConn()
        setupRecyclerView()
        setupButtons()
        setupActivityResultLauncher()
    }


    private fun setupRecyclerView() {
        recyclerViewCities = findViewById(R.id.recyclerViewCities)
        recyclerViewCities.layoutManager = LinearLayoutManager(this)
        setupCityList()
    }


    private fun setupButtons() {
        findViewById<Button>(R.id.buttonGoToMenu).setOnClickListener {
            finish()
        }


        findViewById<Button>(R.id.buttonCreateCity).setOnClickListener {
            val intent = Intent(this@ListActivity, CreateActivity::class.java)
            startActivityForResult(intent, CREATE_LIST_REQUEST_CODE)
        }
    }


    private fun setupActivityResultLauncher() {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val actualizado = data?.getBooleanExtra("actualizado", false) ?: false
                val creado = data?.getBooleanExtra("creado", false) ?: false
                if (actualizado || creado) {
                    updateCityList()
                }
            }
        }
    }


    private fun setupCityList() {
        val allCities = CityHelper.getAllCities(ConnectionDB)
        cityAdapter = CityAdapter(allCities)
        recyclerViewCities.adapter = cityAdapter
    }


    fun updateCityList() {
        val updatedCities = CityHelper.getAllCities(ConnectionDB)
        cityAdapter.updateList(updatedCities)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == UPDATE_LIST_REQUEST_CODE || requestCode == CREATE_LIST_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val actualizado = data?.getBooleanExtra("actualizado", false) ?: false
                val creado = data?.getBooleanExtra("creado", false) ?: false
                if (actualizado || creado) {
                    updateCityList()
                }
            }
        }
    }
}

