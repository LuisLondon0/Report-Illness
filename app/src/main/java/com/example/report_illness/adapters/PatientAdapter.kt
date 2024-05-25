package com.example.report_illness.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.report_illness.R
import com.example.report_illness.models.Patient

class PatientAdapter(private var patients: List<Patient>) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val currentPatient = patients[position]
        holder.bind(currentPatient)
    }

    override fun getItemCount() = patients.size

    fun updateList(newPatients: List<Patient>) {
        patients = newPatients
        notifyDataSetChanged()
    }

    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewPatientName: TextView = itemView.findViewById(R.id.textViewPatientName)
        private val textViewPatientID: TextView = itemView.findViewById(R.id.textViewPatientID)
        private val textViewPatientContact: TextView = itemView.findViewById(R.id.textViewPatientContact)
        private val textViewPatientBirthDate: TextView = itemView.findViewById(R.id.textViewPatientBirthDate)
        private val textViewPatientGender: TextView = itemView.findViewById(R.id.textViewPatientGender)

        fun bind(patient: Patient) {
            textViewPatientName.text = "${patient.names} ${patient.lastNames}"
            textViewPatientID.text = "ID: ${patient.id}"
            textViewPatientContact.text = "Contacto: ${patient.contact}"
            textViewPatientBirthDate.text = "Fecha de Nacimiento: ${patient.birthDate}"
            textViewPatientGender.text = "Género: ${patient.gender}"
        }
    }
}