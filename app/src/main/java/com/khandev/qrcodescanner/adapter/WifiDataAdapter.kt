package com.khandev.qrcodescanner.adapter

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.khandev.qrcodescanner.R

class WifiDataAdapter(
    private val wifiData: List<Pair<String, String>>
) : RecyclerView.Adapter<WifiDataAdapter.WifiDataViewHolder>() {

    // ViewHolder class to hold views for each item
    class WifiDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val keyTextView: TextView = itemView.findViewById(R.id.keyTextView)
        val valueTextView: TextView = itemView.findViewById(R.id.valueTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiDataViewHolder {
        // Inflate the item view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vcard_data, parent, false)
        return WifiDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: WifiDataViewHolder, position: Int) {
        // Get the current key-value pair
        val (key, value) = wifiData[position]

        // Bind the data to the views
        holder.keyTextView.text = key
        holder.valueTextView.text = value
    }

    override fun getItemCount(): Int {
        return wifiData.size
    }
}

