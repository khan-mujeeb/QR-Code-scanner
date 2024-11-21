package com.khandev.qrcodescanner.adapter

import android.annotation.SuppressLint
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
import com.khandev.qrcodescanner.database.data.QrCodeEntity
import com.khandev.qrcodescanner.R
import com.khandev.qrcodescanner.utlis.Categories
import com.khandev.qrcodescanner.utlis.QrCodeParser
import com.khandev.qrcodescanner.utlis.ScannerUtils.copyTextToClipboard

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

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(result: QrCodeEntity) {
            val data = result.qrcodeData
            val cat = result.category

            itemView.setOnClickListener {

                when(cat) {
                    Categories.URL -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                        context.startActivity(intent)
                    }

                    Categories.WIFI -> {
                        val wifiData = QrCodeParser.parseWifiQRCode(data)
                        copyTextToClipboard(context, wifiData!!.password!!)
                    }

                    Categories.PAYMENT -> {
                        val upiData = QrCodeParser.parseUpiQrCode(data)
                        copyTextToClipboard(context, upiData.upiId!!)

                    }

                    Categories.CONTACT -> {
                        val (name, phone) = extractNameAndPhone(data)
                        copyTextToClipboard(context, phone!! )
                    }
                    else -> {
                        copyTextToClipboard(context, data)
                    }
                }

            }
            when (cat) {



                Categories.URL -> {
                    logo.setImageDrawable(context.getDrawable(R.drawable.browser_new))
                    resultTextView.text = data
                }
                Categories.CONTACT -> {
                    logo.setImageDrawable(context.getDrawable(R.drawable.baseline_contact_phone_24))
                    val (name, phone) = extractNameAndPhone(data)
                    resultTextView.text = "$name\n$phone"
                }

                Categories.WIFI -> {
                    logo.setImageDrawable(context.getDrawable(R.drawable.wifi_logo))
                    val wifiData = QrCodeParser.parseWifiQRCode(data)
                    resultTextView.text = wifiData!!.ssid + "\n" + wifiData!!.password
                }

                Categories.PAYMENT -> {
                    logo.setImageDrawable(context.getDrawable(R.drawable.upi_icon))
                    val upiData = QrCodeParser.parseUpiQrCode(data)
                    resultTextView.text = upiData!!.payeeName + "\n" + upiData!!.upiId
                }
                else -> {
                    logo.setImageDrawable(context.getDrawable(R.drawable.plain_text))
                    resultTextView.text = data

                }
            }


        }


    }

    fun getQrCodeAt(position: Int): QrCodeEntity {
        return resultList[position]
    }

    fun extractNameAndPhone(vCard: String): Pair<String?, String?> {
        val nameRegex = Regex("FN:(.*)")
        val phoneRegex = Regex("TEL:(.*)")

        val name = nameRegex.find(vCard)?.groupValues?.get(1)?.trim()
        val phone = phoneRegex.find(vCard)?.groupValues?.get(1)?.trim().isNullOrBlank().let {
            "No phone number found"
        }


        return Pair(name, phone)
    }
}

