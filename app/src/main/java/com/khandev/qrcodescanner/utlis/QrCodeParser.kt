package com.khandev.qrcodescanner.utlis

import android.net.Uri
import com.khandev.qrcodescanner.data.UpiData
import com.khandev.qrcodescanner.data.WifiConfig

object QrCodeParser {


    //    *******************************************************************************************
    //                                  function to parse upi data
    //    *******************************************************************************************
    fun parseUpiQrCode(upiData: String): UpiData {
        val uri = Uri.parse(upiData)

        return UpiData(
            upiId = uri.getQueryParameter("pa"),
            payeeName = uri.getQueryParameter("pn"),
            amount = uri.getQueryParameter("am"),
            currency = uri.getQueryParameter("cu"),
            transactionNote = uri.getQueryParameter("tn"),
            transactionRefId = uri.getQueryParameter("tr"),
            merchantCode = uri.getQueryParameter("mc"),
            url = uri.getQueryParameter("url")
        )
    }

    //    *******************************************************************************************
    //                                  function to check if the upi data is valid
    //    *******************************************************************************************
    fun isValidUpiQrCode(upiData: String): Boolean {
        val uri = Uri.parse(upiData)
        return uri.scheme == "upi" && uri.authority == "pay"
    }



    // **********************************************************************************************
    //                                 function to parse wifi qr code
    //    **********************************************************************************************
    fun parseWifiQRCode(qrCodeContent: String): WifiConfig? {
        if (!qrCodeContent.startsWith("WIFI:")) return null

        val params = qrCodeContent.removePrefix("WIFI:")
            .split(";")
            .filter { it.isNotEmpty() }
            .associate {
                val keyValue = it.split(":")
                keyValue[0] to keyValue.getOrElse(1) { "" }
            }

        val ssid = params["S"] ?: return null
        val password = params["P"]
        val encryption = params["T"]
        val hidden = params["H"]?.toBoolean() ?: false

        return WifiConfig(ssid, password, encryption, hidden)
    }


//**************************************************************************************************
    // function to extract contact info from vcard string and return as a map
//    **********************************************************************************************
    fun parseVCard(vCardString: String): Map<String, String> {

        val lines = vCardString.split("\n")
        val vCardDataMap = mutableMapOf<String, String>()

        for (line in lines) {
            when {
                line.startsWith("FN:") -> vCardDataMap["Full Name"] = line.substringAfter("FN:").trim()
                line.startsWith("ORG:") -> vCardDataMap["Organization"] = line.substringAfter("ORG:").trim()
                line.startsWith("TITLE:") -> vCardDataMap["Title"] = line.substringAfter("TITLE:").trim()
                line.startsWith("TEL;WORK;VOICE:") -> vCardDataMap["Phone No."] = line.substringAfter("TEL;WORK;VOICE:").trim()
                line.startsWith("TEL;CELL:") -> vCardDataMap["Mobile No."] = line.substringAfter("TEL;CELL:").trim()
                line.startsWith("EMAIL;WORK;INTERNET:") -> vCardDataMap["Email"] = line.substringAfter("EMAIL;WORK;INTERNET:").trim()
                line.startsWith("URL:") -> vCardDataMap["website"] = line.substringAfter("URL:").trim()
//                line.startsWith("ADR:") -> vCardDataMap["Address"] = line.substringAfter("ADR:").trim()
                line.startsWith("TEL;FAX:") -> vCardDataMap["Fax"] = line.substringAfter("TEL;FAX:").trim()

                // Handle the ADR (Address) field
                line.startsWith("ADR:") -> {
                    val addressParts = line.substringAfter("ADR:").split(";").map { it.trim() }
                    val addressMap = mutableMapOf(
                        "Street" to (addressParts.getOrNull(2) ?: ""),
                        "City" to (addressParts.getOrNull(3) ?: ""),
                        "State" to (addressParts.getOrNull(4) ?: ""),
                        "Postal Code" to (addressParts.getOrNull(5) ?: ""),
                        "Country" to (addressParts.getOrNull(6) ?: "")
                    )
                    // Combine address parts into a human-readable format
                    vCardDataMap["Address"] = addressMap.entries
                        .filter { it.value.isNotBlank() } // Exclude empty values
                        .joinToString(", ") { it.value }
                }


            }
        }


//        phoneNumber = if (vCardDataMap["Phone No."]!!.isNotEmpty()) {
//            vCardDataMap["Phone No."]!!
//        } else {
//            vCardDataMap["Mobile No."]!!
//        }

        return vCardDataMap
    }
}