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
import com.example.report_illness.helpers.DiseaseHelper
import com.example.report_illness.models.Disease
import com.example.report_illness.views.disease.CreateActivity
import com.example.report_illness.views.disease.ListActivity

class DiseaseAdapter(private var diseases: List<Disease>) : RecyclerView.Adapter<DiseaseAdapter.DiseaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_disease, parent, false)
        return DiseaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiseaseViewHolder, position: Int) {
        val currentDisease = diseases[position]
        holder.bind(currentDisease)
    }

    override fun getItemCount() = diseases.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newDiseases: List<Disease>) {
        diseases = newDiseases
        notifyDataSetChanged()
    }

    inner class DiseaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDiseaseId: TextView = itemView.findViewById(R.id.textViewDiseaseId)
        private val textViewDiseaseName: TextView = itemView.findViewById(R.id.textViewDiseaseName)
        private val textViewDiseaseDescription: TextView = itemView.findViewById(R.id.textViewDiseaseDescription)
        private val textViewDiseaseUrgency: TextView = itemView.findViewById(R.id.textViewDiseaseUrgency)
        private val textViewDiseaseVaccine: TextView = itemView.findViewById(R.id.textViewDiseaseVaccine)
        private val textViewDiseaseTreatment: TextView = itemView.findViewById(R.id.textViewDiseaseTreatment)
        private val buttonEditDisease: Button = itemView.findViewById(R.id.buttonEditDisease)
        private val buttonDeleteDisease: Button = itemView.findViewById(R.id.buttonDeleteDisease)

        fun bind(disease: Disease) {
            textViewDiseaseId.text = "ID: ${disease.id}"
            textViewDiseaseName.text = disease.name
            textViewDiseaseDescription.text = "Descripción: ${disease.description}"
            textViewDiseaseUrgency.text = "Urgencia: ${disease.urgency}"
            textViewDiseaseVaccine.text = "Vacuna: ${if (disease.vaccine) "Sí" else "No"}"
            textViewDiseaseTreatment.text = "Tratamiento: ${if (disease.treatment) "Sí" else "No"}"

            buttonEditDisease.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, CreateActivity::class.java)
                intent.putExtra("diseaseId", disease.id)
                intent.putExtra("isNewDisease", false)
                (context as? ListActivity)?.startActivityForResult(intent, ListActivity.CREATE_LIST_REQUEST_CODE)
            }

            buttonDeleteDisease.setOnClickListener {
                val context = itemView.context
                if (DiseaseHelper.deleteDisease(disease.id, ConnectionDB)) {
                    Toast.makeText(context, "Enfermedad eliminada", Toast.LENGTH_SHORT).show()
                    if (context is ListActivity) {
                        context.updateDiseaseList()
                    }
                } else {
                    Toast.makeText(context, "Error al eliminar la enfermedad", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
