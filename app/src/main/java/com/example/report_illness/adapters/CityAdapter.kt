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
import com.example.report_illness.helpers.CityHelper
import com.example.report_illness.helpers.CountryHelper
import com.example.report_illness.models.City
import com.example.report_illness.views.city.CreateActivity
import com.example.report_illness.views.city.ListActivity

class CityAdapter(private var cities: List<City>) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return CityViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val currentCity = cities[position]
        holder.bind(currentCity)
    }

    override fun getItemCount() = cities.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newCities: List<City>) {
        cities = newCities
        notifyDataSetChanged()
    }

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewCityName: TextView = itemView.findViewById(R.id.textViewCityName)
        private val textViewCityID: TextView = itemView.findViewById(R.id.textViewCityID)
        private val textViewLatitude: TextView = itemView.findViewById(R.id.textViewCityLatitude)
        private val textViewLongitude: TextView = itemView.findViewById(R.id.textViewCityLongitude)
        private val textViewCountryName: TextView = itemView.findViewById(R.id.textViewCountryName) // Actualizado
        private val buttonEditCity: Button = itemView.findViewById(R.id.buttonEditCity)
        private val buttonDeleteCity: Button = itemView.findViewById(R.id.buttonDeleteCity)

        fun bind(city: City) {
            textViewCityName.text = city.name
            textViewCityID.text = "ID: ${city.id}"
            textViewLatitude.text = "Latitud: ${city.latitude}"
            textViewLongitude.text = "Longitud: ${city.longitude}"

            val country = CountryHelper.getCountryById(city.countryId, ConnectionDB)
            val countryName = country?.name ?: "País Desconocido"
            textViewCountryName.text = "País: $countryName"

            buttonEditCity.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, CreateActivity::class.java)
                intent.putExtra("cityId", city.id)
                intent.putExtra("isNewCity", false)
                (context as? ListActivity)?.startActivityForResult(intent, ListActivity.CREATE_LIST_REQUEST_CODE)
            }

            buttonDeleteCity.setOnClickListener {
                val context = itemView.context
                if (CityHelper.deleteCity(city.id, ConnectionDB)) {
                    Toast.makeText(context, "Ciudad eliminada", Toast.LENGTH_SHORT).show()
                    if (context is ListActivity) {
                        context.updateCityList()
                    }
                } else {
                    Toast.makeText(context, "Error al eliminar la ciudad", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
