package com.example.report_illness.adapters

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
import com.example.report_illness.helpers.CityHelper
import com.example.report_illness.helpers.DiseaseHelper
import com.example.report_illness.helpers.PatientHelper
import com.example.report_illness.helpers.ReportedCaseHelper
import com.example.report_illness.models.ReportedCase
import com.example.report_illness.views.reported_case.CreateActivity
import com.example.report_illness.views.reported_case.ListActivity

class ReportedCaseAdapter(private var reportedCases: List<ReportedCase>) : RecyclerView.Adapter<ReportedCaseAdapter.ReportedCaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportedCaseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_reported_case, parent, false)
        return ReportedCaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReportedCaseViewHolder, position: Int) {
        val currentReportedCase = reportedCases[position]
        holder.bind(currentReportedCase)
    }

    override fun getItemCount() = reportedCases.size

    fun updateList(newReportedCases: List<ReportedCase>) {
        reportedCases = newReportedCases
        notifyDataSetChanged()
    }

    inner class ReportedCaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewReportedCaseID: TextView = itemView.findViewById(R.id.textViewReportedCaseID)
        private val textViewReportedDate: TextView = itemView.findViewById(R.id.textViewReportedDate)
        private val textViewPatientName: TextView = itemView.findViewById(R.id.textViewPatientName)
        private val textViewCityName: TextView = itemView.findViewById(R.id.textViewCityName)
        private val textViewDiseaseName: TextView = itemView.findViewById(R.id.textViewDiseaseName)
        private val buttonEditReportedCase: Button = itemView.findViewById(R.id.buttonEditReportedCase)
        private val buttonDeleteReportedCase: Button = itemView.findViewById(R.id.buttonDeleteReportedCase)

        fun bind(reportedCase: ReportedCase) {
            textViewReportedCaseID.text = "ID: ${reportedCase.id}"
            textViewReportedDate.text = "Fecha Reportada: ${reportedCase.getFormattedReportedDate()}"

            val patient = PatientHelper.getPatientById(reportedCase.patientId, ConnectionDB)
            val patientName = patient?.let { "${it.id} - ${it.names} ${it.lastNames}" } ?: "Paciente Desconocido"
            textViewPatientName.text = "Paciente: $patientName"

            val city = CityHelper.getCityById(reportedCase.cityId, ConnectionDB)
            val cityName = city?.name ?: "Ciudad Desconocida"
            textViewCityName.text = "Ciudad: $cityName"

            val disease = DiseaseHelper.getDiseaseById(reportedCase.diseaseId, ConnectionDB)
            val diseaseName = disease?.name ?: "Enfermedad Desconocida"
            textViewDiseaseName.text = "Enfermedad: $diseaseName"

            buttonEditReportedCase.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, CreateActivity::class.java)
                intent.putExtra("reportedCaseId", reportedCase.id)
                intent.putExtra("isNewReportedCase", false)
                (context as? ListActivity)?.startActivityForResult(intent, ListActivity.UPDATE_LIST_REQUEST_CODE)
            }

            buttonDeleteReportedCase.setOnClickListener {
                val context = itemView.context
                if (ReportedCaseHelper.deleteReportedCase(reportedCase.id, ConnectionDB)) {
                    Toast.makeText(context, "Caso reportado eliminado", Toast.LENGTH_SHORT).show()
                    if (context is ListActivity) {
                        context.updateReportedCaseList()
                    }
                } else {
                    Toast.makeText(context, "Error al eliminar el caso reportado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
