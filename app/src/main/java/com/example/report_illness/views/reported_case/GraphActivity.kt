package com.example.report_illness.views.reported_case

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.report_illness.R
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.CountryHelper
import com.example.report_illness.helpers.ReportedCaseHelper
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.DateFormatSymbols

class GraphActivity : AppCompatActivity() {
    private lateinit var spinnerCountry: Spinner
    private lateinit var editTextYear: EditText
    private lateinit var buttonExecute: Button
    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reported_case_graph)

        // Initialize views
        spinnerCountry = findViewById(R.id.spinnerCountry)
        editTextYear = findViewById(R.id.editTextYear)
        buttonExecute = findViewById(R.id.buttonExecute)
        barChart = findViewById(R.id.barChart)

        // Desactivar la etiqueta de descripción
        barChart.description.isEnabled = false
        barChart.setDrawValueAboveBar(true)
        barChart.setBackgroundColor(Color.TRANSPARENT)
        barChart.setDrawGridBackground(false)
        barChart.setFitBars(true)

        // Check if current mode is dark
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            barChart.axisLeft.textColor = Color.WHITE
            barChart.xAxis.textColor = Color.WHITE
            barChart.legend.textColor = Color.WHITE
        } else {
            val textColor = ContextCompat.getColor(this, R.color.black)
            barChart.axisLeft.textColor = textColor
            barChart.xAxis.textColor = textColor
            barChart.legend.textColor = textColor
        }

        // Setup spinner and button
        setupCountrySpinner()

        buttonExecute.setOnClickListener {
            executeReportedCasesQuery()
        }
    }

    private fun setupCountrySpinner() {
        val countries = CountryHelper.getAllCountries(ConnectionDB)
        val countryNames = countries.map { it.name }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCountry.adapter = adapter
    }

    private fun executeReportedCasesQuery() {
        val year = editTextYear.text.toString().toInt()
        val countryName = spinnerCountry.selectedItem.toString()
        val reportedCases = ReportedCaseHelper.getReportedCasesByYearAndCountry(year, countryName, ConnectionDB)

        // Check if reportedCases list is empty
        if (reportedCases.isEmpty()) {
            barChart.setNoDataText("No chart data available")
            barChart.clear()
            barChart.invalidate()
        } else {
            // Process reported cases and update bar chart
            updateBarChart(reportedCases)
        }
    }

    private fun updateBarChart(reportedCases: List<Map<String, Any>>) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        reportedCases.forEachIndexed { index, map ->
            val month = map["Month"] as Int
            val totalCases = map["TotalCases"] as Int
            entries.add(BarEntry(index.toFloat(), totalCases.toFloat()))
            labels.add(getMonthName(month))
        }

        val dataSet = BarDataSet(entries, "Total Cases")
        dataSet.colors = getColors(reportedCases.size) // Assign different colors to bars
        dataSet.setDrawValues(true) // Enable value display on bars

        // Set value text color based on the mode
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        dataSet.valueTextColor = if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) Color.WHITE else Color.BLACK

        // Set value formatter to display integer values
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        val data = BarData(dataSet)
        data.barWidth = 0.9f // Set custom bar width
        barChart.data = data

        // Customize labels for X-axis
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        // Customize left axis
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(false)

        // Customize right axis
        val rightAxis = barChart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.isEnabled = false // Disable right axis

        // Animate the chart
        barChart.animateY(1500)

        barChart.invalidate()
    }

    private fun getMonthName(month: Int): String {
        return DateFormatSymbols().months[month - 1]
    }

    private fun getColors(count: Int): List<Int> {
        val colors = mutableListOf<Int>()
        val colorPalette = ColorTemplate.MATERIAL_COLORS

        for (i in 0 until count) {
            colors.add(colorPalette[i % colorPalette.size])
        }

        return colors
    }
}
