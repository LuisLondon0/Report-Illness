package com.example.report_illness.views.disease

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.report_illness.R
import com.example.report_illness.adapters.DiseaseAdapter
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.DiseaseHelper
import androidx.activity.result.contract.ActivityResultContracts

class ListActivity : AppCompatActivity() {

    private lateinit var recyclerViewDiseases: RecyclerView
    private lateinit var diseaseAdapter: DiseaseAdapter

    companion object {
        const val UPDATE_LIST_REQUEST_CODE = 2
        const val CREATE_LIST_REQUEST_CODE = 1
    }

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_list)

        ConnectionDB.dbConn()

        recyclerViewDiseases = findViewById(R.id.recyclerViewDiseases)
        recyclerViewDiseases.layoutManager = LinearLayoutManager(this)

        setupDiseaseList()

        val buttonGoToMenu = findViewById<Button>(R.id.buttonGoToMenu)
        buttonGoToMenu.setOnClickListener {
            finish()
        }

        val buttonCreateDisease = findViewById<Button>(R.id.buttonCreateDisease)
        buttonCreateDisease.setOnClickListener {
            val intent = Intent(this@ListActivity, CreateActivity::class.java)
            startActivityForResult(intent, CREATE_LIST_REQUEST_CODE)
        }

        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val actualizado = data?.getBooleanExtra("actualizado", false) ?: false
                val creado = data?.getBooleanExtra("creado", false) ?: false
                if (actualizado || creado) {
                    updateDiseaseList()
                }
            }
        }
    }

    private fun setupDiseaseList() {
        val allDiseases = DiseaseHelper.getAllDiseases(ConnectionDB)
        diseaseAdapter = DiseaseAdapter(allDiseases)
        recyclerViewDiseases.adapter = diseaseAdapter
    }

    fun updateDiseaseList() {
        val updatedDiseases = DiseaseHelper.getAllDiseases(ConnectionDB)
        diseaseAdapter.updateList(updatedDiseases)
        recyclerViewDiseases.adapter = diseaseAdapter // Vuelve a asignar el adaptador al RecyclerView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPDATE_LIST_REQUEST_CODE || requestCode == CREATE_LIST_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val actualizado = data?.getBooleanExtra("actualizado", false) ?: false
                val creado = data?.getBooleanExtra("creado", false) ?: false
                if (actualizado || creado) {
                    updateDiseaseList()
                }
            }
        }
    }
}
