package com.example.report_illness

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.report_illness.R
import com.example.report_illness.views.patient.ListActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonPatients = findViewById<Button>(R.id.buttonPatients)
        buttonPatients.setOnClickListener {
            val intent = Intent(this@MainActivity, ListActivity::class.java)
            startActivity(intent)
        }

        // Define OnClickListener for other buttons in a similar way
    }
}
