package com.example.report_illness

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.report_illness.R
import com.example.report_illness.views.patient.ListActivity as PatientListActivity
import com.example.report_illness.views.disease.ListActivity as DiseaseListActivity
import com.example.report_illness.views.country.ListActivity as CountryListActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonPatients = findViewById<Button>(R.id.buttonPatients)
        buttonPatients.setOnClickListener {
            val intent = Intent(this@MainActivity, PatientListActivity::class.java)
            startActivity(intent)
        }

        val buttonDiseases = findViewById<Button>(R.id.buttonDiseases)
        buttonDiseases.setOnClickListener {
            val intent = Intent(this@MainActivity, DiseaseListActivity::class.java)
            startActivity(intent)
        }

        val buttonCountries = findViewById<Button>(R.id.buttonCountries)
        buttonCountries.setOnClickListener {
            val intent = Intent(this@MainActivity, CountryListActivity::class.java)
            startActivity(intent)
        }

    }
}
