package com.example.report_illness.views.disease

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.report_illness.R
import com.example.report_illness.adapters.DiseaseAdapter
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.DiseaseHelper

class ListActivity : AppCompatActivity() {

    private lateinit var recyclerViewDiseases: RecyclerView
    private lateinit var diseaseAdapter: DiseaseAdapter

    companion object {
        const val UPDATE_LIST_REQUEST_CODE = 2
        const val CREATE_LIST_REQUEST_CODE = 1
    }

    private val createActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            handleActivityResult(result.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_list)

        ConnectionDB.dbConn()
        initializeViews()
        setupDiseaseList()
        setupButtons()
    }

    private fun initializeViews() {
        recyclerViewDiseases = findViewById(R.id.recyclerViewDiseases)
        recyclerViewDiseases.layoutManager = LinearLayoutManager(this)
    }

    private fun setupDiseaseList() {
        val allDiseases = DiseaseHelper.getAllDiseases(ConnectionDB)
        diseaseAdapter = DiseaseAdapter(allDiseases)
        recyclerViewDiseases.adapter = diseaseAdapter
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.buttonGoToMenu).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.buttonCreateDisease).setOnClickListener {
            val intent = Intent(this@ListActivity, CreateActivity::class.java)
            startActivityForResult(intent, CREATE_LIST_REQUEST_CODE)
        }
    }

    private fun handleActivityResult(data: Intent?) {
        val isUpdated = data?.getBooleanExtra("actualizado", false) ?: false
        val isCreated = data?.getBooleanExtra("creado", false) ?: false
        if (isUpdated || isCreated) {
            updateDiseaseList()
        }
    }

    fun updateDiseaseList() {
        val updatedDiseases = DiseaseHelper.getAllDiseases(ConnectionDB)
        diseaseAdapter.updateList(updatedDiseases)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == UPDATE_LIST_REQUEST_CODE || requestCode == CREATE_LIST_REQUEST_CODE) &&
            resultCode == Activity.RESULT_OK) {
            handleActivityResult(data)
        }
    }
}