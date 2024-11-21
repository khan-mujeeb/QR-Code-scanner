package com.khandev.qrcodescanner.utlis

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.webkit.URLUtil
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.khandev.qrcodescanner.data.UpiData
import com.khandev.qrcodescanner.data.WifiConfig
import com.khandev.qrcodescanner.utlis.QrCodeParser.parseUpiQrCode

object ScannerUtils {

    // function to copt text to clipboard
    fun copyTextToClipboard(context: Context, text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", text)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    // function to get the category of the input
    fun getCateogory(input: String): String {

        val temp = input.toLowerCase()

        return if (temp.contains("vcard")) {
            Categories.CONTACT
        } else if (temp.contains("wifi")) {
            Categories.WIFI
        } else if (temp.contains("upi")) {
            Categories.PAYMENT
        } else {
            if (URLUtil.isValidUrl(input)) "url" else "text"
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectToWifi(context: Context, wifiConfig: WifiConfig) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Create a WifiNetworkSuggestion
        val suggestion = WifiNetworkSuggestion.Builder()
            .setSsid(wifiConfig.ssid)
            .apply {
                if (!wifiConfig.password.isNullOrEmpty()) {
                    setWpa2Passphrase(wifiConfig.password)
                }
            }
            .build()

        // Add the suggestion to the device
        val suggestions = listOf(suggestion)
        val status = wifiManager.addNetworkSuggestions(suggestions)

        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            Toast.makeText(context, "Failed to suggest Wi-Fi network", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Wi-Fi network suggested successfully", Toast.LENGTH_SHORT)
                .show()
        }
    }


    // *********************************************************************************************
    //                                  function to open upi app
    // *********************************************************************************************
    @SuppressLint("QueryPermissionsNeeded")
    fun openUpiApp(context: Context, upiData: UpiData) {
        // Construct the UPI URI
        val upiUri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", upiData.upiId)
            .appendQueryParameter("pn", upiData.payeeName)
            .appendQueryParameter("am", upiData.amount)
            .appendQueryParameter("cu", upiData.currency)
            .appendQueryParameter("tn", upiData.transactionNote)
            .appendQueryParameter("tr", upiData.transactionRefId)
            .appendQueryParameter("mc", upiData.merchantCode)
            .appendQueryParameter("url", upiData.url)
            .build()

        // Create an intent to open UPI apps
        val upiIntent = Intent(Intent.ACTION_VIEW).apply {
            data = upiUri
        }

        // Verify if there are apps to handle the intent
        if (upiIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(upiIntent)
        } else {
            Toast.makeText(context, "Unable to make payment ", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleUpiPayment(context: Context, upiData: UpiData) {

        if (upiData.upiId.isNullOrEmpty() ) {
            Toast.makeText(context, "Invalid UPI QR Code", Toast.LENGTH_SHORT).show()
            return
        }

        openUpiApp(context, upiData)
    }



}