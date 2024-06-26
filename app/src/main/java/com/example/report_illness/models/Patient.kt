package com.example.report_illness.models

import java.text.SimpleDateFormat
import java.util.*

class Patient(
    id: Int,
    names: String,
    lastNames: String,
    contact: String,
    var birthDate: String,
    var gender: String
) : Person(id, names, lastNames, contact) {
    fun getFormattedBirthDate(): String {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("yyyy-MMMM-dd", Locale.getDefault())
        val fechaDate: Date? = formatoEntrada.parse(birthDate)
        return if (fechaDate != null) {
            formatoSalida.format(fechaDate)
        } else {
            // Manejo de errores o devolución de un valor predeterminado si la fecha no se puede analizar correctamente
            "Fecha no válida"
        }
    }
}

