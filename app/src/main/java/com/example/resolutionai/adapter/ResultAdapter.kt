package com.example.resolutionai.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.resolutionai.R
import com.example.resolutionai.data.ScannerResult

class ResultAdapter(private val context: Context, private val resultList: List<ScannerResult>) :
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

        fun bind(result: ScannerResult) {
            val data = result.result

            itemView.setOnClickListener {
                if (isPlainTextOrUrl(data)) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                    context.startActivity(intent)

                } else {
                    copyTextToClipboard(context, data)
                }
            }
            if (isPlainTextOrUrl(result.result)) {
                logo.setImageDrawable(context.getDrawable(R.drawable.browser))
            } else {
                logo.setImageDrawable(context.getDrawable(R.drawable.baseline_format_color_text_24))
            }

            resultTextView.text = data
        }
    }

    fun copyTextToClipboard(context: Context, text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "copied", Toast.LENGTH_SHORT).show()
    }

    fun isPlainTextOrUrl(input: String): Boolean {
        return URLUtil.isValidUrl(input)
    }
}

