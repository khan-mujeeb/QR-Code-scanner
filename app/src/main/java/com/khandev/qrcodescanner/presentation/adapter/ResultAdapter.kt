package com.khandev.qrcodescanner.presentation.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.khandev.qrcodescanner.presentation.activity.ResultActivity.Companion.copyTextToClipboard
import com.khandev.qrcodescanner.data.QrCodeEntity
import com.khandev.qrcodescanner.R

class ResultAdapter(private val context: Context, private val resultList: List<QrCodeEntity>) :
    RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result, parent, false)
        return ResultViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val currentResult = resultList[position]
        holder.bind(currentResult)
    }

    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val logo: ImageView = itemView.findViewById(R.id.Rclogo)
        private val resultTextView: TextView = itemView.findViewById(R.id.Rcdata)

        fun bind(result: QrCodeEntity) {
            val data = result.qrcodeData
            val cat = result.category

            itemView.setOnClickListener {
                if (cat == "url") {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                    context.startActivity(intent)

                } else {
                    copyTextToClipboard(context, data)
                    Toast.makeText(context, "copied", Toast.LENGTH_SHORT).show()

                }
            }
            if (cat == "url") {
                logo.setImageDrawable(context.getDrawable(R.drawable.browser_new))
            } else {
                logo.setImageDrawable(context.getDrawable(R.drawable.plain_text))
            }

            resultTextView.text = data
        }


    }

    fun getQrCodeAt(position: Int): QrCodeEntity {
        return resultList[position]
    }
}

