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
import com.example.report_illness.helpers.CountryHelper
import com.example.report_illness.models.Country
import com.example.report_illness.views.country.CreateActivity
import com.example.report_illness.views.country.ListActivity

class CountryAdapter(private var countries: List<Country>) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val currentCountry = countries[position]
        holder.bind(currentCountry)
    }

    override fun getItemCount() = countries.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newCountries: List<Country>) {
        countries = newCountries
        notifyDataSetChanged()
    }

    inner class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewCountryName: TextView = itemView.findViewById(R.id.textViewCountryName)
        private val textViewCountryID: TextView = itemView.findViewById(R.id.textViewCountryID)
        private val buttonEditCountry: Button = itemView.findViewById(R.id.buttonEditCountry)
        private val buttonDeleteCountry: Button = itemView.findViewById(R.id.buttonDeleteCountry)

        fun bind(country: Country) {
            textViewCountryName.text = country.name
            textViewCountryID.text = "ID: ${country.id}"

            buttonEditCountry.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, CreateActivity::class.java)
                intent.putExtra("countryId", country.id)
                intent.putExtra("isNewCountry", false)
                (context as? ListActivity)?.startActivityForResult(intent, ListActivity.CREATE_LIST_REQUEST_CODE)
            }

            buttonDeleteCountry.setOnClickListener {
                val context = itemView.context
                if (CountryHelper.deleteCountry(country.id, ConnectionDB)) {
                    Toast.makeText(context, "País eliminado", Toast.LENGTH_SHORT).show()
                    if (context is ListActivity) {
                        context.updateCountryList()
                    }
                } else {
                    Toast.makeText(context, "Error al eliminar el país", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
