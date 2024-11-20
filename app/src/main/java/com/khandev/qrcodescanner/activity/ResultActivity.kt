package com.khandev.qrcodescanner.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.khandev.qrcodescanner.R
import com.khandev.qrcodescanner.adapter.VCardDataAdapter
import com.khandev.qrcodescanner.data.VCardData
import com.khandev.qrcodescanner.database.data.QrCodeEntity
import com.khandev.qrcodescanner.database.viewmodel.DBViewModle
import com.khandev.qrcodescanner.databinding.ActivityResultBinding
import com.khandev.qrcodescanner.utlis.ScannerUtils.copyTextToClipboard


class ResultActivity : AppCompatActivity() {
    private lateinit var viewMole: DBViewModle
    private lateinit var binding: ActivityResultBinding
    private var vcardResult: Map<String, String>? = null
    private var cat = ""
    private var result = ""
    private var count = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        variableInit()

    }

    override fun onStart() {
        super.onStart()
        subscribeUi()
        subscribeClickEvents()
        if (count == 0) {
            addToDb()
            count++
        }
    }

    private fun addToDb() {
        viewMole.insert(
            QrCodeEntity(
                qrcodeData = result,
                category = cat
            )
        )
    }

    @SuppressLint("SuspiciousIndentation")
    private fun subscribeClickEvents() {

        binding.btn.setOnClickListener {
            if (cat == "url") {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result))
                startActivity(intent)

            } else {
                copyTextToClipboard(this@ResultActivity, result)
                Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()

            }
        }

        binding.share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, result)
            startActivity(Intent.createChooser(intent, "Share via"))
        }

        binding.result.setOnLongClickListener {
            copyTextToClipboard(this, result)
            Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()
            true
        }
    }


    @SuppressLint("SetTextI18n")
    private fun subscribeUi() {
        backPressed()
        displayBody()
        if(result != "contact") {
            binding.result.text = result
        } else {
            binding.ResultRc.adapter = VCardDataAdapter(vcardResult!!)
        }
    }

    private fun backPressed() {

        val toolbar = binding.toolbar

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun displayBody() {
        if (cat == "contact") {
            setUpContactData()
        } else if (cat == "url") {
            setUpUrlData()
        } else {
            setUpPlainTextData()
        }
    }

    private fun setUpContactData() {
        binding.apply {
            logo.setImageDrawable(resources.getDrawable(R.drawable.baseline_contact_phone_24))
            category.text = getString(R.string.contact)
            btn.text = getString(R.string.copy_text)
//            result.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.black))
            result.visibility = android.view.View.GONE
            ResultRc.visibility = android.view.View.VISIBLE

            ResultRc.adapter = VCardDataAdapter(vcardResult!!)
        }
    }

    private fun setUpPlainTextData() {
        binding.apply {
            logo.setImageDrawable(resources.getDrawable(R.drawable.plain_text))
            category.text = getString(R.string.qr_code)
            btn.text = getString(R.string.copy_text)
            result.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.black))
            result.visibility = android.view.View.VISIBLE
            ResultRc.visibility = android.view.View.GONE
        }


    }

    private fun setUpUrlData() {
        binding.apply {
            logo.setImageDrawable(resources.getDrawable(R.drawable.browser_new))
            category.text = getString(R.string.website)
            btn.text = getString(R.string.go_to_website)
            result.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.midnight_blue))
            result.visibility = android.view.View.VISIBLE
            ResultRc.visibility = android.view.View.GONE
        }
    }

    private fun variableInit() {
        count = intent.getIntExtra("count", -1)!!
        result = intent.getStringExtra("result")!!
        cat = getCateogory(result)
        viewMole = ViewModelProvider(this)[DBViewModle::class.java]

        if (cat == "contact") {
            vcardResult = parseVCard(result)
        }

    }

     private fun getCateogory(input: String): String {

         val temp = input.toLowerCase()

         return if (temp.contains("vcard")) {
             "contact"
         } else {
             if(URLUtil.isValidUrl(input)) "url" else "text"
         }
    }

    private fun parseVCard(vCardString: String): Map<String, String> {

        Log.d("VCard", vCardString)
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

        return vCardDataMap
    }
}