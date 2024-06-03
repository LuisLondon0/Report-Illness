package com.example.report_illness.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.report_illness.R
import com.example.report_illness.database.ConnectionDB
import com.example.report_illness.helpers.PatientHelper
import com.example.report_illness.models.Patient
import com.example.report_illness.views.patient.CreateActivity
import com.example.report_illness.views.patient.ListActivity
import androidx.activity.result.contract.ActivityResultContracts

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

    @SuppressLint("NotifyDataSetChanged")
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
        private val buttonEditPatient: Button = itemView.findViewById(R.id.buttonEditPatient)
        private val buttonDeletePatient: Button = itemView.findViewById(R.id.buttonDeletePatient)

        fun bind(patient: Patient) {
            textViewPatientName.text = "${patient.names} ${patient.lastNames}"
            textViewPatientID.text = "ID: ${patient.id}"
            textViewPatientContact.text = "Contacto: ${patient.contact}"
            textViewPatientBirthDate.text = "Fecha de Nacimiento: ${patient.getFormattedBirthDate()}"
            textViewPatientGender.text = "Género: ${patient.gender}"


            buttonEditPatient.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, CreateActivity::class.java)
                intent.putExtra("patientId", patient.id)
                intent.putExtra("isNewPatient", false)
                (context as? ListActivity)?.startActivityForResult(intent, ListActivity.CREATE_LIST_REQUEST_CODE)
            }

            buttonDeletePatient.setOnClickListener {
                val context = itemView.context
                if (PatientHelper.deletePatient(patient.id, ConnectionDB)) {
                    Toast.makeText(context, "Paciente eliminado", Toast.LENGTH_SHORT).show()
                    if (context is ListActivity) {
                        context.updatePatientList()
                    }
                } else {
                    Toast.makeText(context, "Error al eliminar el paciente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}