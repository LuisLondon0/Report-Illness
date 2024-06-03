package com.example.report_illness.models

import java.text.SimpleDateFormat
import java.util.*

class ReportedCase(
    var id: Int,
    var reportedDate: String,
    var patientId: Int,
    var diseaseId: Int,
    var cityId: Int
) {
    fun getFormattedReportedDate(): String {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("yyyy-MMMM-dd", Locale.getDefault())
        val fechaDate: Date? = formatoEntrada.parse(reportedDate)
        return if (fechaDate != null) {
            formatoSalida.format(fechaDate)
        } else {
            // Manejo de errores o devolución de un valor predeterminado si la fecha no se puede analizar correctamente
            "Fecha no válida"
        }
    }
}
