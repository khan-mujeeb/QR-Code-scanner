package com.khandev.qrcodescanner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.khandev.qrcodescanner.R

class VCardDataAdapter(private val vCardMap: Map<String, String>) : RecyclerView.Adapter<VCardDataAdapter.VCardViewHolder>() {

    private val dataList = vCardMap.toList()

    inner class VCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val keyTextView: TextView = itemView.findViewById(R.id.keyTextView)
        val valueTextView: TextView = itemView.findViewById(R.id.valueTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_vcard_data, parent, false)
        return VCardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VCardViewHolder, position: Int) {
        val (key, value) = dataList[position]

        holder.keyTextView.text = key
        holder.valueTextView.text = value
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
